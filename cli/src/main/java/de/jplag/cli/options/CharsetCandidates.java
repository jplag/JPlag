package de.jplag.cli.options;

import java.util.ArrayList;
import java.util.List;

import com.ibm.icu.text.CharsetDetector;

/**
 * Provides a list of charsets for picocli.
 */
public class CharsetCandidates extends ArrayList<String> {
    /**
     * New instance.
     */
    public CharsetCandidates() {
        super(List.of(CharsetDetector.getAllDetectableCharsets()));
    }
}
