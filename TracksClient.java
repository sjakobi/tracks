import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class P2PClient {
  public static final int[] remotePorts = new int[] {
      50001, 50002, 50003, 50004, 50005, 50006, 50007, 50008, 50009, 50010};
  public static final int timeout = 3; // ms

  public final String dateiname;
  public final int port;
  public final Set<MusikStueck> stuecke;

  public static void main(String[] args) {
    String dateiname = null;
    int port = 0;

    if (args.length == 2) {
      dateiname = args[0];
      port = Integer.parseInt(args[1]);
    } else {
      System.err.println("Verwendung: java P2PClient <dateiname> <port>");
      System.exit(1);
    }
    try {
      P2PClient client = new P2PClient(dateiname, port);
      client.run();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public P2PClient(String dateiname, int port) throws IOException {
    Set<MusikStueck> stuecke = new HashSet<>();
    List<String> zeilen =
        Files.readAllLines(FileSystems.getDefault().getPath(".", dateiname),
                           Charset.defaultCharset());
    for (String s : zeilen) {
      stuecke.add(MusikStueck.fromString(s));
    }
    this.stuecke = stuecke;
    this.dateiname = dateiname;
    this.port = port;
  }

  public void run() throws IOException, SocketException {
    System.out.println("Ich habe folgende Stücke:");
    for (MusikStueck stueck : stuecke) {
      System.out.println("    " + stueck);
    }
    DatagramSocket socket = new DatagramSocket(port);
    socket.setSoTimeout(timeout);
    while (true) {
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
            } else {
            }
          }
        }
      }
      public String ihave() {
        return "IHAVE" + stuecke.size() + "|" + stuecke.hashCode();
      }
    }
