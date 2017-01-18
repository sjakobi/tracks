import java.util.*;
abstract class Message { public abstract <T extends Message> byte[] toBytes(); }
