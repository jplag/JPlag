package de.jplag.emf.model.parser;

import org.eclipse.emf.ecore.EObject;

import de.jplag.emf.util.AbstractMetamodelVisitor;

/**
 * Visits a model containment tree and extracts the relevant token based on a dynamically created token set.
 * @author Timur Saglam
 */
public class EmfModelTokenGenerator extends AbstractMetamodelVisitor {
    private final EmfModelParser parser;
    private final EmfModelElementTokenizer tokenizer;

    /**
     * Creates the visitor.
     * @param parser is the parser which receives the generated tokens.
     * @param tokenizer is the tokenizer that assigns tokens to model elements.
     */
    public EmfModelTokenGenerator(EmfModelParser parser, EmfModelElementTokenizer tokenizer) {
        this.parser = parser;
        this.tokenizer = tokenizer;
    }

    @Override
    protected void visitEObject(EObject eObject) {
        parser.addToken(tokenizer.element2Token(eObject), eObject);
    }
}
