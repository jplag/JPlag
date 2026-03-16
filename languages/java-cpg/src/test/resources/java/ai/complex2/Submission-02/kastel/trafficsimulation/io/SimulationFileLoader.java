package edu.kit.kastel.trafficsimulation.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * File loader for simulation files.
 *
 * @author Lucas Alber
 * @version 1.0
 */
public final class SimulationFileLoader {

    /**
     * The filename for the simulation data representing streets.
     */
    public static final String FILENAME_STREETS = "streets.sim";
    /**
     * The filename for the simulation data representing crossings.
     */
    public static final String FILENAME_CROSSINGS = "crossings.sim";
    /**
     * The filename for the simulation data representing cars.
     */
    public static final String FILENAME_CARS = "cars.sim";


    private final Path folderPath;


    /**
     * Creates a new {@link SimulationFileLoader}.
     *
     * @param      folderPath  a path to a folder containing the three simulation files.
     * @throws     IOException if the folder does not exist or the path is pointing to a normal file.
     */
    public SimulationFileLoader(final String folderPath) throws IOException {
        this.folderPath = Path.of(folderPath).normalize().toAbsolutePath();
        final File folder = this.folderPath.toFile();

        if (!folder.exists()) {
            throw new IOException(String.format("folder %s does not exist.", this.folderPath.toString()));
        }
        if (!folder.isDirectory()) {
            throw new IOException(String.format("%s is not a directory.", this.folderPath.toString()));
        }
    }


    /**
     * Loads the simulation file {@value FILENAME_STREETS} and returns the lines as list of String.
     *
     * The returned value is never {@code null}. An empty list is returned, if the file is empty.
     *
     * @return     the lines of the file as list of String.
     *
     * @throws     IOException  if the file does not exist or points to a directory.
     */
    public List<String> loadStreets() throws IOException {
        return loadSimulationFile(FILENAME_STREETS);
    }

    /**
     * Loads the simulation file {@value FILENAME_CROSSINGS} and returns the lines as list of String.
     *
     * The returned value is never {@code null}. An empty list is returned, if the file is empty.
     *
     * @return     the lines of the file as list of String.
     *
     * @throws     IOException  if the file does not exist or points to a directory.
     */
    public List<String> loadCrossings() throws IOException {
        return loadSimulationFile(FILENAME_CROSSINGS);
    }

    /**
     * Loads the simulation file {@value FILENAME_CARS} and returns the lines as list of String.
     *
     * The returned value is never {@code null}. An empty list is returned, if the file is empty.
     *
     * @return     the lines of the file as list of String.
     *
     * @throws     IOException  if the file does not exist or points to a directory.
     */
    public List<String> loadCars() throws IOException {
        return loadSimulationFile(FILENAME_CARS);
    }


    private List<String> loadSimulationFile(String fileName) throws IOException {
        final Path filePath = this.folderPath.resolve(Path.of(fileName));
        final File file = filePath.toFile();

        if (!file.exists()) {
            throw new IOException(String.format("file %s does not exist.", filePath.toString()));
        }
        if (!file.isFile()) {
            throw new IOException(String.format("file %s is not a normal file.", filePath.toString()));
        }

        return Files.readAllLines(filePath);
    }

}
