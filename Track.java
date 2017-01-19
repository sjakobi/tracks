import java.util.*;
import java.nio.charset.*;

public class Track {
  public final String artist;
  public final String title;

  public Track(String artist, String title) {
    this.artist = artist;
    this.title = title;
  }

  public String toString() { return artist + "|" + title; }

  public static Track fromString(String input) {
    String[] parts = input.split("\\|");
    String artist = parts[0];
    String title = parts[1];
    return new Track(artist, title);
  }

  @Override
  public int hashCode() {
    int result = 123;
    result = 37 * result + artist.hashCode();
    result = 37 * result + title.hashCode();
    return result;
  }

  public Hash getHash() { return new Hash(hashCode()); }

  @Override
  public boolean equals(Object other) {
    if (other == null)
      return false;
    if (other == this)
      return true;
    if (!(other instanceof Track))
      return false;
    Track otherTrack = (Track)other;
    return this.artist.equals(otherTrack.artist) &&
        this.title.equals(otherTrack.title);
  }

  public byte[] toBytes() {
    return toString().getBytes(StandardCharsets.UTF_8);
  }

  public static Optional<Track> fromBytes(byte[] bytes) {
    return Optional.of(fromString(new String(bytes, StandardCharsets.UTF_8)));
  }

  public static void main(String[] args) {
    Track t = Track.fromString("bli|bla");
    System.out.println(t.artist);
    System.out.println(t.title);
    System.out.println(t);
    Track u = Track.fromString("bli|bla");
    System.out.println(t.equals(u));
  }
}
