package jplag.options;

/**
 * The available languages.
 */
public enum LanguageOption {
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

    private final String classPath;

    LanguageOption(String classPath) {
        this.classPath = classPath;
    }

    public String getClassPath() {
        return this.classPath;
    }

    public static LanguageOption fromOption(String optionName) {
        switch(optionName) {
            case "java_1_1": return JAVA_1_1;
            case "java_1_2": return JAVA_1_2;
            case "java_1_5": return JAVA_1_5;
            case "java_1_5_dm": return JAVA_1_5_DM;
            case "java_1_7": return JAVA_1_7;
            case "java_1_9": return JAVA_1_9;
            case "python_3": return PYTHON_3;
            case "c_cpp": return C_CPP;
            case "c_sharp": return C_SHARP;
            case "char": return CHAR;
            case "text": return TEXT;
            case "scheme": return SCHEME;
            default: return JAVA_1_9;
        }
    }
}
