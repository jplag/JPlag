package de.jplag.java_cpg.evaluation;

import static de.jplag.java_cpg.AbstractJavaCpgLanguageTest.BASE_PATH;
import static de.jplag.java_cpg.evaluation.EvaluationEngineTest.checkNonDeadCodeNotRemoved;
import static de.jplag.java_cpg.evaluation.EvaluationEngineTest.getTokensFromFileWithoutDeadCode;
import static de.jplag.java_cpg.evaluation.EvaluationEngineTest.progpediaFiles;
import static de.jplag.java_cpg.evaluation.EvaluationEngineTest.similarity;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.java_cpg.JavaCpgLanguage;
import de.jplag.java_cpg.ai.ArrayAiType;
import de.jplag.java_cpg.ai.CharAiType;
import de.jplag.java_cpg.ai.CpgErrorException;
import de.jplag.java_cpg.ai.FloatAiType;
import de.jplag.java_cpg.ai.IntAiType;
import de.jplag.java_cpg.ai.JavaLanguageFeatureNotSupportedException;
import de.jplag.java_cpg.ai.StringAiType;

class ProgpediaEvalTest {

    @Test
    @Disabled("Only for evaluation purposes, not a real test")
    void ProgpediaDeadCodeEvaluationAll() {
        progpediaFiles().forEach(fileName -> {
            try {
                long startTime = System.nanoTime();
                List<Token> tokens = getTokensFromFileStandard(fileName, false, false, false, false, false);
                long timeNoRemoval = System.nanoTime() - startTime;

                startTime = System.nanoTime();
                List<Token> tokensWithoutSimpleDeadCode = getTokensFromFileStandard(fileName, false, false, false, true, false);
                long timeSimpleRemoval = System.nanoTime() - startTime;

                boolean javaLanguageFeatureNotSupported = false;
                boolean cpgErrorException = false;
                boolean runtimeError = false;
                startTime = System.nanoTime();
                List<Token> tokensWithoutDeadCode;
                try {
                    tokensWithoutDeadCode = getTokensFromFileStandard(fileName, true, true, false, true, false);
                } catch (JavaLanguageFeatureNotSupportedException _) {
                    tokensWithoutDeadCode = new ArrayList<>(tokens);
                    javaLanguageFeatureNotSupported = true;
                } catch (CpgErrorException _) {
                    tokensWithoutDeadCode = new ArrayList<>(tokens);
                    cpgErrorException = true;
                } catch (Exception e) {
                    Throwable one = e.getCause();
                    Throwable two = one.getCause();
                    if (two instanceof CpgErrorException) {
                        tokensWithoutDeadCode = new ArrayList<>(tokens);
                        cpgErrorException = true;
                    } else if (two instanceof JavaLanguageFeatureNotSupportedException) {
                        tokensWithoutDeadCode = new ArrayList<>(tokens);
                        javaLanguageFeatureNotSupported = true;
                    } else {
                        runtimeError = true;
                        tokensWithoutDeadCode = new ArrayList<>(tokens);
                    }
                }
                long timeFullRemoval = System.nanoTime() - startTime;

                List<Token> tokensWithoutDeadCodeManual = getTokensFromFileWithoutDeadCode(fileName, false, false);

                boolean tokensSound = checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokens);
                boolean simpleRemovalSound = checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokensWithoutSimpleDeadCode);
                boolean removalSound = checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokensWithoutDeadCode);
                if (javaLanguageFeatureNotSupported || cpgErrorException || runtimeError) {
                    tokensSound = true;
                    simpleRemovalSound = true;
                    removalSound = true;
                }
                double simNoRemoval = similarity(tokensWithoutDeadCodeManual, tokens);
                double simSimpleRemoval = similarity(tokensWithoutDeadCodeManual, tokensWithoutSimpleDeadCode);
                double simFullRemoval = similarity(tokensWithoutDeadCodeManual, tokensWithoutDeadCode);
                double removedSimpleTokens = tokens.size() - tokensWithoutSimpleDeadCode.size();
                double removedFullTokens = tokens.size() - tokensWithoutDeadCode.size();
                double removedManualTokens = tokens.size() - tokensWithoutDeadCodeManual.size();

                File csvFile = new File("Progpedia_deadcode_results_Standard.csv");
                boolean fileExists = csvFile.exists();
                try (java.io.FileWriter writer = new java.io.FileWriter(csvFile, true)) {
                    if (!fileExists) {
                        writer.write(
                                "FileName,NoRemoval,SimpleRemoval,FullRemoval,RemovedSimpleTokens,RemovedFullTokens,RemovedManualTokens,TimeNoRemoval(ms),TimeSimpleRemoval(ms),TimeFullRemoval(ms),JavaLanguageFeatureNotSupported,CpgErrorException,RuntimeException,tokensSound,simpleRemovalSound,removalSound\n");
                    }
                    writer.write(String.format("%s,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.2f,%.2f,%.2f,%b,%b,%b,%b,%b,%b%n", fileName, simNoRemoval,
                            simSimpleRemoval, simFullRemoval, removedSimpleTokens, removedFullTokens, removedManualTokens,
                            timeNoRemoval / 1_000_000.0, timeSimpleRemoval / 1_000_000.0, timeFullRemoval / 1_000_000.0,
                            javaLanguageFeatureNotSupported, cpgErrorException, runtimeError, tokensSound, simpleRemovalSound, removalSound));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (ParsingException e) {
                System.out.println(e.getMessage());
            }
            assertTrue(true);
        });

