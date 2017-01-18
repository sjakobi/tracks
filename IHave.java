import java.nio.*;
import java.util.*;
import java.nio.charset.*;

public class IHave extends Message {
  public Hash hash;
  public IHave(Hash hash) { this.hash = hash; }
  public static byte[] marker = "IHAV".getBytes(StandardCharsets.US_ASCII);
  public byte[] toBytes() {
    byte[] hb = hash.toBytes();
    ByteBuffer bb = ByteBuffer.allocate(marker.length + hb.length);
    bb.put(marker);
    bb.put(hb);
    return bb.array();
  }
  public static Optional<IHave> fromBytes(byte[] bytes) {
    return (Arrays.equals(Arrays.copyOf(bytes, 4), marker) && bytes.length > 4)
        ? Hash.fromBytes(Arrays.copyOfRange(bytes, 4, 8)).map(IHave::new)
        : Optional.empty();
  }

  public boolean equals(Object other) {
    return (other instanceof IHave) && ((IHave)other).hash.equals(this.hash);
  }

  public static void main(String[] args) {
    IHave x = new IHave(new Hash(17));
    System.out.println(IHave.fromBytes(x.toBytes()).equals(Optional.of(x)));
  }
}
