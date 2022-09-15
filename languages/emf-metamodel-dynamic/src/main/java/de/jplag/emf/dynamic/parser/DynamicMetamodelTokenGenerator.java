package de.jplag.emf.dynamic.parser;

import org.eclipse.emf.ecore.EObject;

import de.jplag.emf.dynamic.DynamicMetamodelTokenType;
import de.jplag.emf.util.AbstractMetamodelVisitor;

/**
 * Visits a metamodel containment tree and extracts the relevant token based on a dynamically created token set.
 * @author Timur Saglam
 */
public class DynamicMetamodelTokenGenerator extends AbstractMetamodelVisitor {
    private final DynamicEcoreParser parser;

    /**
     * Creates the visitor.
     * @param parser is the parser which receives the generated tokens.
     */
    public DynamicMetamodelTokenGenerator(DynamicEcoreParser parser) {
        super(false);
        this.parser = parser;
    }

    @Override
    protected void visitEObject(EObject eObject) {
        var tokenType = new DynamicMetamodelTokenType(eObject);
        parser.addToken(tokenType, eObject);
    }
}
