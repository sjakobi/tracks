import java.nio.*;
import java.util.*;
import java.nio.charset.*;

public class RequestTrack extends Message {
  public Hash hash;
  public RequestTrack(Hash hash) { this.hash = hash; }
  public static byte[] marker = "RQST".getBytes(StandardCharsets.US_ASCII);
  public byte[] toBytes() {
    byte[] hb = hash.toBytes();
    ByteBuffer bb = ByteBuffer.allocate(marker.length + hb.length);
    bb.put(marker);
    bb.put(hb);
    return bb.array();
  }
  public static Optional<Message> fromBytes(byte[] bytes) {
    return (Arrays.equals(Arrays.copyOf(bytes, 4), marker) && bytes.length > 4)
        ? Hash.fromBytes(Arrays.copyOfRange(bytes, 4, 8)).map(RequestTrack::new)
        : Optional.empty();
  }

  public boolean equals(Object other) {
    return (other instanceof RequestTrack) &&
        ((RequestTrack)other).hash.equals(this.hash);
  }

  public static void main(String[] args) {
    RequestTrack x = new RequestTrack(new Hash(17));
    System.out.println(
        RequestTrack.fromBytes(x.toBytes()).equals(Optional.of(x)));
  }
}
