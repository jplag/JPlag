package de.jplag.pdf.utils;

import java.io.File;
import java.util.HashMap;

public class PathIdLookup {
    private static PathIdLookup instance;

    public static PathIdLookup getInstance() {
        if (instance == null) {
            instance = new PathIdLookup();
        }
        return instance;
    }

    private HashMap<String, String> ids;

    private PathIdLookup() {
        this.ids = new HashMap<>();
    }

    public String getIdFor(String path) {
        if (this.ids.containsKey(path)) {
            return this.ids.get(path);
        } else {
            String id = String.valueOf(this.ids.size());
            this.ids.put(path, id);
            return id;
        }
    }

    public String getIdFor(File file) {
        return this.getIdFor(file.getAbsolutePath());
    }
}
