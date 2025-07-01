package de.jplag.cli.options;

import java.util.ArrayList;
import java.util.List;

import com.ibm.icu.text.CharsetDetector;

public class CharsetCandidates extends ArrayList<String> {
    public CharsetCandidates() {
        super(List.of(CharsetDetector.getAllDetectableCharsets()));
    }
}
