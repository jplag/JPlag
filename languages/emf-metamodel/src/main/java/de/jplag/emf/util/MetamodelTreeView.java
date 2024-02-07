package de.jplag.emf.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
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
        Optional<EObject> optionalEObject = token.getEObject();
        if (optionalEObject.isPresent()) {
            EObject object = optionalEObject.get();
            TokenTrace trace = objectToLine.get(object);
            return new MetamodelToken(token.getType(), token.getFile(), trace, optionalEObject);
        }
        return new MetamodelToken(token.getType(), token.getFile());
    }

    private final class TreeViewBuilder extends AbstractMetamodelVisitor {
        private static final String IDENTIFIER_FEATURE = "name";
        private static final String INDENTATION = "  ";
        private static final String NAME_SEPARATOR = " : ";

        @Override
        protected void visitEObject(EObject eObject) {
            String prefix = INDENTATION.repeat(getCurrentTreeDepth());
            String line = prefix;
            if (eObject instanceof ENamedElement element) {
                line += element.getName() + NAME_SEPARATOR;
            } else {
                for (EStructuralFeature feature : eObject.eClass().getEAllStructuralFeatures()) {
                    if (feature.getName().toLowerCase().matches(IDENTIFIER_FEATURE) && eObject.eGet(feature) != null) {
                        line += eObject.eGet(feature).toString() + NAME_SEPARATOR;
                    }
                }
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