        progpediaFiles().forEach(fileName -> {
            try {
                long startTime = System.nanoTime();
                List<Token> tokens = getTokensFromFileLevel1(fileName, false, false, false, false, false);
                long timeNoRemoval = System.nanoTime() - startTime;

                startTime = System.nanoTime();
                List<Token> tokensWithoutSimpleDeadCode = getTokensFromFileLevel1(fileName, false, false, false, true, false);
                long timeSimpleRemoval = System.nanoTime() - startTime;

                boolean javaLanguageFeatureNotSupported = false;
                boolean cpgErrorException = false;
                boolean runtimeError = false;
                startTime = System.nanoTime();
                List<Token> tokensWithoutDeadCode;
                try {
                    tokensWithoutDeadCode = getTokensFromFileLevel1(fileName, true, true, false, true, false);
                } catch (JavaLanguageFeatureNotSupportedException _) {
                    tokensWithoutDeadCode = new ArrayList<>(tokens);
                    javaLanguageFeatureNotSupported = true;
                } catch (CpgErrorException _) {
                    tokensWithoutDeadCode = new ArrayList<>(tokens);
                    cpgErrorException = true;
                } catch (Exception e) {
                    Throwable one = e.getCause();
                    Throwable two = one.getCause();
                    if (two instanceof CpgErrorException) {
                        tokensWithoutDeadCode = new ArrayList<>(tokens);
                        cpgErrorException = true;
                    } else if (two instanceof JavaLanguageFeatureNotSupportedException) {
                        tokensWithoutDeadCode = new ArrayList<>(tokens);
                        javaLanguageFeatureNotSupported = true;
                    } else {
                        runtimeError = true;
                        tokensWithoutDeadCode = new ArrayList<>(tokens);
                    }
                }
                long timeFullRemoval = System.nanoTime() - startTime;

                List<Token> tokensWithoutDeadCodeManual = getTokensFromFileWithoutDeadCode(fileName, false, false);

                boolean tokensSound = checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokens);
                boolean simpleRemovalSound = checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokensWithoutSimpleDeadCode);
                boolean removalSound = checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokensWithoutDeadCode);
                if (javaLanguageFeatureNotSupported || cpgErrorException || runtimeError) {
                    tokensSound = true;
                    simpleRemovalSound = true;
                    removalSound = true;
                }
                double simNoRemoval = similarity(tokensWithoutDeadCodeManual, tokens);
                double simSimpleRemoval = similarity(tokensWithoutDeadCodeManual, tokensWithoutSimpleDeadCode);
                double simFullRemoval = similarity(tokensWithoutDeadCodeManual, tokensWithoutDeadCode);
                double removedSimpleTokens = tokens.size() - tokensWithoutSimpleDeadCode.size();
                double removedFullTokens = tokens.size() - tokensWithoutDeadCode.size();
                double removedManualTokens = tokens.size() - tokensWithoutDeadCodeManual.size();

