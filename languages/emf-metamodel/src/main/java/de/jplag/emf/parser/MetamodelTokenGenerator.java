package de.jplag.emf.parser;

import static de.jplag.emf.MetamodelTokenType.ABSTRACT_CLASS_END;
import static de.jplag.emf.MetamodelTokenType.BOUND;
import static de.jplag.emf.MetamodelTokenType.CLASS_END;
import static de.jplag.emf.MetamodelTokenType.ENUM_END;
import static de.jplag.emf.MetamodelTokenType.INTERFACE_END;
import static de.jplag.emf.MetamodelTokenType.PACKAGE_END;
import static de.jplag.emf.MetamodelTokenType.RETURN_TYPE;
import static de.jplag.emf.MetamodelTokenType.THROWS_DECLARATION;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.ETypeParameter;

import de.jplag.emf.util.AbstractMetamodelVisitor;

/**
 * Visits a metamodel containment tree and extracts the relevant token. See also {@link MetamodelElementTokenizer}.
 * @author Timur Saglam
 */
public class MetamodelTokenGenerator extends AbstractMetamodelVisitor {
    private final EcoreParser parser;
    private final ModelingElementTokenizer tokenizer;

    /**
     * Creates the visitor.
     * @param parser is the parser which receives the generated tokens.
     */
    public MetamodelTokenGenerator(EcoreParser parser) {
        this.parser = parser;
        tokenizer = new MetamodelElementTokenizer();
    }

    @Override
    protected void visitEObject(EObject eObject) {
        // Create begin tokens for elements that directly map to a token.
        tokenizer.element2OptionalToken(eObject).ifPresent(it -> parser.addToken(it, eObject));
    }

    @Override
    protected void visitEOperation(EOperation eOperation) {
        if (eOperation.getEType() != null) {
            parser.addToken(RETURN_TYPE, eOperation);
        }
        eOperation.getEExceptions().forEach(it -> parser.addToken(THROWS_DECLARATION, it));
    }

    @Override
    protected void visitETypeParameter(ETypeParameter eTypeParameter) {
        eTypeParameter.getEBounds().forEach(it -> parser.addToken(BOUND, it));
    }

    @Override
    protected void leaveEClass(EClass eClass) {
        if (eClass.isInterface()) {
            parser.addToken(INTERFACE_END, eClass);
        } else if (eClass.isAbstract()) {
            parser.addToken(ABSTRACT_CLASS_END, eClass);
        } else {
            parser.addToken(CLASS_END, eClass);
        }
    }

    @Override
    protected void leaveEPackage(EPackage ePackage) {
        parser.addToken(PACKAGE_END, ePackage);
    }

    @Override
    protected void leaveEEnum(EEnum eEnum) {
        parser.addToken(ENUM_END, eEnum);
    }

}
