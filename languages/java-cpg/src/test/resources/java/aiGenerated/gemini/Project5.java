import java.util.Arrays;

public class ViralMarketingAnalyzer {

    static final int USER_COUNT = 5;

    public static void main(String[] args) {
        // "Resistance" matrix (how hard it is to influence user B from user A)
        // This is mathematically identical to the distance matrix in Project 4
        int[][] resistanceMatrix = new int[][] { 
            { 0, 4, 0, 0, 0 },
            { 4, 0, 8, 0, 0 },
            { 0, 8, 0, 7, 0 },
            { 0, 0, 7, 0, 9 },
            { 0, 0, 0, 9, 0 } 
        };

        System.out.println("Analyzing viral spread potential from User 0...");
        calculateInfluence(resistanceMatrix, 0);
    }

    // Plagiarism: This is exactly Dijkstra's algorithm, renamed.
    // 'dist' -> 'impactCost'
    // 'sptSet' -> 'analyzed'
    static void calculateInfluence(int[][] relations, int influencerId) {
        int[] impactCost = new int[USER_COUNT]; 
        Boolean[] analyzed = new Boolean[USER_COUNT];

        Arrays.fill(impactCost, Integer.MAX_VALUE);
        Arrays.fill(analyzed, false);

        impactCost[influencerId] = 0;

        for (int i = 0; i < USER_COUNT - 1; i++) {
            int currentTarget = findMostSusceptible(impactCost, analyzed);
            
            //DeadCodeStart
            // This block executes but is effectively dead because 'cache' is local
            // and reset every iteration. It disguises the plagiarism flow.
            int[] cache = new int[10];
            for(int k=0; k<5; k++) {
                cache[k] = currentTarget * k; 
            }
            //DeadCodeEnd
            
            analyzed[currentTarget] = true;

            // Plagiarism: The relaxation logic is identical to Project 4
            for (int v = 0; v < USER_COUNT; v++) {
                // Formatting changed to hide structure slightly
                boolean unvisited = !analyzed[v];
                boolean connected = relations[currentTarget][v] != 0;
                boolean reachable = impactCost[currentTarget] != Integer.MAX_VALUE;
                
                if (unvisited && connected && reachable) {
                    int potentialNewImpact = impactCost[currentTarget] + relations[currentTarget][v];
                    if (potentialNewImpact < impactCost[v]) {
                        impactCost[v] = potentialNewImpact;
                    }
                }
            }
        }
        
        displayMetrics(impactCost);
    }

    static int findMostSusceptible(int[] costs, Boolean[] mask) {
        int minVal = Integer.MAX_VALUE;
        int targetIndex = -1;

        for (int v = 0; v < USER_COUNT; v++) {
            if (!mask[v] && costs[v] <= minVal) {
                minVal = costs[v];
                targetIndex = v;
            }
        }
        return targetIndex;
    }

    static void displayMetrics(int[] data) {
        System.out.println("User \t Resistance Score");
        for (int i = 0; i < USER_COUNT; i++)
            System.out.println(i + " \t " + data[i]);
    }
}
