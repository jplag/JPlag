package de.jplag.cli;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.argparse4j.inf.ArgumentContainer;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;

/**
 * Can be used to automatically create and reuse {@link ArgumentGroup}s and {@link MutuallyExclusiveGroup}s through
 * their names only. This is useful when an {@link ArgumentParser} is not configured in an imperative fashion.
 */
public class CliGroupHelper {

    private final ArgumentParser parser;
    private final Map<String, MutuallyExclusiveGroup> mutuallyExclusiveGroups = new HashMap<>();
    private final Map<String, ArgumentGroup> argumentGroups = new HashMap<>();

    public CliGroupHelper(ArgumentParser parser) {
        this.parser = parser;
    }

    public ArgumentContainer getMutuallyExclusiveGroup(String name) {
        return mutuallyExclusiveGroups.computeIfAbsent(name, parser::addMutuallyExclusiveGroup);
    }

    public ArgumentContainer getArgumentGroup(String name) {
        return argumentGroups.computeIfAbsent(name, parser::addArgumentGroup);
    }

}
