package de.jplag.regressiontest.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.regressiontest.model.DataSet;

/**
 * Manages unzip operations with caching for datasets.
 */
public final class UnzipManager {
    private static UnzipManager instance;
    private final Map<DataSet, Path> unzippedFiles;
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
     * @param dataSet the dataset associated with the ZIP
     * @param zip the ZIP file to unzip
     * @return the directory with unzipped contents
     * @throws IOException if an I/O error occurs during unzipping
     */
    public static Path unzipOrCache(DataSet dataSet, Path zip) throws IOException {
        return getInstance().unzipOrCacheInternal(dataSet, zip);
    }

    private Path unzipOrCacheInternal(DataSet dataSet, Path zip) throws IOException {
        if (!unzippedFiles.containsKey(dataSet)) {
            Path target;

            if (SystemUtils.IS_OS_UNIX) {
                FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
                target = Files.createTempDirectory(zip.getFileName().toString(), attr);
            } else {
                target = Files.createTempDirectory(zip.getFileName().toString());
                try {
                    Files.setPosixFilePermissions(target,
                            Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE));
                } catch (IOException e) {
                    logger.warn("Could not set permissions for temp directory ({}).", target.toRealPath());
                }
            }

            FileHelper.unzip(zip, target);
            this.unzippedFiles.put(dataSet, target);
        }

        return this.unzippedFiles.get(dataSet);
    }
}
