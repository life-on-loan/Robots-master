package utils;

/**
 * Класс вспомогательных математических функций
 */
public class AdditionalMathMethods
{
    /**
     * Метод, отвечающий за то, что бы значение не вышло за рамки, которые для него допустимы
     * @param value - текущее вычисленное значение
     * @param min - минимум для этого значения
     * @param max - максимум для этого значения
     * @return значение, которое входит в допустимые рамки
     */
    public static double applyLimits(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    /**
     * Переход от градусов к радианам
     * @param angle - угол, который нужно привести к радианам
     * @return угол в радианах
     */
    public static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI) {
            angle -= 2*Math.PI;
        }
        return angle;
    }

    /**
     * Окруление
     * @param value - значение, которое округляем
     * @return округленное значение
     */
    public static int round(double value) {
        return (int)(value + 0.5);
    }
}
