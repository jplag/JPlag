package de.jplag.emf.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.emfatic.core.generator.emfatic.Writer;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.TokenTrace;
import de.jplag.TokenType;
import de.jplag.emf.MetamodelTokenType;

/**
 * Textual view of an EMF metamodel based on Emfatic. Emfatic code is generated for the metamodel and the model elements
 * are then traced to line in the code. The tracing is done via hashes as model element names and keyword detection via
 * regex matching. The tracing is requires, as Emfatic does not provide it itself.
 */
public final class EmfaticModelView extends AbstractModelView {
    // The following regular expressions match keywords of the Emfatic syntax:
    private static final String PACKAGE_REGEX = "package\\s+\\S+;";
    private static final String TYPE_KEYWORD_REGEX = "(package |class |datatype |enum )";
    private static final String FEATURE_KEYWORD_REGEX = "(.*attr .*|op .*|.*ref .*|.*val .*).*";
    private static final String TYPE_SUFFIX_REGEX = "(;| extends| \\{)";
    private static final String LINE_SUFFIX_REGEX = ";";
    private static final char CLOSING_CHAR = '}';
    private static final String ANYTHING_REGEX = ".*";

    // Internal state of the view:
    private final List<String> lines; // Emfatic view code
    private final List<String> hashedLines; // code for model element tracing lookup
    private final Map<ENamedElement, Integer> elementToLine; // maps model elements to Emfatic code line numbers

    private final Copier modelCopier; // Allows to trace between original and copied elements
    private int lastLineIndex; // last line given to a token
    private final int rootPackageIndex;

    /**
     * Creates an Emfatic view for a metamodel.
     * @param file is the path for the view file to be created.
     * @param modelResource is the resource containing the metamodel.
     * @throws ParsingException if Emfatic crashes.
     */
    public EmfaticModelView(File file, Resource modelResource) throws ParsingException {
        super(file);
        elementToLine = new HashMap<>();
        lines = generateEmfaticCode(viewBuilder, modelResource);

        // preparation for model to code tracing
        modelCopier = new Copier();
        Resource copiedResource = EMFUtil.copyModel(modelResource, modelCopier);
        replaceElementNamesWithHashes(copiedResource);
        hashedLines = generateEmfaticCode(new StringBuilder(), copiedResource);
        rootPackageIndex = findIndexOfRootPackage(hashedLines);
    }

    @Override
    public TokenTrace getTokenTrace(EObject modelElement, TokenType tokenType) {
        int lineIndex = calculateLineIndexOf(modelElement, tokenType);
        String line = lines.get(lineIndex);
        int columnIndex = indentationOf(line);
        int length = line.length() - columnIndex;

        // post processing, viewer requires 1-based indexing:
        lineIndex++;
        columnIndex += columnIndex == Token.NO_VALUE ? 0 : 1;

        return new TokenTrace(lineIndex, columnIndex, length);
    }

    /**
     * Iterates over a model, replacing the names of all named elements by their hashcode. This allows identifying model
     * elements in subsequently generated Emfatic code while avoiding name collisions.
     */
    private void replaceElementNamesWithHashes(Resource copiedResource) {
        AbstractMetamodelVisitor renamer = new AbstractMetamodelVisitor() {
            @Override
            protected void visitENamedElement(ENamedElement eNamedElement) {
                eNamedElement.setName(Integer.toString(eNamedElement.hashCode()));
            }
        };
        copiedResource.getContents().forEach(renamer::visit);
    }

    /**
     * Generates Emfatic code from a model resource and splits it into lines with a string builder.
     * @throws ParsingException if the Emfatic writer fails.
     */
    private List<String> generateEmfaticCode(StringBuilder builder, Resource modelResource) throws ParsingException {
        Writer writer = new Writer();
        try {
            String code = writer.write(modelResource, null, null);
            builder.append(code);
        } catch (Exception exception) { // Emfatic does not properly handle errors, thus throws random exceptions.
            throw new ParsingException(file, "Emfatic view could not be generated!", exception);
        }
        return builder.toString().lines().toList();
    }

    /**
     * Calculates the index of the root package declaration, as it has unique syntax in Emfatic.
     */
    private int findIndexOfRootPackage(List<String> lines) {
        for (int index = 0; index < lines.size(); index++) {
            if (lines.get(index).matches(PACKAGE_REGEX)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Calculates the line index of a metamodel token from the emfatic code. If it cannot be found, the last index is used.
     */
    private int calculateLineIndexOf(EObject eObject, TokenType tokenType) {
        int line = Token.NO_VALUE;

        if (eObject instanceof ENamedElement element) {
            line = lineIndexOf(element);
            if (line != Token.NO_VALUE && isEndToken(tokenType)) {
                line = findEndIndexOf(line);
            }
        }

        if (line == Token.NO_VALUE) {
            return lastLineIndex;
        }
        lastLineIndex = line;
        return line;
    }

    /**
     * Locates the end index (closing character) for an element with a already known declaration index.
     */
    private int findEndIndexOf(int declarationIndex) {
        int indentation = indentationOf(lines.get(declarationIndex));
        if (declarationIndex > rootPackageIndex) { // exception for top level package
            for (int i = declarationIndex + 1; i < lines.size(); i++) {
                String nextLine = lines.get(i);
                if (nextLine.length() > indentation && CLOSING_CHAR == nextLine.charAt(indentation)) {
                    return i;
                }
            }
        }
        return lastLineIndex;
    }

    /**
     * Checks if a token is representing a end of a block, e.g. a closing bracket.
     */
    private boolean isEndToken(TokenType tokenType) {
        return tokenType instanceof MetamodelTokenType type && type.isEndToken();
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
        return elementToLine.computeIfAbsent(element, this::findLineIndexOf);
    }

    /**
     * Searches for the declaration of an element in the Emfatic code.
     */
    private int findLineIndexOf(ENamedElement element) {
        String hash = Integer.toString(modelCopier.get(element).hashCode());
        for (int index = 0; index < hashedLines.size(); index++) {
            String line = hashedLines.get(index);
            String trimmedLine = line.stripLeading();
            if (line.contains(hash) && isDeclaration(element, hash, trimmedLine)) {
                return index;
            }
        }
        return Token.NO_VALUE;
    }

    /**
     * Checks if a line (with leading whitespace removed) contains an element based on the elements hash.
     */
    private boolean isDeclaration(ENamedElement element, String hash, String line) {
        return isStructuralFeature(element, hash, line) || isTypedElement(element, hash, line) || isEnumLiteral(element, hash, line)
                || isType(hash, line);
    }

    private boolean isType(String hash, String line) {
        return line.matches(TYPE_KEYWORD_REGEX + hash + TYPE_SUFFIX_REGEX + ANYTHING_REGEX);
    }

    private boolean isEnumLiteral(ENamedElement element, String hash, String line) {
        return element instanceof EEnumLiteral && line.matches(hash + ANYTHING_REGEX);
    }

    private boolean isTypedElement(ENamedElement element, String hash, String line) {
        return element instanceof EOperation && element instanceof EParameter && line.matches(FEATURE_KEYWORD_REGEX + hash + ANYTHING_REGEX);
    }

    private boolean isStructuralFeature(ENamedElement element, String hash, String line) {
        return element instanceof ETypedElement && line.matches(FEATURE_KEYWORD_REGEX + hash + LINE_SUFFIX_REGEX);
    }

}
