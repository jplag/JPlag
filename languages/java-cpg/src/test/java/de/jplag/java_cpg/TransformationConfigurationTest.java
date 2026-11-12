package de.jplag.java_cpg;

import static de.jplag.java_cpg.transformation.TransformationRepository.forStatementToWhileStatement;
import static de.jplag.java_cpg.transformation.TransformationRepository.ifWithNegatedConditionResolution;
import static de.jplag.java_cpg.transformation.TransformationRepository.inlineSingleUseVariable;
import static de.jplag.java_cpg.transformation.TransformationRepository.wrapThenStatement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.jplag.TokenType;
import de.jplag.java_cpg.passes.TransformationPassConfiguration;
import de.jplag.java_cpg.transformation.GraphTransformation.ExecutionPhase;
import de.jplag.java_cpg.transformation.operations.GraphMutationContext;

class TransformationConfigurationTest {
    @TempDir
    private Path directory;

    private Path source(String name, String method) throws Exception {
        Path path = directory.resolve(name + ".java");
        Files.writeString(path, "class " + name + " { " + method + " }");
        return path;
    }

    private List<TokenType> tokens(JavaCpgLanguage language, Path source, boolean normalize) throws Exception {
        return language.parse(Set.of(source.toFile()), normalize).stream().map(token -> token.getType()).toList();
    }

    private Path loop() throws Exception {
        return source("Loop", "static int sum(int[] a) { int sum=0; for(int i=0;i<a.length;i++) { sum+=a[i]; } return sum; }");
    }

    @Test
    void normalizationSelectionDoesNotDependOnPreviousParses() throws Exception {
        Path file = loop();
        var language = new JavaCpgLanguage();
        var normalized = tokens(new JavaCpgLanguage(), file, true);
        var plain = tokens(new JavaCpgLanguage(), file, false);
        assertFalse(normalized.equals(plain), "Fixture must expose loop conversion");
        assertEquals(normalized, tokens(language, file, true));
        assertEquals(plain, tokens(language, file, false));
        assertEquals(normalized, tokens(language, file, true));
        var initiallyPlain = new JavaCpgLanguage();
        assertEquals(plain, tokens(initiallyPlain, file, false));
        assertEquals(normalized, tokens(initiallyPlain, file, true));
    }

    @Test
    void constructingAndConfiguringOtherInstancesDoesNotChangeSelection() throws Exception {
        Path file = loop();
        var first = new JavaCpgLanguage();
        first.resetTransformations();
        var obligatoryOnly = tokens(first, file, true);
        var second = new JavaCpgLanguage();
        var allRules = tokens(second, file, true);
        assertFalse(obligatoryOnly.equals(allRules));
        assertEquals(obligatoryOnly, tokens(first, file, true));
        first.addTransformation(forStatementToWhileStatement);
        assertEquals(allRules, tokens(first, file, true));
        first.resetTransformations();
        assertEquals(allRules, tokens(second, file, true));
        assertEquals(obligatoryOnly, tokens(first, file, true));
    }

    @Test
    void obligatoryWrappingIsAppliedWithAndWithoutNormalization() throws Exception {
        Path braced = source("Braced", "static int pick(boolean b) { if(b) { return 1; } else { return 2; } }");
        Path bare = source("Bare", "static int pick(boolean b) { if(b) return 1; else return 2; }");
        var language = new JavaCpgLanguage();
        for (boolean normalize : List.of(true, false)) {
            assertEquals(tokens(language, braced, normalize), tokens(language, bare, normalize));
        }
    }

    @Test
    void registrationIsOrderedIdempotentAndLocal() {
        var adapter = new CpgAdapter(wrapThenStatement, forStatementToWhileStatement, ifWithNegatedConditionResolution, inlineSingleUseVariable,
                forStatementToWhileStatement);
        assertEquals(List.of(forStatementToWhileStatement, ifWithNegatedConditionResolution),
                adapter.snapshot(true).forPhase(ExecutionPhase.AST_TRANSFORM, new GraphMutationContext()).getTransformations());
        adapter.clearTransformations();
        adapter.addTransformation(wrapThenStatement);
        assertEquals(List.of(wrapThenStatement), adapter.snapshot(true).transformations());
        assertTrue(new CpgAdapter().snapshot(true).transformations().isEmpty());
    }

    @Test
    void snapshotsAndPassConfigurationsAreImmutable() {
        var adapter = new CpgAdapter(wrapThenStatement, forStatementToWhileStatement);
        var snapshot = adapter.snapshot(true);
        adapter.clearTransformations();
        adapter.addTransformation(inlineSingleUseVariable);
        assertEquals(List.of(wrapThenStatement, forStatementToWhileStatement), snapshot.transformations());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.transformations().clear());
        var input = new ArrayList<>(List.of(forStatementToWhileStatement));
        var configuration = new TransformationPassConfiguration(input, new GraphMutationContext());
        input.clear();
        assertEquals(List.of(forStatementToWhileStatement), configuration.getTransformations());
        assertThrows(UnsupportedOperationException.class, () -> configuration.getTransformations().clear());
    }

    @Test
    void configurationMutationsAndSnapshotsCanRunConcurrently() throws Exception {
        var adapter = new CpgAdapter(wrapThenStatement);
        var original = adapter.snapshot(true);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var writer = executor.submit(() -> {
                for (int i = 0; i < 1000; i++) {
                    adapter.addTransformation(forStatementToWhileStatement);
                    adapter.clearTransformations();
                }
            });
            var reader = executor.submit(() -> {
                for (int i = 0; i < 1000; i++) {
                    var selection = adapter.snapshot(true).transformations();
                    assertTrue(selection.equals(List.of(wrapThenStatement))
                            || selection.equals(List.of(wrapThenStatement, forStatementToWhileStatement)));
                }
            });
            writer.get();
            reader.get();
        }
        assertEquals(List.of(wrapThenStatement), original.transformations());
    }

    @Test
    void unnormalizedSnapshotDoesNotOverwriteReorderingPreference() {
        var adapter = new CpgAdapter(wrapThenStatement, forStatementToWhileStatement);
        assertFalse(adapter.snapshot(false).reorderingEnabled());
        assertEquals(List.of(wrapThenStatement), adapter.snapshot(false).transformations());
        assertTrue(adapter.snapshot(true).reorderingEnabled());
        adapter.setReorderingEnabled(false);
        adapter.snapshot(false);
        assertFalse(adapter.snapshot(true).reorderingEnabled());
    }

    @Test
    void standardPresetMustBeSelectedExplicitlyAfterReset() throws Exception {
        Path file = source("Inline", "static int twice(int value) { int doubled=value*2; return doubled; }");
        var language = new JavaCpgLanguage();
        language.resetTransformations();
        var obligatoryOnly = tokens(language, file, true);
        language.addTransformations(language.standardTransformations());
        var standard = tokens(language, file, true);
        assertFalse(obligatoryOnly.equals(standard), "Standard preset must enable local inlining");
        language.resetTransformations();
        assertEquals(obligatoryOnly, tokens(language, file, true));
    }
}
