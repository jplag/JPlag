package de.jplag.reporting;

public enum Color {
    BLUE("#0000ff"),
    BROWN("#f63526"),
    BROWN_2("#980517"),
    CADET_BLUE("#53858b"),
    CHARTREUSE("#6cc417"),
    CORNFLOWER_BLUE("#151b8d"),
    CORNSILK("#8c8774"),
    CYAN("#38a4a5"),
    DARK_GOLDENROD("#c58917"),
    DARK_OLIVE_GREEN("#83a33a"),
    DARK_ORANGE("#ad5910"),
    DARK_ORCHID("#b041ff"),
    DARK_ORCHID_2("#571b7e"),
    DARK_TURQUOISE("#3b9c9c"),
    DARK_VIOLET("#842dce"),
    DEEP_PINK("#f52887"),
    DEEP_SKY_BLUE("#2981b2"),
    DEEP_SKY_BLUE_2("#3090c7"),
    FIRE_BRICK("#800517"),
    FIRE_BRICK_2("#f62817"),
    FOREST_GREEN("#4e9258"),
    GOLD("#947010"),
    GREEN("#4cc417"),
    HOT_PINK("#f660ab"),
    KHAKI("#79764d"),
    LAWN_GREEN("#5eac10"),
    LIGHT_BLUE("#68818b"),
    LIGHT_CORAL("#e77471"),
    LIGHT_CYAN("#717d7d"),
    LIGHT_PINK("#af7a82"),
    LIGHT_SALMON("#ae694a"),
    LIGHT_SEA_GREEN("#3ea99f"),
    LIGHT_SKY_BLUE("#5b8daf"),
    LIGHT_SLATE_BLUE("#736aff"),
    LIGHT_YELLOW("#827d6b"),
    LIME_GREEN("#41a317"),
    MAGENTA("#ff00ff"),
    MAROON("#810541"),
    MEDIUM_AQUAMARINE("#348781"),
    MEDIUM_BLUE("#152dc6"),
    MEDIUM_FOREST_GREEN("#347235"),
    ORANGE("#f87a17"),
    ORANGE_2("#c57717"),
    ORANGE_RED("#c22817"),
    ORCHID("#a057a5"),
    PALE_GREEN("#549748"),
    PALE_TURQUOISE("#668b8b"),
    PALE_VIOLET_RED("#d16587"),
    PERU("#c57726"),
    PURPLE("#8e35ef"),
    RED("#ff0000"),
    ROSY_BROWN("#b38481"),
    ROYAL_BLUE("#2b60de"),
    SALMON("#ad5a3d"),
    SEA_GREEN("#4e8975"),
    STEEL_BLUE("#4863a0"),
    YELLOW_GREEN("#52d017");

    private final String hexadecimalValue;

    private Color(String hexadecimalValue) {
        this.hexadecimalValue = hexadecimalValue;
    }

    public String getHexadecimalValue() {
        return hexadecimalValue;
    }

    public static String getHexadecimalValue(int index) {
        return values()[index % values().length].getHexadecimalValue();
    }
}
