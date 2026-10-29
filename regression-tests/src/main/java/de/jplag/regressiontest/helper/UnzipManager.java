package de.jplag.regressiontest.helper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import de.jplag.regressiontest.model.Dataset;
import de.jplag.util.FilePermissionUtils;

/**
 * Manages unzip operations with caching for datasets.
 */
public final class UnzipManager {
    private static UnzipManager instance;
    private final Map<Dataset, Path> unzippedFiles;

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
            Path target = FilePermissionUtils.createTempDirectory(zip.getFileName().toString());
            FileHelper.unzip(zip, target);
            this.unzippedFiles.put(dataset, target);
        }

        return this.unzippedFiles.get(dataset);
    }
}
