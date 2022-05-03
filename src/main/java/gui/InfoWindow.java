package gui;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Класс окна отображения координат робота и цели
 */
public class InfoWindow extends JDialog implements Observer {

    private final GameModel mModel;
    private final Label mRobotX = new Label();
    private final Label mRobotY = new Label();

    public InfoWindow(GameModel model) {
        super();
        setName(FrameNames.INFO_WINDOW.getName());

        mModel = model;
        model.addObserver(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setPreferredSize(new Dimension(250, 100));

        addLine(panel, "Robot X", mRobotX);
        addLine(panel, "Robot Y", mRobotY);

        getContentPane().add(panel);
        setVisible(true);
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
        mRobotX.setText(String.format("%.2f", mModel.getRobotPositionX()));
        mRobotY.setText(String.format("%.2f", mModel.getRobotPositionY()));
    }
}
