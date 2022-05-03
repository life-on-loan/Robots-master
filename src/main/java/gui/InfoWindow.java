package gui;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Класс окна отображения координат робота и цели
 */
public class InfoWindow extends JInternalFrame implements Observer {

    private final GameModel mModel;
    private final Label mRobotX = new Label();
    private final Label mRobotY = new Label();

    public InfoWindow(GameModel model) {
        super("Координаты робота", true, true, true, true);
        setName(FrameNames.INFO_WINDOW.getName());

        mModel = model;
        mModel.addObserver(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        addLine(panel, "Robot X", mRobotX);
        addLine(panel, "Robot Y", mRobotY);

        getContentPane().add(panel);
        pack();
    }

    /**
     * Метод добавления строки с координатами на панель
     * @param panel - панель
     * @param name - название координаты
     * @param label - значение координаты
     */
    private void addLine(JPanel panel, String name, Label label) {
        JPanel line = new JPanel();
        line.setLayout(new BoxLayout(line, BoxLayout.LINE_AXIS));
        line.add(new Label(String.format("%s: ", name)));
        line.add(label);
        panel.add(line);
    }
    /**
     * This method is called whenever the observed object is changed. An
     * application calls an {@code Observable} object's
     * {@code notifyObservers} method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the {@code notifyObservers}
     */
    @Override
    public void update(Observable o, Object arg) {
        double robotX = mModel.getRobotPositionX();
        double robotY = mModel.getRobotPositionY();

        mRobotX.setText(String.format("%.2f", robotX));
        mRobotY.setText(String.format("%.2f", robotY));
    }
}
