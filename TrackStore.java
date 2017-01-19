import java.time.*;
import java.util.*;

/**
 * A datastore for music tracks, keeping track of the last change.
 */
public class TrackStore {
  public final HashSet<Track> tracks;
  public Instant lastChange;
  public TrackStore() {
    tracks = new HashSet<Track>();
    lastChange = Instant.now();
  }

  /**
   * Hash ID for the stored tracks.
   */
  public Hash getHash() { return new Hash(tracks.hashCode()); }

  /**
   * Add a track, returning true, if the track wasn't stored before.
   */
  public boolean add(Track t) {
    boolean b = tracks.add(t);
    if (b) {
      lastChange = Instant.now();
    }
    return b;
  }

  /**
   * Output the stored tracks to stdout.
   */
  public void printContents() {
    for (Track t : tracks) {
      System.out.println(t);
    }
  }

  /**
   * Get the Hash IDs for the stored tracks.
   */
  public Set<Hash> getTrackHashes() {
    Set<Hash> l = new HashSet<>(tracks.size());
    for (Track t : tracks) {
      l.add(t.getHash());
    }
    return l;
  }
}
