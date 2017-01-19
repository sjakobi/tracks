import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.function.*;

/**
 * A P2P client for sharing music tracks.
 */
public class TracksClient {
  public static final int[] remotePorts = new int[] {
      50001, 50002, 50003, 50004, 50005, 50006, 50007, 50008, 50009, 50010};
  public static final int minTimeout = 200; // ms
  public static final int maxTimeout = 800; // ms

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
      System.err.println("Usage: java TracksClient <dateiname> <port>");
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

  /**
   * Set a random timeout between minTimeout and maxTimeout for listening on the
   * port.
   */
  public void setRandomTimeout(DatagramSocket socket) throws SocketException {
    int randomTimeout =
        ThreadLocalRandom.current().nextInt(minTimeout, maxTimeout + 1);
    socket.setSoTimeout(randomTimeout);
  }

  /**
   * Send an IHave message to every potential peer.
   */
  public void sendIHaves() throws IOException {
    IHave ihave = new IHave(store.getHash());
    for (int remotePort : remotePorts) {
      if (remotePort != port) {
        sendMessage(ihave, remotePort);
      }
    }
  }

  /**
   * Send a message to the given port.
   */
  public void sendMessage(Message msg, int remotePort) throws IOException {
    System.out.println("Sending message to port " + remotePort + ": " + msg);
    byte[] bytes = msg.toBytes();
    DatagramPacket outPacket = new DatagramPacket(
        bytes, bytes.length, new InetSocketAddress(remotePort));
    socket.send(outPacket);
  }

  /**
   * Decode a received message.
   */
  public Optional<Message> decodeBytes(byte[] bytes) {
    List<Function<byte[], Optional<Message>>> decoders = Arrays.asList(
        IHave::fromBytes, WhatHaveYou::fromBytes, Summary::fromBytes,
        RequestTrack::fromBytes, SendTrack::fromBytes);
    return decoders.stream()
        .map(x -> x.apply(bytes))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }

  /**
   * React to a received message. This method contains the main protocol logic.
   */
  public void handleMessage(byte[] bytes, int remotePort) throws IOException {
    System.out.print("Message received from Port " + remotePort + ": ");
    Message msg = decodeBytes(bytes).orElse(null);

    if (msg != null) {
      System.out.println(msg);
      if (msg instanceof IHave) {
        if (!((IHave)msg).hash.equals(store.getHash())) {
          sendMessage(new WhatHaveYou(), remotePort);
        }
      } else if (msg instanceof WhatHaveYou) {
        sendMessage(new Summary(store.getTrackHashes()), remotePort);
      } else if (msg instanceof Summary) {
        Summary s = (Summary)msg;
        Set<Hash> trackHashes = store.getTrackHashes();
        for (Hash h : s.trackHashes) {
          if (!trackHashes.contains(h)) {
            sendMessage(new RequestTrack(h), remotePort);
            break;
          }
        }
      } else if (msg instanceof RequestTrack) {
        RequestTrack r = (RequestTrack)msg;
        Hash requestedHash = r.hash;
        for (Track t : store.tracks) {
          if (t.getHash().equals(requestedHash)) {
            sendMessage(new SendTrack(t), remotePort);
            break;
          }
        }
      } else if (msg instanceof SendTrack) {
        SendTrack s = (SendTrack)msg;
        store.add(s.track);
      } else {
        System.out.println("Not sure what this message is: " + msg);
      }
    }
  }

  public void quitIfNoRecentUpdate() {
    Instant now = Instant.now();
    if (Duration.between(store.lastChange, now).toMillis() > 10000L) {
      System.out.println("No update in the last 10s. I have:");
      store.printContents();
      System.exit(0);
    }
  }

  /**
   * Main loop.
   */
  public void run() throws IOException, SocketException {
    System.out.println("I have the following tracks:");
    store.printContents();

    while (true) {
      quitIfNoRecentUpdate();
      setRandomTimeout(socket);

      byte[] buffer = new byte[2048];
      DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
      try {
        socket.receive(inPacket);
        byte[] inMsg = inPacket.getData();
        handleMessage(inMsg, inPacket.getPort());
      } catch (SocketTimeoutException ste) {
        System.out.println("Received no message");
        sendIHaves();
      }
    }
  }
}
