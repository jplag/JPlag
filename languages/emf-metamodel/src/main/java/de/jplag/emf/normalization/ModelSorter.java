package de.jplag.emf.normalization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;

import de.jplag.emf.parser.ModelingElementTokenizer;
import de.jplag.emf.util.AbstractMetamodelVisitor;

/**
 * Utility class that sorts all containment references of an EMF model or metamodel.
 */
public class ModelSorter extends AbstractMetamodelVisitor {

    private final ModelingElementTokenizer tokenizer;

    /**
     * Creates a model sorter.
     */
    private ModelSorter(ModelingElementTokenizer tokenizer) {
        this.tokenizer = tokenizer; // private constructor to hide visitor functionality.
    }

    /**
     * Sorts the given model or metamodel.
     * @param modelResource is the resource of the model or metamodel.
     * @param tokenizer provides the tokenization rules for the sorting.
     */
    public static void sort(Resource modelResource, ModelingElementTokenizer tokenizer) {
        modelResource.getContents().forEach(new ModelSorter(tokenizer)::visit);
    }

    @Override
    protected void visitEObject(EObject eObject) {
        for (EReference reference : eObject.eClass().getEAllContainments()) {
            if (reference.isMany()) {
                Object containment = eObject.eGet(reference);
                if (containment instanceof List) {
                    @SuppressWarnings("unchecked") // There is no cleaner way
                    List<EObject> containmentList = (List<EObject>) containment;
                    List<EObject> sortedContent = new ArrayList<>(containmentList);
                    sortedContent.sort(new ContainmentOrderNormalizer(sortedContent, tokenizer));
                    containmentList.clear();
                    containmentList.addAll(sortedContent);
                }
            }
        }
    }

}
