package de.jplag.merging;

import de.jplag.Match;

/**
 * This class realizes a pair of neighboring matches, named upperMatch and lowerMatch. Two matches are considered
 * neighbors, if they begin directly after one another in the left submission and in the right submission.
 */
public record Neighbor(Match upperMatch, Match lowerMatch) {
}