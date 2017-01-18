import java.util.Scanner;

public class MusikStueck {
  public final String interpret;
  public final String titel;

  public MusikStueck(String interpret, String titel) {
    this.interpret = interpret;
    this.titel = titel;
  }

  public String toString() {
    return interpret + "|" + titel;
  }

  public static MusikStueck fromString(String input) {
    Scanner scanner = new Scanner(input);
    scanner.useDelimiter("|");
    String interpret = scanner.next();
    String titel = scanner.next();
    return new MusikStueck(interpret, titel);
  }

  public int hashCode() {
    int result = 123;
    result = 37 * result + interpret.hashCode();
    result = 37 * result + titel.hashCode();
    return result;
  }
}
