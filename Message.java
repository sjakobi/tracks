import java.util.*;

/**
 * A message that can be serialised in order to be sent over the wire.
 */
abstract class Message {
  /**
   * Serialise the message.
   */
  public abstract <T extends Message> byte[] toBytes();
}
