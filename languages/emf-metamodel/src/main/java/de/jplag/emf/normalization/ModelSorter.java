package de.jplag.emf.normalization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;

import de.jplag.emf.util.AbstractMetamodelVisitor;

/**
 * Utility class that sorts all containment references of an EMF model or metamodel.
 */
public final class ModelSorter extends AbstractMetamodelVisitor {

    /**
     * Creates a model sorter.
     */
    private ModelSorter() {
        // private constructor for non-instantiability.
    }

    /*
     * TODO TS: Currently this only supports the normalization for the handcrafted rules, not the dynamic ones.
     */

    /**
     * Sorts the given model or metamodel.
     * @param modelResource is the resource of the model or metamodel.
     */
    public static void sort(Resource modelResource) {
        modelResource.getContents().forEach(new ModelSorter()::visit);
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
                    sortedContent.sort(new ContainmentOrderNormalizer(sortedContent));
                    containmentList.clear();
                    containmentList.addAll(sortedContent);
                }
            }
        }
    }

}
