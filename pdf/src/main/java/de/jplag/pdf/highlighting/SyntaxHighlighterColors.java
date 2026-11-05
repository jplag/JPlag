package de.jplag.pdf.highlighting;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;

public enum SyntaxHighlighterColors {
    RED(new DeviceRgb(202, 64, 43)),
    CYAN(new DeviceRgb(21, 147, 147)),
    GREEN(new DeviceRgb(145, 139, 59)),
    YELLOW(new DeviceRgb(187, 138, 53)),
    BLUE(new DeviceRgb(81, 106, 236)),
    MAGENTA(new DeviceRgb(123, 89, 192)),
    GRAY(new DeviceRgb(150, 150, 150));

    private Color color;

    SyntaxHighlighterColors(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
