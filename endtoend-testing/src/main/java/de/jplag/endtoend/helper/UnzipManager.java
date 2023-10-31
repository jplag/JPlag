package de.jplag.endtoend.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import de.jplag.endtoend.model.DataSet;

public class UnzipManager {
    private final Map<DataSet, File> unzippedFiles;
    private static UnzipManager instance;

    private static UnzipManager getInstance() {
        if (instance == null) {
            synchronized (UnzipManager.class) {
                if (instance == null) {
                    instance = new UnzipManager();
                }
            }
        }

        return instance;
    }

    public static File unzipOrCache(DataSet dataSet, File zip) throws IOException {
        return getInstance().unzipOrCacheInternal(dataSet, zip);
    }

    private UnzipManager() {
        this.unzippedFiles = new HashMap<>();
    }

    private File unzipOrCacheInternal(DataSet dataSet, File zip) throws IOException {
        if (!unzippedFiles.containsKey(dataSet)) {
            File target = Files.createTempDirectory(zip.getName()).toFile();
            FileHelper.unzip(zip, target);
            this.unzippedFiles.put(dataSet, target);
        }

        return this.unzippedFiles.get(dataSet);
    }
}
