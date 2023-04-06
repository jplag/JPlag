package de.jplag.emf.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import de.jplag.emf.MetamodelTokenType;
import de.jplag.emf.parser.TokenExtractionRules;

public class EClassVectorGenerator {
    private Set<MetamodelTokenType> allClasses;

    public int eClassSetSize() {
        return allClasses.size();
    }

    public EClassVectorGenerator(List<EObject> allObjects) { // TODO take objects
        this.allClasses = new HashSet<>();
        allClasses.addAll(TokenExtractionRules.elements2Tokens(allObjects));
    }

    public List<Integer> generateEClassHistogram(Iterator<EObject> modelElements) {
        Map<MetamodelTokenType, Integer> occurences = new HashMap<>();

        while (modelElements.hasNext()) {
            EObject eObject = modelElements.next();
            // occurences.merge(eObject.eClass(), 1, Integer::sum);
            occurences.merge(TokenExtractionRules.element2Token(eObject), 1, Integer::sum);
        }
        List<Integer> vector = new ArrayList<>(allClasses.size());
        for (MetamodelTokenType type : allClasses) {
            vector.add(occurences.getOrDefault(type, 0));
        }
        return vector;
    }
}
