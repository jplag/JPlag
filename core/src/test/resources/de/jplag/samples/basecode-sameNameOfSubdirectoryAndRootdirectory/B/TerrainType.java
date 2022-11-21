package carcassonne.model.terrain;

import java.util.List;

/**
 * Enumeration for the terrain type. Is used to specify the terrain of a tile on its different positions.
 * @author Mean Plagiarizer
 */
public enum TerrainType {
    CASTLE,
    ROAD,
    MONASTERY,
    FIELDS,
    OTHER;

    private static final int CONST_0 = 0;
    private static final int CONST_1 = 1;

    /**
     * Generates a list of the basic terrain types, which is every terrain except {@link Other}.
     * @return a list of CASTLE, ROAD, MONASTERY, FIELDS.
     */
    public static List<TerrainType> basicTerrain() {
        return List.of(CASTLE, ROAD, MONASTERY, FIELDS);
    }

    public String toReadableString() {
        return toString().charAt(CONST_0) + toString().substring(CONST_1).toLowerCase();
    }
}