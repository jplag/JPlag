package de.jplag.emf.dynamic.parser;

import org.eclipse.emf.ecore.EObject;

import de.jplag.emf.dynamic.DynamicMetamodelTokenConstants;
import de.jplag.emf.parser.AbstractMetamodelVisitor;

/**
 * Visits a metamodel containment tree and extracts the relevant token based on a dynamically created token set.
 * @author Timur Saglam
 */
public class DynamicMetamodelTokenGenerator extends AbstractMetamodelVisitor {
    final private DynamicEcoreParser parser;

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
        int tokenType = DynamicMetamodelTokenConstants.getTokenType(eObject.eClass());
        parser.addToken(tokenType, eObject);
    }
}
