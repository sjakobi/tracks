import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class TracksClient {
  public static final int[] remotePorts = new int[] {
      50001, 50002, 50003, 50004, 50005, 50006, 50007, 50008, 50009, 50010};
  public static final int minTimeout = 200;  // ms
  public static final int maxTimeout = 1500; // ms

  public final int port;
  public final TrackStore store;

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
  }

  public void setRandomTimeout(DatagramSocket socket) throws SocketException {
    int randomTimeout =
        ThreadLocalRandom.current().nextInt(minTimeout, maxTimeout + 1);
    socket.setSoTimeout(randomTimeout);
  }

  public void run() throws IOException, SocketException {
    System.out.println("Ich habe folgende Stücke:");
    for (Track track : store.tracks) {
      System.out.println("    " + track);
    }
    DatagramSocket socket = new DatagramSocket(port);
    setRandomTimeout(socket);
    while (true) {
      /*
for (int remotePort : remotePorts) {
  if (remotePort != port) {
    // Send IHAVE
    String ihave = ihave();
    byte[] ihaveBytes = ihave.getBytes();
    DatagramPacket outPacket = new DatagramPacket(
        ihaveBytes, ihaveBytes.length, new InetSocketAddress(remotePort));
    socket.send(outPacket);

    // Accept Answers
    byte[] buffer = new byte[2048];
    DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
    try {
      socket.receive(inPacket);
    } catch (SocketTimeoutException ste) {
      continue;
    }
    String inMsg = inPacket.getData();
    if (inMsg.startsWith("IHAVE")) {
      if (inMsg.equals(ihave)) {
        continue;
      }
    }
  }
}
  */
    }
  }
}
