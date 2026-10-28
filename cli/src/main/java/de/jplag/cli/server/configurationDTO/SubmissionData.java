package de.jplag.cli.server.configurationDTO;

import java.util.List;

public record SubmissionData(List<String> newSubmissions, List<String> oldSubmissions, String baseCode) {
}
