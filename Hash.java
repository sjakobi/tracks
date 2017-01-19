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
  public boolean equals(Object other) {
    return (other instanceof Hash) && ((Hash)other).hash == this.hash;
  }

  public int hashCode() { return hash; }

  public String toString() { return "Hash{" + hash + "}"; }
}
