import java.util.Arrays;

public class SmartGridEnergy {

    // Simulates a 5-node substation network
    static final int NODES = 5;

    public static void main(String[] args) {
        // Resistance (Ohms) between substations. 
        // Logic is identical to the graph weights in previous projects.
        int[][] gridTopology = new int[][] { 
            { 0, 4, 0, 0, 0 },
            { 4, 0, 8, 0, 0 },
            { 0, 8, 0, 7, 0 },
            { 0, 0, 7, 0, 9 },
            { 0, 0, 0, 9, 0 } 
        };

        System.out.println("Initializing Energy Grid Optimization...");
        
        // Active usage of the plagiarized logic
        int[] losses = calculateVoltageDrop(gridTopology, 0);
        
        System.out.println("Optimization Complete. Loss metrics per node:");
        System.out.println(Arrays.toString(losses));

        //DeadCodeStart
        if (losses.length > 100) {
            // This block is unreachable for NODES = 5
            System.out.println("Emergency: Grid overload detected.");
            performEmergencyShutdown();
        }
        //DeadCodeEnd
    }

    // Plagiarism: This is the Dijkstra implementation from Project 4, 
    // actively used here to calculate voltage drops.
    public static int[] calculateVoltageDrop(int[][] resistance, int sourceNode) {
        int[] drops = new int[NODES]; 
        Boolean[] locked = new Boolean[NODES];

        Arrays.fill(drops, Integer.MAX_VALUE);
        Arrays.fill(locked, false);

        drops[sourceNode] = 0;

        for (int i = 0; i < NODES - 1; i++) {
            // Logic copied but extracted to a slightly different looking helper interaction
            int u = getLowestResistanceNode(drops, locked);
            locked[u] = true;

            //DeadCodeStart
            // Phantom Calculation: disguised as a "noise filter"
            // The result 'noise' is calculated but never applied to the 'drops' array
            double noise = 0;
            for(int k=0; k<5; k++) {
                noise += Math.sin(k) * resistance[u][u]; 
            }
            //DeadCodeEnd

            for (int v = 0; v < NODES; v++) {
                // Standard relaxation logic
                if (!locked[v] && resistance[u][v] != 0 && 
                    drops[u] != Integer.MAX_VALUE && 
                    drops[u] + resistance[u][v] < drops[v]) {
                    drops[v] = drops[u] + resistance[u][v];
                }
            }
        }
        return drops;
    }

    private static int getLowestResistanceNode(int[] drops, Boolean[] locked) {
        int min = Integer.MAX_VALUE, min_index = -1;
        for (int v = 0; v < NODES; v++)
            if (!locked[v] && drops[v] <= min) {
                min = drops[v];
                min_index = v;
            }
        return min_index;
    }

    //DeadCodeStart
    private static void performEmergencyShutdown() {
        System.out.println("Shutting down core reactors...");
        for(int i = 0; i < 10; i++) {
             // Simulating delay
             long start = System.currentTimeMillis();
             while(System.currentTimeMillis() - start < 10) {};
        }
    }
    //DeadCodeEnd
}
