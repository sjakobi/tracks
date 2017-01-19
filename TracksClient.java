import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

public class TracksClient {
  public static final int[] remotePorts = new int[] {
      50001, 50002, 50003, 50004, 50005, 50006, 50007, 50008, 50009, 50010};
  public static final int minTimeout = 200;  // ms
  public static final int maxTimeout = 1500; // ms

  public final int port;
  public final TrackStore store;
  public final DatagramSocket socket;

  public static void main(String[] args) throws IOException {
    String dateiname = null;
    int port = 0;

    if (args.length == 2) {
      dateiname = args[0];
      port = Integer.parseInt(args[1]);
    } else {
      System.err.println("Verwendung: java TracksClient <dateiname> <port>");
      System.exit(1);
    }
    TrackStore store = new TrackStore();
    List<String> lines =
        Files.readAllLines(FileSystems.getDefault().getPath(".", dateiname),
                           Charset.defaultCharset());
    for (String s : lines) {
      store.add(Track.fromString(s));
    }
    try {
      TracksClient client = new TracksClient(port, store);
      client.run();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public TracksClient(int port, TrackStore store) throws IOException {
    this.store = store;
    this.port = port;
    socket = new DatagramSocket(port);
  }

  public void setRandomTimeout(DatagramSocket socket) throws SocketException {
    int randomTimeout =
        ThreadLocalRandom.current().nextInt(minTimeout, maxTimeout + 1);
    socket.setSoTimeout(randomTimeout);
  }
  public void sendIHaves() throws IOException {
    IHave ihave = new IHave(store.getHash());
    for (int remotePort : remotePorts) {
      if (remotePort != port) {
        sendMessage(ihave, remotePort);
      }
    }
  }

  public void sendMessage(Message msg, int remotePort) throws IOException {
    byte[] bytes = msg.toBytes();
    DatagramPacket outPacket = new DatagramPacket(
        bytes, bytes.length, new InetSocketAddress(remotePort));
    socket.send(outPacket);
  }

  public Optional<Message> decodeBytes(byte[] bytes) {
    List<Function<byte[], Optional<Message>>> decoders = Arrays.asList(
        IHave::fromBytes, WhatHaveYou::fromBytes, Summary::fromBytes);
    return decoders.stream()
        .map(x -> x.apply(bytes))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }

  public void handleMessage(byte[] bytes, int remotePort) throws IOException {
    System.out.println("Msg received from Port " + remotePort + ": " +
                       Arrays.toString(Arrays.copyOf(bytes, 5)) + "...");

    Message msg = decodeBytes(bytes).orElse(null);
    if (msg != null) {
      if ((msg instanceof IHave) &&
          !((IHave)msg).hash.equals(store.getHash())) {
        sendMessage(new WhatHaveYou(), remotePort);
      } else if (msg instanceof WhatHaveYou) {
        sendMessage(new Summary(store.getTrackHashes()), remotePort);
      } else if (msg instanceof Summary) {
        // request what I don't have yet
      }
    }

    System.out.println("Decoded: " + msg);
  }

  public void run() throws IOException, SocketException {
    System.out.println("Ich habe folgende Stücke:");
    for (Track track : store.tracks) {
      System.out.println("    " + track);
    }
    while (true) {
      setRandomTimeout(socket);

      // Accept Answers
      byte[] buffer = new byte[2048];
      DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
      try {
        socket.receive(inPacket);
        byte[] inMsg = inPacket.getData();
        handleMessage(inMsg, inPacket.getPort());
      } catch (SocketTimeoutException ste) {
        System.out.println("No message");
        sendIHaves();
      }
    }
  }
}
