package de.jplag.java.babylon.transformer;

import jdk.incubator.code.CodeTransformer;

/**
 * {@link CodeTransformer} with an identifier.<br>
 * Registered implementations automatically get picked up by {@link TransformationStepLoader} using the
 * {@link java.util.ServiceLoader} mechanism.
 */
public interface SimpleTransformation extends CodeTransformer {
    /**
     * @return Identifier of the transformation used for CLI options and dynamic loading. You should use some name within
     * {@code [a-z_-]+}
     */
    String getIdentifier();
}
