import java.nio.*;
import java.util.*;

public class Hash {
  public int hash;
  public Hash(int n) { hash = n; }
  public byte[] toBytes() {
    return ByteBuffer.allocate(4).putInt(hash).array();
  }
  public static Optional<Hash> fromBytes(byte[] bytes) {
    if (bytes.length == 4) {
      return Optional.of(new Hash(ByteBuffer.wrap(bytes).getInt()));
    } else {
      return Optional.empty();
    }
  }
}
