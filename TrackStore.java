import java.time.*;
import java.util.*;

public class TrackStore {
  public final HashSet<Track> tracks;
  public final LocalDateTime lastChange;
  public TrackStore() {
    tracks = new HashSet();
    lastChange = LocalDateTime.now();
  }
  public Hash getHash() { return new Hash(tracks.hashCode()); }
  public boolean insert(Track t) {
    boolean b = tracks.insert(t);
    if (b) {
      lastChange = LocalDateTime.now();
    }
    return b;
  }
  public void printContents() {
    for (Track t : tracks) {
      System.out.println(t);
    }
  }
}
