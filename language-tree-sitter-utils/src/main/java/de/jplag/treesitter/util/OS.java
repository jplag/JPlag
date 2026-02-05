package de.jplag.treesitter.util;

import java.util.Locale;

/**
 * Utility class for operating system detection and architecture identification.
 * <p>
 * This class provides methods to determine the current operating system and CPU architecture in a normalized format. It
 * is primarily used for loading the appropriate native Tree-sitter libraries based on the platform.
 * </p>
 * <p>
 * The class normalizes various OS and architecture identifiers to a consistent set of values that match the naming
 * conventions used in the native library distribution.
 * </p>
 */
public final class OS {
    private static final String UNKNOWN_OS = "UNKNOWN";
    private static final String UNKNOWN_ARCHITECTURE = "UNKNOWN";

    private static final String OS_MAC = "mac";
    private static final String OS_LINUX = "linux";
    private static final String OS_WINDOWS = "windows";

    private OS() {
    }

    /**
     * Returns the normalized operating system name.
     * <p>
     * Detects the current operating system and returns a normalized identifier. Supports macOS (including various naming
     * conventions), Linux, and Windows.
     * </p>
     * @return The normalized OS name: "mac", "linux", or "windows"
     * @throws RuntimeException If the operating system is not supported
     */
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

    /**
     * Returns the normalized CPU architecture name.
     * <p>
     * Detects the current CPU architecture and returns a normalized identifier. Maps various architecture names to
     * consistent values used by native library distributions.
     * </p>
     * @return The normalized architecture name (e.g., "i386", "amd64", "aarch64", "arm", "ppc", "ppc64")
     */
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

    /**
     * Gets the operating system name from system properties.
     * @return The lowercase OS name, or "UNKNOWN" if not available
     */
    private static String osNameProperty() {
        String value = System.getProperty("os.name");
        return value != null ? value.toLowerCase(Locale.ROOT) : UNKNOWN_OS;
    }

    /**
     * Gets the CPU architecture from system properties.
     * @return The lowercase architecture name, or "UNKNOWN" if not available
     */
    private static String osArchitectureProperty() {
        String value = System.getProperty("os.arch");
        return value != null ? value.toLowerCase(Locale.ROOT) : UNKNOWN_ARCHITECTURE;
    }
}
