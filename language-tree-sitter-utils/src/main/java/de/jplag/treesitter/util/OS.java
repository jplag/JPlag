package de.jplag.treesitter.util;

import java.util.Locale;

public final class OS {
    private static final String UNKNOWN_OS = "UNKNOWN";
    private static final String UNKNOWN_ARCHITECTURE = "UNKNOWN";

    private static final String OS_MAC = "mac";
    private static final String OS_LINUX = "linux";
    private static final String OS_WINDOWS = "windows";

    private OS() {
    }

    public static String name() {
        String os = osNameProperty();
        if (os.contains("mac os x") || os.contains("darwin") || os.contains("osx")) {
            return OS_MAC;
        } else if (os.contains(OS_LINUX)) {
            return OS_LINUX;
        } else if (os.contains(OS_WINDOWS)) {
            return OS_WINDOWS;
        } else {
            throw new RuntimeException("OS " + os + " is not supported");
        }
    }

    public static String architecture() {
        String architecture = osArchitectureProperty();
        return switch (architecture) {
            case "x86" -> "i386";
            case "x86_64", "amd64" -> "amd64";
            case "aarch64", "arm64" -> "aarch64";
            case "arm" -> "arm";
            case "powerpc" -> "ppc";
            case "ppc64" -> "ppc64";
            default -> architecture;
        };
    }

    private static String osNameProperty() {
        String value = System.getProperty("os.name");
        return value != null ? value.toLowerCase(Locale.ROOT) : UNKNOWN_OS;
    }

    private static String osArchitectureProperty() {
        String value = System.getProperty("os.arch");
        return value != null ? value.toLowerCase(Locale.ROOT) : UNKNOWN_ARCHITECTURE;
    }
}
