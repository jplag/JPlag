package de.jplag.emf.dynamic;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;

import de.jplag.TokenConstants;

/**
 * Utility class for the dynamic creation of token constants. Replaces a handcrafted token set.
 * @author Timur Saglam
 */
public class DynamicMetamodelTokenConstants implements TokenConstants {

    private static Map<EClass, Integer> eClassToTokenType = new HashMap<>();
    private static Map<Integer, EClass> tokenTypeToEClass = new HashMap<>();

    private static int tokenTypeIndex = 2;

    private DynamicMetamodelTokenConstants() {
        // private constructor for non-instantiability.
    }

    public static int getTokenType(EClass eClass) {
        if (eClassToTokenType.containsKey(eClass)) {
            return eClassToTokenType.get(eClass);
        }
        Integer tokenType = tokenTypeIndex++;
        eClassToTokenType.put(eClass, tokenType);
        tokenTypeToEClass.put(tokenType, eClass);
        return tokenType;
    }

    public static String getTokenString(int tokenType) {
        return tokenTypeToEClass.get(tokenType).getName();
    }

    /**
     * Returns the current size of the dynamic token set.
     * @return the number of known tokens.
     */
    public static int getNumberOfTokens() {
        return eClassToTokenType.size();
    }

}
