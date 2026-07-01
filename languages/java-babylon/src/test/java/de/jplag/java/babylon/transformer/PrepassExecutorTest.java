package de.jplag.java.babylon.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import de.jplag.ParsingException;
import de.jplag.java.babylon.DefaultTransformations;
import de.jplag.java.babylon.TestWithResource;
import de.jplag.java.babylon.pipeline.EagerPrepassExecutor;
import de.jplag.java.babylon.pipeline.HybridPrepassExecutor;
import de.jplag.java.babylon.pipeline.LazyPrepassExecutor;
import de.jplag.java.babylon.pipeline.PrepassExecutor;
import de.jplag.java.babylon.pipeline.TransformationPipeline;

import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Unit test for ensuring that the {@link PrepassExecutor} implementations are consistent.
 */
public class PrepassExecutorTest extends TestWithResource {
    /**
     * Unit test for ensuring that the {@link PrepassExecutor} implementations are consistent.
     */
    @Test
    public void testProduceSameResults() {
        List<TransformationPipeline> pipelines = getPipelines();
        List<ParseResult> files = getFiles();

        for (ParseResult file : files) {
            Iterator<CoreOp.FuncOp> list = pipelines.stream().map(file::extractCodeModel).toList().iterator();
            CoreOp.FuncOp first = list.next();
            while (list.hasNext()) {
                assertEquals(first.toText(), list.next().toText());
            }
        }
    }

    private List<TransformationPipeline> getPipelines() {
        List<? extends TransformationStep<?>> steps = DefaultTransformations.getDefaultSteps();
        List<PrepassExecutor> prepassExecutors = List.of(new LazyPrepassExecutor(), new EagerPrepassExecutor(), new HybridPrepassExecutor());
        return prepassExecutors.stream().map(executor -> new TransformationPipeline(steps, executor)).toList();
    }

    private List<ParseResult> getFiles() {
        return Stream.of("Asserts.java", "CopyElision.java", "EnhancedFor.java", "Inline.java", "StringConcat.java", "TryWithResources.java")
                .map(path -> {
                    try {
                        return parseFile(path);
                    } catch (ParsingException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }
}
