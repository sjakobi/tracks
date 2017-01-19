import java.nio.*;
import java.util.*;
import java.nio.charset.*;

/**
 * Message that contains a Track.
 */
public class SendTrack extends Message {
  public Track track;
  public SendTrack(Track track) { this.track = track; }
  public static byte[] marker = "SDTK".getBytes(StandardCharsets.US_ASCII);
  public byte[] toBytes() {
    byte[] tb = track.toBytes();
    ByteBuffer bb = ByteBuffer.allocate(marker.length + 4 + tb.length);
    bb.put(marker);
    bb.putInt(tb.length);
    bb.put(tb);
    return bb.array();
  }
  public static Optional<Message> fromBytes(byte[] bytes) {
    if (Arrays.equals(Arrays.copyOf(bytes, 4), marker) && bytes.length >= 8) {
      ByteBuffer bb = ByteBuffer.wrap(bytes);
      bb.getInt(); // marker
      int len = bb.getInt();
      byte[] trackBytes = new byte[len];
      bb.get(trackBytes, 0, len);
      return Track.fromBytes(trackBytes).map(SendTrack::new);
    } else {
      return Optional.empty();
    }
  }

  public boolean equals(Object other) {
    return (other instanceof SendTrack) &&
        ((SendTrack)other).track.equals(this.track);
  }

  public static void main(String[] args) {
    SendTrack x = new SendTrack(Track.fromString("bli|bla"));
    System.out.println(SendTrack.fromBytes(x.toBytes()).equals(Optional.of(x)));
  }
}
