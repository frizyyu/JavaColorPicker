package com.converter.colorconverter.logic;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import static com.converter.colorconverter.logic.ColorConvertEnum.*;

public class ColorConvert {
    public static javafx.scene.paint.Color convertToRGBA(String[] input, ColorConvertEnum type){
        switch (type){
            case HEX ->{
                return hexToRgba(input[0]);
            }
            case RGB -> {
                return new javafx.scene.paint.Color(Double.parseDouble(input[0])/255.0, Double.parseDouble(input[1])/255.0, Double.parseDouble(input[2])/255.0, 1);
            }
            case HSLA -> {
                float h = Float.parseFloat(input[0]), s = Float.parseFloat(input[1]), l = Float.parseFloat(input[2]), a = Float.parseFloat(input[3]);
                return hslaToRgba(new float[]{h, s, l, a});
            }
            case CMYK -> {
                float c = Float.parseFloat(input[0]), m = Float.parseFloat(input[1]), y = Float.parseFloat(input[2]), k = Float.parseFloat(input[3]);
                int a = Integer.parseInt(input[4]);
                return cmykToRgba(c, m, y, k, a);
            }
            default -> {
                return new javafx.scene.paint.Color(Double.parseDouble(input[0])/255.0, Double.parseDouble(input[1])/255.0, Double.parseDouble(input[2])/255.0, Double.parseDouble(input[3])/255.0);
            }
        }
    }
    public static javafx.scene.paint.Color hslaToRgba(float[] hsla) {
        float h = hsla[0] / 360;
        float s = hsla[1] / 100;
        float l = hsla[2] / 100;
        float a = hsla[3];

        float q = l < 0.5 ? l * (1 + s) : l + s - l * s;
        float p = 2 * l - q;
        float[] rgb = new float[3];
        for (int i = 0; i < 3; i++) {
            float t = h + (i == 0 ? 1f / 3 : (i == 1 ? 0 : -1f / 3));
            if (t < 0) t += 1;
            if (t > 1) t -= 1;
            if (t < 1f / 6) rgb[i] = p + (q - p) * 6 * t;
            else if (t < 1f / 2) rgb[i] = q;
            else if (t < 2f / 3) rgb[i] = p + (q - p) * (2f / 3 - t) * 6;
            else rgb[i] = p;
        }
        return new javafx.scene.paint.Color(rgb[0], rgb[1], rgb[2], a);
    }
    public static javafx.scene.paint.Color hexToRgba(String hex) {
        hex = hex.replace("#", "");
        if (hex.length() == 6){
            hex += "ff";
        }
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        int a = Integer.parseInt(hex.substring(6, 8), 16);
        return new javafx.scene.paint.Color(r/255.0, g/255.0, b/255.0, a/255.0);
    }

    public static javafx.scene.paint.Color cmykToRgba(float c, float m, float y, float k, int alpha) {
        double r = (1 - c / 100.0) * (1 - k / 100.0);
        double g = (1 - m / 100.0) * (1 - k / 100.0);
        double b = (1 - y / 100.0) * (1 - k / 100.0);
        return new javafx.scene.paint.Color(r, g, b, alpha/255.0);
    }

    public static LinkedHashMap<ColorConvertEnum, String> convert(Color color){
        LinkedHashMap<ColorConvertEnum, String> colorMap = new LinkedHashMap<>();

        colorMap.put(HEX, String.format("#%02x%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));

        colorMap.put(RGB, String.format("%s, %s, %s", color.getRed(), color.getGreen(), color.getBlue()));

        colorMap.put(RGBA, String.format("%s, %s, %s, %s", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));

        int[] cmyk = rgbaToCmyk(color.getRed(), color.getGreen(), color.getBlue());
        colorMap.put(CMYK, String.format("%s, %s, %s, %s", cmyk[0], cmyk[1], cmyk[2], cmyk[3]));

        String[] hsla = rgbaToHsla(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        colorMap.put(HSLA, String.format("%s, %s, %s, %s", hsla[0], hsla[1], hsla[2], hsla[3]));
        return colorMap;
    }

    private static int[] rgbaToCmyk(int r, int g, int b){
        double futureC = r / 255.0 * 100;
        double futureM = g / 255.0 * 100;
        double futureY = b / 255.0 * 100;

        double K = 100-Math.max(Math.max(futureC, futureM), futureY);
        int C = (int) ((int)(100 - futureC - K) / (100 - K) * 100);
        int M = (int) ((int)(100 - futureM - K) / (100 - K) * 100);
        int Y = (int) ((int)(100 - futureY - K) / (100 - K) * 100);
        return new int[] {C, M, Y, (int) K};
    }
    public static String[] rgbaToHsla(int r, int g, int b, int a) {

        float rf = r / 255.0f;
        float gf = g / 255.0f;
        float bf = b / 255.0f;

        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float l = (max + min) / 2.0f;

        float h, s;
        if (max == min) {
            h = s = 0;
        } else {
            float d = max - min;
            s = l > 0.5 ? d / (2 - max - min) : d / (max + min);

            if (max == rf) {
                h = (gf - bf) / d + (gf < bf ? 6 : 0);
            } else if (max == gf) {
                h = (bf - rf) / d + 2;
            } else {
                h = (rf - gf) / d + 4;
            }
            h /= 6;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        String resH = df.format(h * 360).replace(",", ".");
        String resS = String.format("%s%%", df.format(s * 100).replace(",", "."));
        String resL = String.format("%s%%", df.format(l * 100).replace(",", "."));
        String resA = df.format(a/255.0).replace(",", ".");
        return new String[] {resH, resS, resL, resA};
    }
}
