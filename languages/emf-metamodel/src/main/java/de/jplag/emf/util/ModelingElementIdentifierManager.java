package de.jplag.emf.util;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * This class provides type-unique identifiers for EObjects.
 */
public class ModelingElementIdentifierManager {

    private final Map<EClass, Set<EObject>> elementToIdentifer;

    /**
     * Creates the identifier manager. Identifers are only unique if managed by the same instance.
     */
    public ModelingElementIdentifierManager() {
        elementToIdentifer = new HashMap<>();
    }

    /**
     * Returns the type-unique identifier for any EMF modeling element.
     * @param element is the modeling element for which the identifier is requested.
     * @return the identifier, that is uniquen for all elements of the same EClass.
     */
    public int getIdentifier(EObject element) {
        Set<EObject> elements = elementToIdentifer.computeIfAbsent(element.eClass(), key -> new LinkedHashSet<>());
        int index = 0;
        for (EObject containedElement : elements) {
            if (containedElement.equals(element)) {
                return index;
            }
            ++index;
        }
        elements.add(element);
        return index;
    }
}