                File csvFile = new File("Progpedia_deadcode_results_Level1.csv");
                boolean fileExists = csvFile.exists();
                try (java.io.FileWriter writer = new java.io.FileWriter(csvFile, true)) {
                    if (!fileExists) {
                        writer.write(
                                "FileName,NoRemoval,SimpleRemoval,FullRemoval,RemovedSimpleTokens,RemovedFullTokens,RemovedManualTokens,TimeNoRemoval(ms),TimeSimpleRemoval(ms),TimeFullRemoval(ms),JavaLanguageFeatureNotSupported,CpgErrorException,RuntimeException,tokensSound,simpleRemovalSound,removalSound\n");
                    }
                    writer.write(String.format("%s,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.2f,%.2f,%.2f,%b,%b,%b,%b,%b,%b%n", fileName, simNoRemoval,
                            simSimpleRemoval, simFullRemoval, removedSimpleTokens, removedFullTokens, removedManualTokens,
                            timeNoRemoval / 1_000_000.0, timeSimpleRemoval / 1_000_000.0, timeFullRemoval / 1_000_000.0,
                            javaLanguageFeatureNotSupported, cpgErrorException, runtimeError, tokensSound, simpleRemovalSound, removalSound));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (ParsingException e) {
                System.out.println(e.getMessage());
            }
            assertTrue(true);
        });

        progpediaFiles().forEach(fileName -> {
            try {
                long startTime = System.nanoTime();
                List<Token> tokens = getTokensFromFileLevel2(fileName, false, false, false, false, false);
                long timeNoRemoval = System.nanoTime() - startTime;

                startTime = System.nanoTime();
                List<Token> tokensWithoutSimpleDeadCode = getTokensFromFileLevel2(fileName, false, false, false, true, false);
                long timeSimpleRemoval = System.nanoTime() - startTime;

                boolean javaLanguageFeatureNotSupported = false;
                boolean cpgErrorException = false;
                boolean runtimeError = false;
                startTime = System.nanoTime();
                List<Token> tokensWithoutDeadCode;
                try {
                    tokensWithoutDeadCode = getTokensFromFileLevel2(fileName, true, true, false, true, false);
                } catch (JavaLanguageFeatureNotSupportedException _) {
                    tokensWithoutDeadCode = new ArrayList<>(tokens);
                    javaLanguageFeatureNotSupported = true;
                } catch (CpgErrorException _) {
                    tokensWithoutDeadCode = new ArrayList<>(tokens);
                    cpgErrorException = true;
                } catch (Exception e) {
                    Throwable one = e.getCause();
                    Throwable two = one.getCause();
                    if (two instanceof CpgErrorException) {
                        tokensWithoutDeadCode = new ArrayList<>(tokens);
                        cpgErrorException = true;
                    } else if (two instanceof JavaLanguageFeatureNotSupportedException) {
                        tokensWithoutDeadCode = new ArrayList<>(tokens);
                        javaLanguageFeatureNotSupported = true;
                    } else {
                        runtimeError = true;
                        tokensWithoutDeadCode = new ArrayList<>(tokens);
                    }
                }
                long timeFullRemoval = System.nanoTime() - startTime;

                List<Token> tokensWithoutDeadCodeManual = getTokensFromFileWithoutDeadCode(fileName, false, false);

                boolean tokensSound = checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokens);
                boolean simpleRemovalSound = checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokensWithoutSimpleDeadCode);
                boolean removalSound = checkNonDeadCodeNotRemoved(tokensWithoutDeadCodeManual, tokensWithoutDeadCode);
                if (javaLanguageFeatureNotSupported || cpgErrorException || runtimeError) {
                    tokensSound = true;
                    simpleRemovalSound = true;
                    removalSound = true;
                }
                double simNoRemoval = similarity(tokensWithoutDeadCodeManual, tokens);
                double simSimpleRemoval = similarity(tokensWithoutDeadCodeManual, tokensWithoutSimpleDeadCode);
                double simFullRemoval = similarity(tokensWithoutDeadCodeManual, tokensWithoutDeadCode);
                double removedSimpleTokens = tokens.size() - tokensWithoutSimpleDeadCode.size();
                double removedFullTokens = tokens.size() - tokensWithoutDeadCode.size();
                double removedManualTokens = tokens.size() - tokensWithoutDeadCodeManual.size();

                File csvFile = new File("Progpedia_deadcode_results_Level2.csv");
                boolean fileExists = csvFile.exists();
                try (java.io.FileWriter writer = new java.io.FileWriter(csvFile, true)) {
                    if (!fileExists) {
                        writer.write(
                                "FileName,NoRemoval,SimpleRemoval,FullRemoval,RemovedSimpleTokens,RemovedFullTokens,RemovedManualTokens,TimeNoRemoval(ms),TimeSimpleRemoval(ms),TimeFullRemoval(ms),JavaLanguageFeatureNotSupported,CpgErrorException,RuntimeException,tokensSound,simpleRemovalSound,removalSound\n");
                    }
                    writer.write(String.format("%s,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.2f,%.2f,%.2f,%b,%b,%b,%b,%b,%b%n", fileName, simNoRemoval,
                            simSimpleRemoval, simFullRemoval, removedSimpleTokens, removedFullTokens, removedManualTokens,
                            timeNoRemoval / 1_000_000.0, timeSimpleRemoval / 1_000_000.0, timeFullRemoval / 1_000_000.0,
                            javaLanguageFeatureNotSupported, cpgErrorException, runtimeError, tokensSound, simpleRemovalSound, removalSound));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (ParsingException e) {
                System.out.println(e.getMessage());
            }
            assertTrue(true);
        });
    }

    @NotNull
    private static List<Token> getTokensFromFileStandard(@NotNull String fileName, boolean removeDeadCode, boolean detectDeadCode, boolean reorder,
            boolean normalize, boolean removeSimpleDeadCode) throws ParsingException {
        assert normalize || !reorder;
        JavaCpgLanguage language = new JavaCpgLanguage(removeDeadCode, detectDeadCode, reorder, removeSimpleDeadCode,
                JavaCpgLanguage.deadCodeRemovalTransformations(), IntAiType.DEFAULT, FloatAiType.DEFAULT, StringAiType.DEFAULT, CharAiType.DEFAULT,
                ArrayAiType.DEFAULT);
        File file = new File(BASE_PATH.toFile().getAbsolutePath(), fileName);
        Set<File> files = Set.of(file);
        List<Token> result = language.parse(files, normalize);
        result.removeLast(); // remove EOF token
        return result;
    }

    @NotNull
    private static List<Token> getTokensFromFileLevel1(@NotNull String fileName, boolean removeDeadCode, boolean detectDeadCode, boolean reorder,
            boolean normalize, boolean removeSimpleDeadCode) throws ParsingException {
        assert normalize || !reorder;
        JavaCpgLanguage language = new JavaCpgLanguage(removeDeadCode, detectDeadCode, reorder, removeSimpleDeadCode,
                JavaCpgLanguage.deadCodeRemovalTransformations(), IntAiType.INTERVALS, FloatAiType.DEFAULT, StringAiType.CHAR_INCLUSION,
                CharAiType.DEFAULT, ArrayAiType.LENGTH);
        File file = new File(BASE_PATH.toFile().getAbsolutePath(), fileName);
        Set<File> files = Set.of(file);
        List<Token> result = language.parse(files, normalize);
        result.removeLast(); // remove EOF token
        return result;
    }

    @NotNull
    private static List<Token> getTokensFromFileLevel2(@NotNull String fileName, boolean removeDeadCode, boolean detectDeadCode, boolean reorder,
            boolean normalize, boolean removeSimpleDeadCode) throws ParsingException {
        assert normalize || !reorder;
        JavaCpgLanguage language = new JavaCpgLanguage(removeDeadCode, detectDeadCode, reorder, removeSimpleDeadCode,
                JavaCpgLanguage.deadCodeRemovalTransformations(), IntAiType.SET, FloatAiType.SET, StringAiType.REGEX, CharAiType.SET,
                ArrayAiType.DEFAULT);
        File file = new File(BASE_PATH.toFile().getAbsolutePath(), fileName);
        Set<File> files = Set.of(file);
        List<Token> result = language.parse(files, normalize);
        result.removeLast(); // remove EOF token
        return result;
    }

}
