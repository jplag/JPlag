import java.util.Random;

/**
 * PROJECT P: BioLife Simulation
 * A standard implementation of Conway's Game of Life.
 * Uses a 2D grid and standard nested loops.
 */
public class BioLifeSim {

    private static final int ROWS = 10;
    private static final int COLS = 10;
    private static boolean[][] grid = new boolean[ROWS][COLS];

    public static void main(String[] args) {
        System.out.println("--- Micro-Organism Lab v1.4 ---");
        initializeCulture();

        // Simulate 3 Generations
        for (int gen = 0; gen < 3; gen++) {
            System.out.println("Generation: " + gen);
            displayGrid();
            evolve();
        }

        //DeadCodeStart
        // Deprecated 3D rendering pipeline
        // This code is unreachable but was part of the original engine
        if (false) {
            int zAxis = 5;
            System.out.println("Rendering Volumetric Cube at Z=" + zAxis);
            // Simulated heavy calculation
            double vertex = Math.pow(zAxis, 3);
        }
        //DeadCodeEnd
    }

    private static void initializeCulture() {
        Random rng = new Random();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = rng.nextBoolean();
            }
        }
    }

    private static void evolve() {
        boolean[][] nextGen = new boolean[ROWS][COLS];

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int neighbors = countNeighbors(r, c);

                if (grid[r][c]) {
                    // Rule 1: Underpopulation (<2 dies)
                    // Rule 2: Overcrowding (>3 dies)
                    // Rule 3: Survival (2 or 3 lives)
                    if (neighbors < 2 || neighbors > 3) {
                        nextGen[r][c] = false;
                    } else {
                        nextGen[r][c] = true;
                    }
                } else {
                    // Rule 4: Reproduction (3 becomes alive)
                    if (neighbors == 3) {
                        nextGen[r][c] = true;
                    }
                }
            }
        }
        grid = nextGen;
    }

    private static int countNeighbors(int row, int col) {
        int count = 0;
        // Check 3x3 grid around cell
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    //continue
                } else {
                    int r = row + i;
                    int c = col + j;

                    // Boundary checks
                    if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                        if (grid[r][c]) count++;
                    }
                }
            }
        }
        return count;
    }

    private static void displayGrid() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                System.out.print(grid[r][c] + ". ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
