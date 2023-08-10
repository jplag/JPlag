package de.jplag.merging;

import de.jplag.Match;

/*
 * This class realizes a pair of neighboring matches, named upperMatch and lowerMatch
 */
public record Neighbor(Match upperMatch, Match lowerMatch) {
}