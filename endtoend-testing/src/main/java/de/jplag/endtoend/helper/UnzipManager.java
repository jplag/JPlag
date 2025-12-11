package de.jplag.endtoend.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.endtoend.model.DataSet;

/**
 * Manages unzip operations with caching for datasets.
 */
public final class UnzipManager {
    private static UnzipManager instance;
    private final Map<DataSet, File> unzippedFiles;
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
    public static File unzipOrCache(DataSet dataSet, File zip) throws IOException {
        return getInstance().unzipOrCacheInternal(dataSet, zip);
    }

    private File unzipOrCacheInternal(DataSet dataSet, File zip) throws IOException {
        if (!unzippedFiles.containsKey(dataSet)) {
            File target;

            if (SystemUtils.IS_OS_UNIX) {
                FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
                target = Files.createTempDirectory(zip.getName(), attr).toFile();
            } else {
                target = Files.createTempDirectory(zip.getName()).toFile();
                if (!(target.setReadable(true, true) && target.setWritable(true, true) && target.setExecutable(true, true))) {
                    logger.warn("Could not set permissions for temp directory ({}).", target.getAbsolutePath());
                }
            }

            FileHelper.unzip(zip, target);
            this.unzippedFiles.put(dataSet, target);
        }

        return this.unzippedFiles.get(dataSet);
    }
}
