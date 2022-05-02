package utils;

/**
 * Класс вспомогательных математических функций
 */
public class AdditionalMathMethods
{
    public static double applyLimits(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    public static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI) {
            angle -= 2*Math.PI;
        }
        return angle;
    }

    public static int round(double value) {
        return (int)(value + 0.5);
    }
}
