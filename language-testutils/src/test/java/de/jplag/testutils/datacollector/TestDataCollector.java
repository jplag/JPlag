package de.jplag.testutils.datacollector;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import de.jplag.TokenType;

/**
 * Collects data for tests. Used by {@link de.jplag.testutils.LanguageModuleTest}s.
 */
public class TestDataCollector {
    private final List<TestData> sourceCoverageData;
    private final List<TestData> tokenCoverageData;
    private final List<TokenListTest> containedTokenData;
    private final List<TokenListTest> tokenSequenceTest;
    private final List<TokenPositionTestData> tokenPositionTestData;

    private final List<TestData> allTestData;

    private final File testFileLocation;

    /**
     * Creates a new collector. Should only be called by {@link de.jplag.testutils.LanguageModuleTest}.
     * @param testFileLocation The location containing the test source files.
     */
    public TestDataCollector(File testFileLocation) {
        this.testFileLocation = testFileLocation;

        this.sourceCoverageData = new ArrayList<>();
        this.tokenCoverageData = new ArrayList<>();
        this.containedTokenData = new ArrayList<>();
        this.tokenSequenceTest = new ArrayList<>();
        this.tokenPositionTestData = new ArrayList<>();

        this.allTestData = new ArrayList<>();
    }

    /**
     * Adds the given files to the test data. Returns a {@link TestDataContext}, that can be used to configure various tests
     * on the given files.
     * @param fileNames The names of the files to test
     * @return The {@link TestDataContext}
     */
    public TestDataContext testFile(String... fileNames) {
        Set<TestData> data = Arrays.stream(fileNames).map(it -> new File(this.testFileLocation, it)).map(FileTestData::new)
                .collect(Collectors.toSet());
        return new TestDataContext(data);
    }

    /**
     * Adds all files matching a certain type. Returns a {@link TestDataContext}, that can be used to configure various
     * tests on the given files.
     * @param fileExtension is the extension of the files to be added.
     * @return The {@link TestDataContext}
     */
    public TestDataContext testAllOfType(String fileExtension) {
        Set<TestData> data = Arrays.stream(testFileLocation.list()).filter(it -> it.endsWith(fileExtension))
                .map(it -> new File(this.testFileLocation, it)).map(FileTestData::new).collect(Collectors.toSet());
        return new TestDataContext(data);
    }

    /**
     * Adds a list of source string to the test data. Returns a {@link TestDataContext}, that can be used to configure
     * various tests on the given files.
     * @param sources The list of sources
     * @return The {@link TestDataContext}
     */
    public TestDataContext inlineSource(String... sources) {
        Set<TestData> data = Arrays.stream(sources).map(InlineTestData::new).collect(Collectors.toSet());
        return new TestDataContext(data);
    }

    /**
     * Adds all files from the given directory for token position tests. The sources can still be used for other tests,
     * using the returned {@link TestDataContext}.
     * @param directoryName The name of the directory containing the token position tests.
     * @return The context containing the added sources
     * @throws RuntimeException If the files cannot be read
     */
    public TestDataContext addTokenPositionTests(String directoryName) {
        File directory = new File(this.testFileLocation, directoryName);
        assumeTrue(directory.exists() && directory.isDirectory());
        Set<TestData> allTestsInDirectory = new HashSet<>();
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            try {
                TokenPositionTestData data = new TokenPositionTestData(file);
                allTestsInDirectory.add(data);
                this.tokenPositionTestData.add(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new TestDataContext(allTestsInDirectory);
    }

    /**
     * @return The test data that should be checked for source coverage
     */
    public List<TestData> getSourceCoverageData() {
        return Collections.unmodifiableList(sourceCoverageData);
    }

    /**
     * @return The test data that should be checked for token coverage
     */
    public List<TestData> getTokenCoverageData() {
        return Collections.unmodifiableList(tokenCoverageData);
    }

    /**
     * @return The test data that should be checked for a contained set of tokens
     */
    public List<TokenListTest> getContainedTokenData() {
        return Collections.unmodifiableList(containedTokenData);
    }

    /**
     * @return The test data that should be checked for a specific sequence of tokens
     */
    public List<TokenListTest> getTokenSequenceTest() {
        return Collections.unmodifiableList(tokenSequenceTest);
    }

    /**
     * @return The test data that should be checked for token positions.
     */
    public List<TokenPositionTestData> getTokenPositionTestData() {
        return Collections.unmodifiableList(this.tokenPositionTestData);
    }

    /**
     * @return The list of all test data
     */
    public List<TestData> getAllTestData() {
        return Collections.unmodifiableList(allTestData);
    }

    /**
     * Data for tests, that also require a list of tokens.
     * @param tokens The list of tokens
     * @param data The test data
     */
    public record TokenListTest(List<TokenType> tokens, TestData data) {

        @Override
        public String toString() {
            return data.toString(); // readable test name
        }
    }

    /**
     * A builder used to configure tests for a set of data.
     */
    public final class TestDataContext {
        private final Set<TestData> testData;

        private TestDataContext(Set<TestData> testData) {
            this.testData = testData;
            allTestData.addAll(testData);
        }

        /**
         * Test the data set for source coverage.
         * @return self reference
         */
        public TestDataContext testSourceCoverage() {
            sourceCoverageData.addAll(testData);
            return this;
        }

        /**
         * Test the data set for token coverage.
         * @return self reference
         */
        public TestDataContext testTokenCoverage() {
            tokenCoverageData.addAll(testData);
            return this;
        }

        /**
         * Test the data set for source and token coverage. Behaves just like calling {@link this#testSourceCoverage()} and
         * {@link this#testTokenCoverage()}.
         * @return self reference
         */
        public TestDataContext testCoverages() {
            this.testSourceCoverage();
            this.testTokenCoverage();
            return this;
        }

        /**
         * Test the data set for contained tokens. The tokens neither have to occur exclusively nor in the given order.
         * @param tokens The set of tokens to check for.
         * @return self reference
         */
        public TestDataContext testContainedTokens(TokenType... tokens) {
            containedTokenData.addAll(listTestsFromArray(tokens));
            return this;
        }

        /**
         * Test the data set for a specific token sequence. The tokens have to be extracted in that exact order. The file end
         * token can be omitted.
         * @param tokens The sequence of tokens to check for
         * @return self reference
         */
        public TestDataContext testTokenSequence(TokenType... tokens) {
            tokenSequenceTest.addAll(listTestsFromArray(tokens));
            return this;
        }

        private List<TokenListTest> listTestsFromArray(TokenType... tokens) {
            return this.testData.stream().map(it -> new TokenListTest(Arrays.asList(tokens), it)).toList();
        }
    }
}
