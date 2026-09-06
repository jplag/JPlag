package de.jplag.java.babylon.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.jplag.ParsingException;
import de.jplag.java.babylon.TestWithResource;
import de.jplag.java.babylon.pipeline.TransformationPipeline;

import jdk.incubator.code.dialect.core.CoreOp;

/**
 * Base class for tests of transformers.<br>
 * Applies the transformation to a single input file and asserts the results.
 */
public abstract class TransformerTest extends TestWithResource {
    protected abstract String getFileName();

    protected abstract TransformationPipeline getPipeline();

    protected abstract String getExpectedOriginal();

    protected abstract String getExpectedTransformed();

    /**
     * Check that the transformation works as expected.
     * @throws ParsingException if the input could not be parsed
     */
    @Test
    public void testTransformer() throws ParsingException {
        ParseResult parseResult = parseFile(getFileName());

        CoreOp.FuncOp op = parseResult.extractCodeModel();
        assertEquals(getExpectedOriginal(), op.toText());

        CoreOp.FuncOp transformedOp = parseResult.extractCodeModel(getPipeline());
        assertEquals(getExpectedTransformed(), transformedOp.toText());
    }
}
