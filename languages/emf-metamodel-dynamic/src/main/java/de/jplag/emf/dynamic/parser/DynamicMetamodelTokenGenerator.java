package de.jplag.emf.dynamic.parser;

import org.eclipse.emf.ecore.EObject;

import de.jplag.emf.util.AbstractMetamodelVisitor;

/**
 * Visits a metamodel containment tree and extracts the relevant token based on a dynamically created token set.
 * @author Timur Saglam
 */
public class DynamicMetamodelTokenGenerator extends AbstractMetamodelVisitor {
    private final DynamicEcoreParser parser;
    private final DynamicElementTokenizer tokenizer;

    /**
     * Creates the visitor.
     * @param parser is the parser which receives the generated tokens.
     * @param tokenizer is the tokenizer that assigns tokens to model elements.
     */
    public DynamicMetamodelTokenGenerator(DynamicEcoreParser parser, DynamicElementTokenizer tokenizer) {
        this.parser = parser;
        this.tokenizer = tokenizer;
    }

    @Override
    protected void visitEObject(EObject eObject) {
        parser.addToken(tokenizer.element2Token(eObject), eObject);
    }
}
