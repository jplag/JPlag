package de.jplag;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.exceptions.RootDirectoryException;
import de.jplag.options.JPlagOptions;

/**
 * This class prevents the same root name of submissions.
 */
public class PreventSameRootName {
    private static final Logger logger = LoggerFactory.getLogger(PreventSameRootName.class);

    private final Map<String, Integer> statistic = new HashMap<>(); // uses to store information of same roots (Key: same root name ---> Value: number
                                                                    // of the same root name)
    private final JPlagOptions options;

    /**
     * Creates and initializes a PreventSameRootName instance, parameterized by a set of options. If there are same names of
     * root directories, they will be re-named before generating zip file and IDs. (e.g. two same root names: root ===>
     * root_1 && root_2)
     * @param options determines the parameterization
     */
    public PreventSameRootName(JPlagOptions options) throws RootDirectoryException {
        Set<File> newChangedSubmissionDirectories;
        Set<File> oldChangedSubmissionDirectories;
        Set<File> newSubmissionDirectories = options.submissionDirectories();
        Set<File> oldSubmissionDirectories = options.oldSubmissionDirectories();
        if (oldSubmissionDirectories == null && newSubmissionDirectories == null) {
            throw new RootDirectoryException("No submission");
        }
        boolean hasSameRoots = false;
        hasSameRoots = hasSameRoots(newSubmissionDirectories, oldSubmissionDirectories);
        if (hasSameRoots) {
            logger.info("Detected same name of root directories, the name will be re-named...");
            newChangedSubmissionDirectories = renameRoots(newSubmissionDirectories);
            oldChangedSubmissionDirectories = renameRoots(oldSubmissionDirectories);
        } else {
            newChangedSubmissionDirectories = newSubmissionDirectories;
            oldChangedSubmissionDirectories = oldSubmissionDirectories;
        }
        this.options = new JPlagOptions(options.language(), options.minimumTokenMatch(), newChangedSubmissionDirectories,
                oldChangedSubmissionDirectories, options.baseCodeSubmissionDirectory(), options.subdirectoryName(), options.fileSuffixes(),
                options.exclusionFileName(), options.similarityMetric(), options.similarityThreshold(), options.maximumNumberOfComparisons(),
                options.clusteringOptions(), options.debugParser());
    }

    /**
     * @return a new options instance
     */
    public JPlagOptions getOptions() {
        return options;
    }

    /**
     * @return the statistic map
     */
    public Map<String, Integer> getStatistic() {
        return statistic;
    }

    /**
     * Counts the root directory names and their number with Map(named statistic).
     * @param submissionDirectories SubmissionDirectories that need to be counted
     */
    public void rootsClassification(Set<File> submissionDirectories) {
        if (submissionDirectories == null)
            return;
        for (File submissionDirectory : submissionDirectories) {
            String rootDirectory = getRootName(submissionDirectory);
            statistic.put(rootDirectory, statistic.getOrDefault(rootDirectory, 0) + 1);
        }
    }

    /**
     * Renames the same root directory name.
     * @param submissionDirectories SubmissionDirectories that need to be re-named
     * @return SubmissionDirectories that have been re-named
     */
    public Set<File> renameRoots(Set<File> submissionDirectories) {
        if (submissionDirectories == null)
            return Set.of();
        Set<File> set = new HashSet<>();
        for (File submissionDirectory : submissionDirectories) {
            String rootDirectory = getRootName(submissionDirectory);
            int index = statistic.getOrDefault(rootDirectory, -1);
            if (index == -1)
                continue;
            String newRootDirectory = rootDirectory + "_" + index;
            statistic.put(rootDirectory, statistic.get(rootDirectory) - 1);
            File newFile = new File(Path.of(submissionDirectory.getParent(), newRootDirectory).toString());
            set.add(newFile);
            submissionDirectory.renameTo(newFile);
            logger.info("Original submission path ===> Current submission path:" + submissionDirectory.getPath() + " ===> " + newFile.getPath());
        }
        return Set.copyOf(set);
    }

    /**
     * Gets root name of a submissionDirectory.
     * @param submissionDirectory A single submissionDirectory
     * @return Root name of the submission
     */
    public String getRootName(File submissionDirectory) {
        return submissionDirectory.getPath().substring(submissionDirectory.getParent().length() + 1);
    }

    /**
     * Determines if there are the same root directories.
     * @param newSubmissionDirectories The new submissionDirectories
     * @param oldSubmissionDirectories The old submissionDirectories
     * @return True, if there are the same root directories. False, otherwise.
     */
    public boolean hasSameRoots(Set<File> newSubmissionDirectories, Set<File> oldSubmissionDirectories) throws RootDirectoryException {
        checkDirectoryExist(newSubmissionDirectories);
        checkDirectoryExist(oldSubmissionDirectories);
        rootsClassification(newSubmissionDirectories);
        rootsClassification(oldSubmissionDirectories);
        Set<Map.Entry<String, Integer>> entries = statistic.entrySet();
        entries.removeIf(entry -> entry.getValue() == 1);
        return !statistic.isEmpty();
    }

    /**
     * Checks if directories exist. If not, throws RootDirectoryException.
     * @param submissionDirectories The submissionDirectories which needs to be checked
     */
    public void checkDirectoryExist(Set<File> submissionDirectories) throws RootDirectoryException {
        for (File submissionDirectory : submissionDirectories) {
            boolean exists = submissionDirectory.exists();
            if (!exists) {
                throw new RootDirectoryException("Submission Directory doesn't exist: " + submissionDirectory.getPath());
            }
        }
    }
}
