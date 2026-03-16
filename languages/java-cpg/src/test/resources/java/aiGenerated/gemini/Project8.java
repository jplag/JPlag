import java.util.Arrays;

public class SocialGraphMiner {

    private static final int MEMBERS = 5;

    public static void main(String[] args) {
        // Represents "Closeness" (inverse of distance) or "Hops"
        // Same data structure as previous graph examples
        int[][] socialNetwork = {
                {0, 4, 0, 0, 0},
                {4, 0, 8, 0, 0},
                {0, 8, 0, 7, 0},
                {0, 0, 7, 0, 9},
                {0, 0, 0, 9, 0}
        };

        System.out.println("Mapping social degrees of separation...");

        // Active usage of the logic
        mapConnections(socialNetwork, 0);

        //DeadCodeStart
        // This method call looks legitimate but the method implementation does nothing effective
        verifyPrivacyCompliance(socialNetwork);
        //DeadCodeEnd
    }

    // Plagiarism: Logic is identical to Project 4/7
    // Renamed variables to fit "Social Media" context
    static void mapConnections(int[][] graph, int startUser) {
        int[] separationDegrees = new int[MEMBERS];
        Boolean[] visited = new Boolean[MEMBERS];

        Arrays.fill(separationDegrees, Integer.MAX_VALUE);
        Arrays.fill(visited, false);

        separationDegrees[startUser] = 0;

        for (int count = 0; count < MEMBERS - 1; count++) {
            int u = findNextUser(separationDegrees, visited);
            visited[u] = true;

            for (int v = 0; v < MEMBERS; v++) {
                //DeadCodeStart
                // Redundant check: graph[u][v] >= 0 is always true for this data
                if (graph[u][v] < 0) {
                    System.out.println("Negative edge detected");
                }
                //DeadCodeEnd

                if (!visited[v] && graph[u][v] != 0 &&
                        separationDegrees[u] != Integer.MAX_VALUE &&
                        separationDegrees[u] + graph[u][v] < separationDegrees[v]) {

                    separationDegrees[v] = separationDegrees[u] + graph[u][v];
                }
            }
        }

        printDegrees(separationDegrees);
    }

    static int findNextUser(int[] deg, Boolean[] vis) {
        int min = Integer.MAX_VALUE, idx = -1;

        for (int v = 0; v < MEMBERS; v++)
            if (!vis[v] && deg[v] <= min) {
                min = deg[v];
                idx = v;
            }
        return idx;
    }

    static void printDegrees(int[] deg) {
        System.out.println("User ID \t Separation Cost");
        for (int i = 0; i < MEMBERS; i++)
            System.out.println(i + " \t\t " + deg[i]);
    }

    //DeadCodeStart
    // This looks like a complex verification algorithm, but it just loops
    // and modifies local variables that are discarded.
    static void verifyPrivacyCompliance(int[][] data) {
        int complianceScore = 0;
        for (int[] row : data) {
            for (int val : row) {
                if (val > 0) complianceScore++;
            }
        }
        // Compliance score is calculated but never returned or printed
    }
    //DeadCodeEnd
}
