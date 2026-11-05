package de.jplag.util;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for handling file permissions
 */
public class FilePermissionUtils {
    private static final Logger logger = LoggerFactory.getLogger(FilePermissionUtils.class);
    private static Boolean tempDirectoryAllowsPosixPermissions = null;

    private FilePermissionUtils() {
        // hide default constructor
    }

    /**
     * Crates a temporary directory that is only readable by the current user
     * @param name The name of the directory
     * @return The crated directory
     * @throws IOException If setting the permissions fails
     */
    public static Path createTempDirectory(String name) throws IOException {
        if (tempDirectoryAllowsPosixPermissions == null) {
            Path probe = Files.createTempFile("nio-check-", ".tmp");
            FileStore store = Files.getFileStore(probe);
            tempDirectoryAllowsPosixPermissions = store.supportsFileAttributeView(PosixFileAttributeView.class);
            Files.delete(probe);
        }

        if (tempDirectoryAllowsPosixPermissions) {
            return Files.createTempDirectory(name, PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------")));
        } else {
            Path directory = Files.createTempDirectory(name);
            limitToCurrentUser(directory);
            return directory;
        }
    }

    /**
     * Limits the permission of the given file to be only usable by the owner. Warns the user if that is not possible
     * @param path The path to modify
     * @throws IOException If the file system interaction fails
     */
    public static void limitToCurrentUser(Path path) throws IOException {
        alterPermissions(path, "rwx------", AclEntryPermission.READ_DATA, AclEntryPermission.WRITE_DATA, AclEntryPermission.APPEND_DATA,
                AclEntryPermission.READ_ATTRIBUTES, AclEntryPermission.WRITE_ATTRIBUTES, AclEntryPermission.SYNCHRONIZE);
    }

    /**
     * Makes the path nonWritable for all Users
     * @param path The path to alter
     * @throws IOException If the file system interaction fails
     */
    public static void removeWriteAccess(Path path) throws IOException {
        alterPermissions(path, "r--------", AclEntryPermission.READ_DATA, AclEntryPermission.READ_ATTRIBUTES, AclEntryPermission.SYNCHRONIZE);
    }

    private static void alterPermissions(Path path, String unixPermission, AclEntryPermission... aclPermissions) throws IOException {
        FileStore store = Files.getFileStore(path);

        if (store.supportsFileAttributeView(PosixFileAttributeView.class)) {
            PosixFileAttributeView view = Files.getFileAttributeView(path, PosixFileAttributeView.class);
            view.setPermissions(PosixFilePermissions.fromString(unixPermission));
        } else if (store.supportsFileAttributeView(AclFileAttributeView.class)) {
            AclFileAttributeView view = Files.getFileAttributeView(path, AclFileAttributeView.class);
            view.setAcl(Collections.singletonList(
                    AclEntry.newBuilder().setType(AclEntryType.ALLOW).setPrincipal(view.getOwner()).setPermissions(aclPermissions).build()));
        } else {
            logger.warn("Cannot limit permissions of file ({}). This could potentially be unsafe", path);
        }
    }
}
