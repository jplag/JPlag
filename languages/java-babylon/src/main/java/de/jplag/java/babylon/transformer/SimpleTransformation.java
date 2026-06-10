package de.jplag.java.babylon.transformer;

import jdk.incubator.code.CodeTransformer;

public interface SimpleTransformation extends CodeTransformer {
    String getIdentifier();
}
