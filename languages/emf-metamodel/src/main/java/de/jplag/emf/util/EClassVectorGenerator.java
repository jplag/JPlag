package de.jplag.emf.util;

import java.util.ArrayList;
import java.util.Collections;
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

    public List<Double> generateEClassHistogram(Iterator<EObject> modelElements) {
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
        return normalize(vector);
    }

    public static List<Double> normalize(List<Integer> vector) {
        int magnitudeSquared = 0;
        for (int i : vector) {
            magnitudeSquared += i * i;
        }
        double magnitude = Math.sqrt(magnitudeSquared);
        if (magnitude == 0) {
            // Vector has zero magnitude
            return Collections.nCopies(vector.size(), 0.0);
        }
        List<Double> normalizedVector = new ArrayList<>();
        for (int i : vector) {
            double normalizedValue = i / magnitude;
            normalizedVector.add(normalizedValue);
        }
        return normalizedVector;
    }
}
