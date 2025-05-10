package de.jplag.reporting.reportobject.model;

public record BaseCodeMatch(String fileName, CodePosition start, CodePosition end, int tokens) {
}
