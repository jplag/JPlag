package de.jplag.pdf.utils;

import java.util.HashMap;
import java.util.Map;

import com.itextpdf.kernel.colors.Color;

public class ColorGenerator<T> {
    private static final float saturation = .2f;
    private static final float value = 1f;

    private Map<T, Color> colors;
    private int count;

    public ColorGenerator(int count) {
        this.colors = new HashMap<>();
        this.count = count;
    }

    public Color getColor(T key) {
        if (this.colors.containsKey(key)) {
            return this.colors.get(key);
        }

        float hue = (float) (1. / this.count) * this.colors.size();
        Color color = hsvToRgb(hue, saturation, value);
        this.colors.put(key, color);
        return color;
    }

    private static Color hsvToRgb(float h, float s, float v) {
        int r, g, b;

        int i = (int) (h * 6);
        float f = h * 6 - i;
        int p = (int) (v * (1 - s) * 255);
        int q = (int) (v * (1 - f * s) * 255);
        int t = (int) (v * (1 - (1 - f) * s) * 255);
        v = (int) (v * 255);

        switch (i % 6) {
            case 0:
                r = (int) v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = (int) v;
                b = p;
                break;
            case 2:
                r = p;
                g = (int) v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = (int) v;
                break;
            case 4:
                r = t;
                g = p;
                b = (int) v;
                break;
            case 5:
                r = (int) v;
                g = p;
                b = q;
                break;
            default:
                r = g = b = 0;
                break;
        }

        return Color.createColorWithColorSpace(new float[] {r / 255.f, g / 255.f, b / 255.f});
    }
}
