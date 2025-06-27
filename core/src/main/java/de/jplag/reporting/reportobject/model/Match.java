package de.jplag.reporting.reportobject.model;

public record Match(String firstFileName, String secondFileName, CodePosition startInFirst, CodePosition endInFirst, CodePosition startInSecond,
        CodePosition endInSecond, int lengthOfFirst, int lengthOfSecond) {
}
