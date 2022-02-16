package de.jplag.clustering;

import java.util.Collection;
import java.util.function.BiFunction;

public interface ClusteringResult<T> {

    Collection<Cluster<T>> getClusters();

    /**
     * Community strength of the clustering. The expectation of community strength in a random graph is zero and it can not
     * exceed one. It's the sum of it's clusters {@link Cluster#getCommunityStrength}. If the underlying network is not
     * changed, a higher community strength denotes a better clustering. See: Finding and evaluating community structure in
     * networks, M. E. J. Newman and M. Girvan, Phys. Rev. E 69, 026113 – Published 26 February 2004, Doi:
     * 10.1103/PhysRevE.69.026113 It's called modularity in that paper.
     * @return community strength
     */
    float getCommunityStrength();

    int size();

    default float getWorth(BiFunction<T, T, Float> similarity) {
        return (float) getClusters().stream().mapToDouble(c -> c.getWorth(similarity)).map(worth -> Double.isFinite(worth) ? worth : 0).average()
                .getAsDouble();
    }

}
