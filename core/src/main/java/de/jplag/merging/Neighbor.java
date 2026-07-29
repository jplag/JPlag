package de.jplag.merging;

import de.jplag.Match;

/**
 * This class realizes a pair of neighboring matches, named upperMatch and lowerMatch. Two matches are considered
 * neighbors, if they begin directly after one another in the left submission and in the right submission.
 * @param upperMatch is the first match in both sequences.
 * @param lowerMatch is the second match in both sequences.
 */
public record Neighbor(Match upperMatch, Match lowerMatch) {
}