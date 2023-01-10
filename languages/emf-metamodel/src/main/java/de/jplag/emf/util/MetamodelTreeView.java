package de.jplag.emf.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import de.jplag.TokenTrace;
import de.jplag.emf.MetamodelToken;

/**
 * Very basic tree view representation of an EMF metamodel or model.
 */
public class MetamodelTreeView extends AbstractModelView {
    private final List<String> lines;
    private final Map<EObject, TokenTrace> objectToLine;

    /**
     * Creates a tree view for a metamodel.
     * @param file is the path to the metamodel.
     */
    public MetamodelTreeView(File file, Resource modelResource) {
        super(file);
        lines = new ArrayList<>();
        objectToLine = new HashMap<>();
        TreeViewBuilder visitor = new TreeViewBuilder();
        modelResource.getContents().forEach(visitor::visit);
    }

    /**
     * Adds a token to the view, thus adding the index information to the token. Returns a new token enriched with the index
     * metadata.
     * @param token is the token to add.
     */
    @Override
    public MetamodelToken convertToMetadataEnrichedToken(MetamodelToken token) {
        if (token.getEObject().isPresent()) {
            EObject object = token.getEObject().get();
            TokenTrace trace = objectToLine.get(object);
            return new MetamodelToken(token.getType(), token.getFile(), trace, token.getEObject());
        }
        return new MetamodelToken(token.getType(), token.getFile());
    }

    private final class TreeViewBuilder extends AbstractMetamodelVisitor {
        private static final String INDENTATION = "  ";
        private static final String NAME_SEPARATOR = " : ";

        private TreeViewBuilder() {
            super(false);
        }

        @Override
        protected void visitEObject(EObject eObject) {
            String prefix = INDENTATION.repeat(getCurrentTreeDepth());
            String line = prefix;
            if (eObject instanceof ENamedElement element) {
                line += element.getName() + NAME_SEPARATOR;
            }
            line += eObject.eClass().getName();

            lines.add(line);
            viewBuilder.append(line + System.lineSeparator());
            // line and column values are one-indexed
            TokenTrace trace = new TokenTrace(lines.size(), prefix.length() + 1, line.trim().length());
            objectToLine.put(eObject, trace);
        }
    }

}
