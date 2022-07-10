package de.jplag.golang.grammar;

import de.jplag.golang.GoToken;

public class GoTokenUtils {

    private static final int NOT_SET = -1;
    private static final String NOT_SET_STRING = "";

    public static GoToken getDummyToken(int type) {
        return new GoToken(type, NOT_SET_STRING, NOT_SET, NOT_SET, NOT_SET);
    }

    public static GoToken getDummyToken(int type, String fileName) {
        return new GoToken(type, fileName, NOT_SET, NOT_SET, NOT_SET);
    }
}
