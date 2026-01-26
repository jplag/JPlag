package de.jplag.emf.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import de.jplag.TokenTrace;
import de.jplag.TokenType;

/**
 * Very basic tree view representation of an EMF metamodel or model.
 */
public class GenericEmfTreeView extends AbstractModelView {
    private final List<String> lines;
    private final Map<EObject, TokenTrace> objectToLine;
    private final ModelingElementIdentifierManager identifierManager;

    /**
     * Creates a tree view for a metamodel.
     * @param file is the path to the metamodel.
     * @param modelResource is the EMF resource in which the model is loaded.
     */
    public GenericEmfTreeView(File file, Resource modelResource) {
        super(file);
        lines = new ArrayList<>();
        objectToLine = new HashMap<>();
        identifierManager = new ModelingElementIdentifierManager();
        TreeViewBuilder visitor = new TreeViewBuilder();
        modelResource.getContents().forEach(visitor::visit);
    }

    @Override
    public TokenTrace getTokenTrace(EObject modelElement, TokenType tokenType) {
        return objectToLine.get(modelElement);
    }

    private final class TreeViewBuilder extends AbstractMetamodelVisitor {
        private static final String IDENTIFIER_PREFIX = " #";
        private static final String VALUE_ASSIGNMENT = "=";
        private static final String COLLECTION_PREFIX = "[";
        private static final String COLLECTION_SUFFIX = "]";
        private static final String COLLECTION_DELIMITER = ", ";
        private static final int ABBREVIATION_LIMIT = 20;
        private static final String ABBREVIATION_SUFFIX = "...";
        private static final String TEXT_AFFIX = "\"";
        private static final String IDENTIFIER_REGEX = "name|identifier";
        private static final String INDENTATION = "  ";

        @Override
        protected void visitEObject(EObject eObject) {
            String prefix = INDENTATION.repeat(getCurrentTreeDepth());
            StringBuilder line = new StringBuilder(prefix);

            line.append(eObject.eClass().getName());  // Build element type
            line.append(IDENTIFIER_PREFIX);
            line.append(identifierManager.getIdentifier(eObject));
            visitStructuralFeatures(eObject, line);  // Build element features

            lines.add(line.toString());
            viewBuilder.append(line + System.lineSeparator());
            // line and column values are one-indexed
            TokenTrace trace = new TokenTrace(lines.size(), prefix.length() + 1, line.toString().trim().length());
            objectToLine.put(eObject, trace);
        }

        private void visitStructuralFeatures(EObject eObject, StringBuilder line) {
            List<EStructuralFeature> structuralFeatures = eObject.eClass().getEAllStructuralFeatures();
            if (!structuralFeatures.isEmpty()) {
                line.append(": ");
                StringJoiner joiner = new StringJoiner(COLLECTION_DELIMITER);
                for (EStructuralFeature feature : structuralFeatures) {
                    Object value = eObject.eGet(feature);
                    String name = featureValueToString(value);
                    if (name != null) {
                        joiner.add(feature.getName() + VALUE_ASSIGNMENT + name);
                    }
                }
                line.append(joiner.toString());

            }
        }

        private String featureValueToString(Object value) {
            String name = null;
            if (value != null) {
                if (value instanceof EObject featureValue) {
                    List<String> valueIdentifiers = deriveNameOrIdentifers(featureValue);

                    if (!valueIdentifiers.isEmpty()) {
                        name = TEXT_AFFIX + valueIdentifiers.get(0) + TEXT_AFFIX;
                    } else {
                        name = featureValue.eClass().getName() + IDENTIFIER_PREFIX + identifierManager.getIdentifier(featureValue);
                    }
                } else if (value instanceof Collection<?> multipleValues) {
                    name = valueListToString(multipleValues);
                } else {
                    name = value.toString();
                    name = name.length() > ABBREVIATION_LIMIT ? name.substring(0, ABBREVIATION_LIMIT) + ABBREVIATION_SUFFIX : name;
                    name = TEXT_AFFIX + name + TEXT_AFFIX;
                }
            }
            return name;
        }

        private String valueListToString(Collection<?> multipleValues) {
            String name = null;
            if (!multipleValues.isEmpty()) {
                name = COLLECTION_PREFIX;
                StringJoiner joiner = new StringJoiner(COLLECTION_DELIMITER);
                for (Object innerValue : multipleValues) {
                    joiner.add(featureValueToString(innerValue));
                }
                name += joiner.toString() + COLLECTION_SUFFIX;
            }
            return name;
        }

        private static List<String> deriveNameOrIdentifers(EObject eObject) {
            List<String> names = new ArrayList<>();
            if (eObject instanceof ENamedElement element) {
                names.add(element.getName());
            } else {
                for (EStructuralFeature feature : eObject.eClass().getEAllStructuralFeatures()) {
                    if (feature.getName().toLowerCase().matches(IDENTIFIER_REGEX) && eObject.eGet(feature) != null) {
                        names.add(eObject.eGet(feature).toString());
                    }
                }
            }
            return names;
        }
    }

}
