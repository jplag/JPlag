package de.jplag.cli.server.configurationDTO;

import java.util.Map;

public record SubmissionParsing(int newSubmissions, int oldSubmissions, Map<String, Integer> detectedLanguages) {
}
