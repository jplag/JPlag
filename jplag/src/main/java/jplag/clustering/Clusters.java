package jplag.clustering;

import java.util.ArrayList;
import java.util.Vector;

import jplag.JPlag;
import jplag.Submission;
import jplag.options.ClusterType;

/**
 * This class calculates, based on the similarity matrix, the hierarchical clustering of the documents, using MIN, MAX
 * and AVR methods.
 */
public class Clusters {

    public Vector<Submission> submissions;
    public float maxMergeValue = 0;
    private JPlag program;

    public Clusters(JPlag program) {
        this.program = program;
    }

    public Cluster calculateClustering(Vector<Submission> submissions) {
        this.submissions = submissions;
        Cluster clusters = null;

        switch (this.program.getOptions().getClusterType()) {
        case MAX:
        case MIN:
        case AVG:
            clusters = minMaxAvrClustering();
            break;
        default:
        }

        return clusters;
    }

    /**
     * Min clustering...
     */
    public Cluster minMaxAvrClustering() {
        int nrOfSubmissions = submissions.size();
        boolean minClustering = (this.program.getOptions().getClusterType() == ClusterType.MIN);
        boolean maxClustering = (this.program.getOptions().getClusterType() == ClusterType.MAX);
        SimilarityMatrix simMatrix = this.program.similarity;

        ArrayList<Cluster> clusters = new ArrayList<Cluster>(submissions.size());
        for (int i = 0; i < nrOfSubmissions; i++) {
            clusters.add(new Cluster(i, this));
        }

        while (clusters.size() > 1) {
            int indexA = -1, indexB = -1;
            float maxSim = -1;
            int nrOfClusters = clusters.size();

            // find similarity
            for (int a = 0; a < (nrOfClusters - 1); a++) {
                Cluster cluster = clusters.get(a);
                for (int b = a + 1; b < nrOfClusters; b++) {
                    float sim;
                    if (minClustering) {
                        sim = cluster.maxSimilarity(clusters.get(b), simMatrix);
                    } else if (maxClustering) {
                        sim = cluster.minSimilarity(clusters.get(b), simMatrix);
                    } else {
                        sim = cluster.avrSimilarity(clusters.get(b), simMatrix);
                    }
                    if (sim > maxSim) {
                        maxSim = sim;
                        indexA = a;
                        indexB = b;
                    }
                }
            }

            if (maxSim > maxMergeValue) {
                maxMergeValue = maxSim;
            }

            // now merge these clusters
            Cluster clusterA = clusters.get(indexA);
            Cluster clusterB = clusters.get(indexB);
            clusters.remove(clusterA);
            clusters.remove(clusterB);
            clusters.add(new Cluster(clusterA, clusterB, maxSim, this));
        }
        return clusters.get(0);
    }
}
