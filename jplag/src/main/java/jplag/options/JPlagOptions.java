package jplag.options;

public class JPlagOptions {

  private Language language;

  private Verbosity verbosity;

  public static JPlagOptions fromArgs(String[] args) {
    return new JPlagOptions();
  }

  public Language getLanguage() {
    return language;
  }

  public Verbosity getVerbosity() {
    return verbosity;
  }
}
