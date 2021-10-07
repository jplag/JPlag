package de.jplag.options;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collection;

/**
 * The available languages.
 */
public enum LanguageOption {
    JAVA_1_1("java1", "de.jplag.javax.Language"),
    JAVA_1_2("java2", "de.jplag.java.Language"),
    JAVA_1_5("java5", "de.jplag.java15.Language"),
    JAVA_1_5_DM("java5dm", "de.jplag.java15.LanguageWithDelimitedMethods"),
    JAVA_1_7("java7", "de.jplag.java17.Language"),
    JAVA_1_9("java9", "de.jplag.java19.Language"),
    PYTHON_3("python3", "de.jplag.python3.Language"),
    C_CPP("cpp", "de.jplag.cpp.Language"),
    C_SHARP("csharp", "de.jplag.csharp.Language"),
    CHAR("char", "de.jplag.chars.Language"),
    TEXT("text", "de.jplag.text.Language"),
    SCHEME("scheme", "de.jplag.scheme.Language");

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
        return Arrays.stream(LanguageOption.values()).filter(languageOption -> languageOption.displayName.equals(displayName)).findFirst()
                .orElse(getDefault());
    }

    public static Collection<String> getAllDisplayNames() {
        return Arrays.stream(LanguageOption.values()).map(languageOption -> languageOption.displayName).collect(toList());
    }

    public static LanguageOption getDefault() {
        return LanguageOption.JAVA_1_9;
    }
}
