import java.time.*;
import java.util.*;

public class TrackStore {
  public final HashSet<Track> tracks;
  public Instant lastChange;
  public TrackStore() {
    tracks = new HashSet<Track>();
    lastChange = Instant.now();
  }
  public Hash getHash() { return new Hash(tracks.hashCode()); }
  public boolean add(Track t) {
    boolean b = tracks.add(t);
    if (b) {
      lastChange = Instant.now();
    }
    return b;
  }
  public void printContents() {
    for (Track t : tracks) {
      System.out.println(t);
    }
  }
  public Set<Hash> getTrackHashes() {
    Set<Hash> l = new HashSet<>(tracks.size());
    for (Track t : tracks) {
      l.add(t.getHash());
    }
    return l;
  }
}
