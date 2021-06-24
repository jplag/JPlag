package jplag.options;

import java.util.Arrays;

/**
 * The available languages.
 */
public enum LanguageOption {
    JAVA_1_1("java1", "jplag.javax.Language"),
    JAVA_1_2("java2", "jplag.java.Language"),
    JAVA_1_5("java5", "jplag.java15.Language"),
    JAVA_1_5_DM("java5dm", "jplag.java15.LanguageWithDelimitedMethods"),
    JAVA_1_7("java7", "jplag.java17.Language"),
    JAVA_1_9("java9", "jplag.java19.Language"),
    PYTHON_3("python3", "jplag.python3.Language"),
    C_CPP("cpp", "jplag.cpp.Language"),
    C_SHARP("csharp", "jplag.csharp.Language"),
    CHAR("char", "jplag.chars.Language"),
    TEXT("text", "jplag.text.Language"),
    SCHEME("scheme", "jplag.scheme.Language");

    private final String classPath;
    private final String displayName;

    LanguageOption(String displayName, String classPath) {
        this.displayName = displayName;
        this.classPath = classPath;
    }

    public String getClassPath() {
        return this.classPath;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static LanguageOption fromDisplayName(String displayName) {
        return Arrays.stream(LanguageOption.values())
                .filter(languageOption -> languageOption.displayName.equals(displayName))
                .findFirst()
                .orElse(getDefault());
    }

    public static String[] getAllDisplayNames() {
        return Arrays.stream(LanguageOption.values())
                .map(languageOption -> languageOption.displayName)
                .toArray(String[]::new);
    }

    public static LanguageOption getDefault() {
        return LanguageOption.JAVA_1_9;
    }
}
