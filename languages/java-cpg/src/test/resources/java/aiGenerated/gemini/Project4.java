import java.util.Arrays;

public class NetworkRouter {

    static final int V = 5; // Number of vertices

    public static void main(String[] args) {
        // Adjacency matrix representation of the network
        // 0 means no direct link, non-zero is the latency cost
        int graph[][] = new int[][] { 
            { 0, 4, 0, 0, 0 },
            { 4, 0, 8, 0, 0 },
            { 0, 8, 0, 7, 0 },
            { 0, 0, 7, 0, 9 },
            { 0, 0, 0, 9, 0 } 
        };

        System.out.println("Calculating shortest paths from Node 0...");
        dijkstra(graph, 0);
        
        //DeadCodeStart
        // Opaque predicate: checks a mathematical impossibility to hide dead code
        // The JVM might optimize this away, but source-wise it looks like logic
        int check = (V * 2) % 2;
        if (check != 0) {
            System.err.println("Critical Error: Topology unstable.");
            runDiagnostics(graph); 
        }
        //DeadCodeEnd
    }

    static void dijkstra(int graph[][], int src) {
        int dist[] = new int[V]; 
        Boolean sptSet[] = new Boolean[V];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(sptSet, false);

        dist[src] = 0;

        for (int count = 0; count < V - 1; count++) {
            int u = minDistance(dist, sptSet);
            sptSet[u] = true;

            for (int v = 0; v < V; v++)
                if (!sptSet[v] && graph[u][v] != 0 && 
                    dist[u] != Integer.MAX_VALUE && 
                    dist[u] + graph[u][v] < dist[v]) {
                    dist[v] = dist[u] + graph[u][v];
                }
        }
        printSolution(dist);
    }

    static int minDistance(int dist[], Boolean sptSet[]) {
        int min = Integer.MAX_VALUE, min_index = -1;

        for (int v = 0; v < V; v++)
            if (sptSet[v] == false && dist[v] <= min) {
                min = dist[v];
                min_index = v;
            }

        return min_index;
    }

    static void printSolution(int dist[]) {
        System.out.println("Vertex \t Distance from Source");
        for (int i = 0; i < V; i++)
            System.out.println(i + " \t " + dist[i]);
    }

    //DeadCodeStart
    static void runDiagnostics(int graph[][]) {
        // Complex looking loop that effectively does nothing useful
        for(int i=0; i<V; i++) {
            for(int j=0; j<V; j++) {
                int temp = graph[i][j] ^ 255; // Bitwise operation
                // No side effect, result is lost
            }
        }
    }
    //DeadCodeEnd
}
