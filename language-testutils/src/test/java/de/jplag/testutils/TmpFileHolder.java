package de.jplag.testutils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TmpFileHolder {
    public static List<File> tmpFiles = new ArrayList<>();

    public static void deleteTmpFiles() {
        tmpFiles.forEach(File::delete);
        tmpFiles.clear();
    }
}
