package jplag.options;

/**
 * The available languages.
 */
public enum Language {
  JAVA_1_1("jplag.javax.Language"),
  JAVA_1_2("jplag.java.Language"),
  JAVA_1_5("jplag.java15.Language"),
  JAVA_1_5_DM("jplag.java15.LanguageWithDelimitedMethods"),
  JAVA_1_7("jplag.java17.Language"),
  JAVA_1_9("jplag.java19.Language"),
  PYTHON_3("jplag.python3.Language"),
  C_CPP("jplag.cpp.Language"),
  C_SHARP("jplag.csharp.Language"),
  CHAR("jplag.chars.Language"),
  TEXT("jplag.text.Language"),
  SCHEME("jplag.scheme.Language");

  private final String className;

  Language(String className) {
    this.className = className;
  }

  public String getClassName() {
    return this.className;
  }
}
