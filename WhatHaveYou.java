import java.nio.*;
import java.util.*;
import java.nio.charset.*;

public class WhatHaveYou extends Message {
  public static byte[] marker = "WHVY".getBytes(StandardCharsets.US_ASCII);
  public byte[] toBytes() { return marker; }
  public static Optional<WhatHaveYou> fromBytes(byte[] bytes) {
    return (Arrays.equals(Arrays.copyOf(bytes, 4), marker))
        ? Optional.of(new WhatHaveYou())
        : Optional.empty();
  }

  public boolean equals(Object other) { return (other instanceof WhatHaveYou); }
}
