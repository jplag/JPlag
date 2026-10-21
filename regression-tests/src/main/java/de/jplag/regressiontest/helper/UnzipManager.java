package de.jplag.regressiontest.helper;

import static java.nio.file.attribute.AclEntryPermission.APPEND_DATA;
import static java.nio.file.attribute.AclEntryPermission.EXECUTE;
import static java.nio.file.attribute.AclEntryPermission.READ_DATA;
import static java.nio.file.attribute.AclEntryPermission.SYNCHRONIZE;
import static java.nio.file.attribute.AclEntryPermission.WRITE_DATA;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.regressiontest.model.Dataset;

/**
 * Manages unzip operations with caching for datasets.
 */
public final class UnzipManager {
    private static UnzipManager instance;
    private final Map<Dataset, Path> unzippedFiles;
    private final Logger logger = LoggerFactory.getLogger(UnzipManager.class);

    private static synchronized UnzipManager getInstance() {
        if (instance == null) {
            instance = new UnzipManager();
        }

        return instance;
    }

    private UnzipManager() {
        this.unzippedFiles = new HashMap<>();
    }

    /**
     * Unzips the given ZIP file for the dataset or returns a cached directory.
     * @param dataset the dataset associated with the ZIP
     * @param zip the ZIP file to unzip
     * @return the directory with unzipped contents
     * @throws IOException if an I/O error occurs during unzipping
     */
    public static Path unzipOrCache(Dataset dataset, Path zip) throws IOException {
        return getInstance().unzipOrCacheInternal(dataset, zip);
    }

    private Path unzipOrCacheInternal(Dataset dataset, Path zip) throws IOException {
        if (!unzippedFiles.containsKey(dataset)) {
            Path target;

            if (SystemUtils.IS_OS_UNIX) {
                FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
                target = Files.createTempDirectory(zip.getFileName().toString(), attr);
            } else {
                target = Files.createTempDirectory(zip.getFileName().toString());
                try {
                    AclFileAttributeView aclView = Files.getFileAttributeView(target, AclFileAttributeView.class);
                    AclEntry entry = AclEntry.newBuilder().setType(AclEntryType.ALLOW).setPrincipal(aclView.getOwner())
                            .setPermissions(EnumSet.of(EXECUTE, READ_DATA, WRITE_DATA, APPEND_DATA, SYNCHRONIZE)).build();
                    aclView.setAcl(Collections.singletonList(entry));
                } catch (IOException _) {
                    logger.warn("Could not set permissions for temp directory ({}).", target.toRealPath());
                }
            }

            FileHelper.unzip(zip, target);
            this.unzippedFiles.put(dataset, target);
        }

        return this.unzippedFiles.get(dataset);
    }
}
