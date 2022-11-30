package de.jplag.emf.util;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.emfatic.core.generator.emfatic.Writer;

import de.jplag.Token;
import de.jplag.emf.MetamodelToken;
import de.jplag.emf.MetamodelTokenType;

/**
 * Simplistic tree view representation of an EMF metamodel.
 * @author Timur Saglam
 */
public final class EmfaticModelView extends AbstractModelView {
    private static final String TYPE_KEYWORD = "(package |class |datatype |enum )";
    private static final String FEATURE_KEYWORD = "(.*attr .*|op .*|.*ref .*|.*val .*).*";
    private static final String ANYTHING = ".*";
    private static final String TYPE_SUFFIX = "( extends| \\{)";
    private static final char CLOSING_CHAR = '}';

    private final List<String> lines;
    private final List<String> hashedLines;
    private final Map<ENamedElement, Integer> elementToLine;

    private Copier copier;

    /**
     * Creates a tree view for a metamodel.
     * @param file is the file where the metamodel is persisted.
     */
    public EmfaticModelView(File file, Resource modelResource) {
        super(file);
        elementToLine = new HashMap<>();
        lines = generateEmfaticCode(viewBuilder, modelResource);

        // preparation for model to code tracing
        Resource copiedResource = copyModel(modelResource);
        replaceElementNamesWithHashes(copiedResource);
        hashedLines = generateEmfaticCode(new StringBuilder(), copiedResource);
    }

    public MetamodelToken convertToMetadataEnrichedToken(MetamodelToken token) {
        int length = Token.NO_VALUE;
        int line = Token.NO_VALUE;
        int column = Token.NO_VALUE;

        Optional<EObject> optionalEObject = token.getEObject();
        if (optionalEObject.isPresent()) {
            EObject eObject = optionalEObject.get();
            if (eObject instanceof ENamedElement element) {
                line = lineIndexOf(element);
                if (line != Token.NO_VALUE) {
                    if (token.getType()instanceof MetamodelTokenType type && type.isEndToken()) {
                        line = findEndIndex(line);
                    }
                    column = indentationOf(lines.get(line));
                    length = lines.get(line).length() - column;

                    // post processing, viewer requires 1-based indexing:
                    line++;
                    column += column == Token.NO_VALUE ? 0 : 1;
                }
            }
        }
        return new MetamodelToken(token.getType(), token.getFile(), line, column, length, token.getEObject());
    }

    private final void replaceElementNamesWithHashes(Resource copiedResource) {
        AbstractMetamodelVisitor renamer = new AbstractMetamodelVisitor(false) {
            @Override
            protected void visitENamedElement(ENamedElement eNamedElement) {
                eNamedElement.setName(Integer.toString(eNamedElement.hashCode()));
            }
        };
        copiedResource.getContents().forEach(renamer::visit);
    }

    private final List<String> generateEmfaticCode(StringBuilder builder, Resource modelResource) {
        Writer writer = new Writer();
        String code = writer.write(modelResource, null, null);
        builder.append(code);
        return builder.toString().lines().toList();
    }

    private final Resource copyModel(Resource model) {
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource copy = resourceSet.createResource(model.getURI());
        copier = new Copier();
        Collection<EObject> result = copier.copyAll(model.getContents());
        copier.copyReferences();
        copy.getContents().addAll(result);
        return copy;
    }

    /**
     * Locates the end index (closing character) for an element with a already known declaration index.
     */
    private int findEndIndex(int declarationIndex) {
        String beginLine = lines.get(declarationIndex);
        int indentation = indentationOf(beginLine);
        if (declarationIndex > 1) {
            for (int i = declarationIndex + 1; i < lines.size(); i++) {
                String nextLine = lines.get(i);

                if (nextLine.length() > indentation && CLOSING_CHAR == nextLine.charAt(indentation)) {
                    return i;
                }
            }
        }
        return lines.size() - 1;
    }

    /**
     * Calculates the indentation of a line, meaning the number of tabs and spaces.
     */
    private int indentationOf(String beginLine) {
        return beginLine.indexOf(beginLine.stripLeading());
    }

    /**
     * Returns the line index of the declaration of an element in the Emfatic code.
     */
    private int lineIndexOf(ENamedElement element) {
        return elementToLine.computeIfAbsent(element, this::locateLineIndexOf);
    }

    /**
     * Searches for the declaration of an element in the Emfatic code.
     */
    private int locateLineIndexOf(ENamedElement element) {
        String hash = Integer.toString(copier.get(element).hashCode());
        for (int index = 0; index < hashedLines.size(); index++) {
            String line = hashedLines.get(index);
            String trimmedLine = line.substring(indentationOf(line));
            if (line.contains(hash) && isDeclaration(element, hash, trimmedLine)) {
                return index;
            }
        }
        return Token.NO_VALUE;
    }

    /**
     * Checks if a line (with leading whitespace removed) contains an element based on its hash.
     */
    private boolean isDeclaration(ENamedElement element, String hash, String line) {
        return isStructuralFeature(element, hash, line) || isEnumLiteral(element, hash, line) || isType(hash, line);
    }

    private boolean isType(String hash, String line) {
        return line.matches(TYPE_KEYWORD + hash + TYPE_SUFFIX + ANYTHING);
    }

    private boolean isEnumLiteral(ENamedElement element, String hash, String line) {
        return element instanceof EEnumLiteral && line.matches(hash + ANYTHING);
    }

    private boolean isStructuralFeature(ENamedElement element, String hash, String line) {
        return element instanceof ETypedElement && line.matches(FEATURE_KEYWORD + hash + ANYTHING);
    }

}
