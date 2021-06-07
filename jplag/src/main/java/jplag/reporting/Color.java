package jplag.reporting;

public enum Color {
    BLUE("#0000ff"),
    BROWN("#f63526"),
    BROWN_2("#980517"),
    CADET_BLUE("#77bfc7"),
    CHARTREUSE("#6cc417"),
    CORNFLOWER_BLUE("#151b8d"),
    CORNSILK("#c8c2a7"),
    CYAN("#50ebec"),
    DARK_GOLDENROD("#c58917"),
    DARK_OLIVE_GREEN("#bce954"),
    DARK_ORANGE("#f88017"),
    DARK_ORCHID("#b041ff"),
    DARK_ORCHID_2("#571b7e"),
    DARK_TURQUOISE("#3b9c9c"),
    DARK_VIOLET("#842dce"),
    DEEP_PINK("#f52887"),
    DEEP_SKY_BLUE("#3bb9ff"),
    DEEP_SKY_BLUE_2("#3090c7"),
    FIRE_BRICK("#800517"),
    FIRE_BRICK_2("#f62817"),
    FOREST_GREEN("#4e9258"),
    GOLD("#d4a017"),
    GREEN("#00ff00"),
    GREEN_2("#4cc417"),
    HOT_PINK("#f660ab"),
    KHAKI("#ada96e"),
    LAWN_GREEN("#87f717"),
    LIGHT_BLUE("#95b9c7"),
    LIGHT_CORAL("#e77471"),
    LIGHT_CYAN("#717d7d"),
    LIGHT_PINK("#faafba"),
    LIGHT_SALMON("#f9966b"),
    LIGHT_SEA_GREEN("#3ea99f"),
    LIGHT_SKY_BLUE("#82cafa"),
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
    ORCHID("#e57ded"),
    PALE_GREEN("#79d867"),
    PALE_TURQUOISE("#92c7c7"),
    PALE_VIOLET_RED("#d16587"),
    PERU("#c57726"),
    PURPLE("#8e35ef"),
    RED("#ff0000"),
    ROSY_BROWN("#b38481"),
    ROYAL_BLUE("#2b60de"),
    SALMON("#f88158"),
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
