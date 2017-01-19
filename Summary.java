import java.util.*;
import java.nio.*;
import java.nio.charset.*;

public class Summary extends Message {
  public static byte[] marker = "SMRY".getBytes(StandardCharsets.US_ASCII);
  public final List<Hash> trackHashes;
  public Summary(List<Hash> trackHashes) { this.trackHashes = trackHashes; }
  public byte[] toBytes() {
    ByteBuffer bb =
        ByteBuffer.allocate(marker.length + 4 + trackHashes.size() * 4);
    bb.put(marker);
    bb.putInt(trackHashes.size());
    for (Hash h : trackHashes) {
      bb.put(h.toBytes());
    }
    return bb.array();
  }
  public static Optional<Message> fromBytes(byte[] bytes) {
    if (Arrays.equals(Arrays.copyOf(bytes, 4), marker) && bytes.length >= 8) {
      ByteBuffer bb = ByteBuffer.wrap(bytes);
      bb.getInt(); // marker
      int len = bb.getInt();
      ArrayList<Hash> hashes = new ArrayList<>(len);
      for (int i = 0; i < len; i++) {
        hashes.add(new Hash(bb.getInt()));
      }
      return Optional.of(new Summary(hashes));
    } else {
      return Optional.empty();
    }
  }

  public boolean equals(Object other) {
    return (other instanceof Summary) &&
        ((Summary)other).trackHashes.equals(this.trackHashes);
  }

  public static void main(String[] args) {
    Summary x = new Summary(Arrays.asList(new Hash(1), new Hash(2)));
    Summary decoded = (Summary)Summary.fromBytes(x.toBytes()).get();
    System.out.println(decoded);
    System.out.println(decoded.trackHashes);
    System.out.println(x.equals(decoded));
  }
}
