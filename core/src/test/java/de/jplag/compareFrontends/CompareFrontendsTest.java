package de.jplag.compareFrontends;

import java.io.File;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jplag.*;
import de.jplag.exceptions.ExitException;
import de.jplag.java_cpg.JavaCpgLanguage;
import de.jplag.options.JPlagOptions;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CompareFrontendsTest {

    public static final Logger logger = LoggerFactory.getLogger(CompareFrontendsTest.class);

    private static List<UnaryOperator<JPlagOptions>> optionsToCompare() {
        return List.of(
                // c -> c.withLanguageOption(new JavaLanguage()).withNormalize(false),
                // c -> c.withLanguageOption(new JavaLanguage()).withNormalize(true),
                c -> c.withLanguageOption(new JavaCpgLanguage()).withNormalize(false)
        // c -> c.withLanguageOption(new JavaCpgLanguage()).withNormalize(true)
        );
    }

    @ParameterizedTest
    @MethodSource("getInput")
    public void runWithFrontends(File base, File... root) {
        List<UnaryOperator<JPlagOptions>> consumers = optionsToCompare();
        List<JPlagResult> results = new ArrayList<>(consumers.size());
        JPlagOptions baseOptions = new JPlagOptions(null, Set.of(root), Set.of());
        if (Objects.nonNull(base))
            baseOptions = baseOptions.withBaseCodeSubmissionDirectory(base);
        for (UnaryOperator<JPlagOptions> consumer : consumers) {
            JPlagOptions options = consumer.apply(baseOptions);
            try {
                results.add(JPlag.run(options));
            } catch (ExitException e) {
                throw new RuntimeException(e);
            }
        }

        Map<SubmissionPair, List<JPlagComparison>> resultsByPair = new HashMap<>();
        results.forEach(result -> {
            result.getAllComparisons().forEach(comparison -> {
                List<Submission> ordered = Stream.of(comparison.firstSubmission(), comparison.secondSubmission())
                        .sorted(Comparator.comparing(Submission::getName)).toList();
                SubmissionPair pair = new SubmissionPair(ordered.get(0), ordered.get(1));
                List<JPlagComparison> listForPair = resultsByPair.computeIfAbsent(pair, k -> new ArrayList<>());
                listForPair.add(comparison);
            });
        });

        resultsByPair.forEach((pair, comparisons) -> {
            List<Double> similarities = comparisons.stream().map(JPlagComparison::similarity).toList();
            String similaritiesStr = similarities.stream().map("\t%.4f"::formatted).collect(Collectors.joining(" – "));
            logger.info("Results for %s - %s:%s".formatted(pair.first, pair.second, similaritiesStr));

            List<List<FileData>> analysis = comparisons.stream().map(this::analyse).toList();
        });
        var parsingTime = results.stream().map(JPlagResult::getSubmissions).map(SubmissionSet::getTotalParsingTime).map(l -> Long.toString(l));
        logger.info("Total parsing time for each frontend (ms): " + parsingTime.collect(Collectors.joining(" / ")));
    }

    private List<FileData> analyse(JPlagComparison comparison) {
        List<Match> matches = comparison.matches();
        List<Token> tokenList1 = comparison.firstSubmission().getTokenList();
        List<Token> tokenList2 = comparison.secondSubmission().getTokenList();

        Map<String, List<Token>> fileToTokens = tokenList1.stream().collect(Collectors.groupingBy(token -> token.getFile().getName()));

        Map<String, List<Match>> fileToMatches = matches.stream()
                .collect(Collectors.groupingBy(match -> tokenList1.get(match.startOfFirst()).getFile().getName()));

        List<FileData> allFileData = fileToTokens.entrySet().stream()
                .map(entry -> new FileData(entry.getKey(), entry.getValue(), fileToMatches.getOrDefault(entry.getKey(), List.of())))
                .sorted(Comparator.comparing(FileData::fileName)).toList();

        allFileData.forEach(fileData -> {
            int totalMatch = fileData.matches.stream().mapToInt(Match::length).sum();
            int totalLength = fileData.tokenList().size();
            double percentage = totalMatch * 1.0d / totalLength;
            String percentageBar = getPercentageBar(percentage, 20);
            String mostSimilarFile = getMostSimilarFile(fileData, tokenList2);

            String line = "%30s %s (%3d/%3d) %.1f (msf: %s)".formatted(fileData.fileName.replace(".java", ""), percentageBar, totalMatch, totalLength,
                    percentage * 100, mostSimilarFile);
            logger.info(line);
        });

        return allFileData;

    }

    private static String getPercentageBar(double percentage, int length) {
        int barsLength = (int) Math.round(percentage * length);
        int spaceLength = Math.max(length - barsLength, 0);
        return "|%s%s|".formatted("-".repeat(barsLength), " ".repeat(spaceLength));
    }

    @NotNull
    private static String getMostSimilarFile(FileData fileData, List<Token> tokenList2) {
        String mostSimilarFile = fileData.matches().stream()
                .flatMap(match -> IntStream.range(match.startOfSecond(), match.endOfSecond()).mapToObj(tokenList2::get))
                .collect(Collectors.groupingBy(Token::getFile)).entrySet().stream().max(Comparator.comparing(entry -> entry.getValue().size()))
                .map(Map.Entry::getKey).map(File::getName).map(fileName -> fileName.replace(".java", "")).orElse("none");
        return mostSimilarFile;
    }

    record FileData(String fileName, List<Token> tokenList, List<Match> matches) {
    }

    private Stream<Arguments> getInput() {
        File base3 = new File("C:\\Users\\robin\\Documents\\Masterstudium\\FS 7\\MA\\repos\\submissions\\submissions\\base");
        File submission31 = new File("C:\\Users\\robin\\Documents\\Masterstudium\\FS 7\\MA\\repos\\submissions\\submissions\\org");
        File submission32 = new File("C:\\Users\\robin\\Documents\\Masterstudium\\FS 7\\MA\\repos\\submissions\\submissions\\plag");
        File[] submissions3 = {submission31, submission32};

        return Stream.of(
                // Arguments.of(null, new File[]{new File("C:\\Users\\robin\\Documents\\Masterstudium\\FS
                // 7\\MA\\data\\FullyGenerated\\files")})
                // Arguments.of(null, new File[]{new File("C:\\Users\\robin\\Documents\\Masterstudium\\FS
                // 7\\MA\\data\\ObfuscationPrompts\\files\\Freckles")})
                // Arguments.of(null, new File[]{new File("C:\\Users\\robin\\Documents\\Masterstudium\\FS
                // 7\\MA\\data\\prog19-obfuscated\\files")})
                Arguments.of(null, new File[] {new File("C:\\Users\\robin\\Documents\\Masterstudium\\FS 7\\MA\\data\\Fields")})
        // Arguments.of(base3, submissions3)
        );
    }

    record SubmissionPair(Submission first, Submission second) {
    }

}
