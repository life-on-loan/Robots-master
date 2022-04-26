package gui;

import javax.swing.*;

/**
 * Класс, содержащий параметры для восстановления положения окна
 */
public class FrameProperties {
    private FrameNames frameName;
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean isIcon;
    private boolean isMaximum;

    /**
     * Дефолтный конструктор необходимый для десериализации
     */
    FrameProperties() {}

    /**
     * Метод создающий параметры окна для сохранения
     * @param frame - окно
     * @return параметры окна, собранные в структуру FrameProperties
     */
    static FrameProperties createFrameProperties(JInternalFrame frame) {
        return new FrameProperties(
                FrameNames.getTypeByFrameName(frame.getName()),
                frame.getX(),
                frame.getY(),
                frame.getWidth(),
                frame.getHeight(),
                frame.isIcon(),
                frame.isMaximum()
        );
    }

    private FrameProperties(FrameNames frameName, int x, int y, int width, int height, boolean isIcon, boolean isMaximum) {
        this.frameName = frameName;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isIcon = isIcon;
        this.isMaximum = isMaximum;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isIcon() {
        return isIcon;
    }

    public void setIcon(boolean icon) {
        isIcon = icon;
    }

    public boolean isMaximum() {
        return isMaximum;
    }

    public void setMaximum(boolean maximum) {
        isMaximum = maximum;
    }

    public FrameNames getFrameName() {
        return frameName;
    }

    public void setFrameName(FrameNames frameName) {
        this.frameName = frameName;
    }
}
