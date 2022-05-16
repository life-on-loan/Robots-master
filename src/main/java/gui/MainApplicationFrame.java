package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.List;

import javax.swing.*;

import log.Logger;
import utils.FrameStateHandler;

import static constants.TextConstants.*;

public class MainApplicationFrame extends JFrame {
    private GameModel model = new GameModel();
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
    private final GameWindow gameWindow = new GameWindow(model);
    private final FrameStateHandler frameStateHandler = new FrameStateHandler();
    private InfoWindow infoWindow;

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);

        setContentPane(desktopPane);

        loadFrames();

        setJMenuBar(createMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        WindowAdapter windowAdapter = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                showConfirmationClosing(event);
            }
        };
        addWindowListener(windowAdapter);
    }

    /**
     * Метод, восстанавливающий положения окон
     */
    private void loadFrames() {
        List<FrameProperties> frameProperties = frameStateHandler.loadProperties();
        if (frameProperties != null) {
            infoWindow = new InfoWindow(model, desktopPane);
            for (FrameProperties properties : frameProperties) {
                switch (properties.getFrameName()) {
                    case GAME_WINDOW -> setupFrame(gameWindow, properties);
                    case LOG_WINDOW -> setupFrame(logWindow, properties);
                    case INFO_WINDOW -> setupFrame(infoWindow, properties);
                }
            }
        } else {
            setupLogWindow(logWindow);
            desktopPane.add(logWindow).setVisible(true);

            gameWindow.setSize(400, 400);
            desktopPane.add(gameWindow).setVisible(true);

            infoWindow = new InfoWindow(model, desktopPane);
            infoWindow.setBounds(400, 50, 210, 110);
        }
    }

    /**
     * Метод, настраивающий параметры окон
     */
    private void setupFrame(JInternalFrame frame, FrameProperties properties) {
        desktopPane.add(frame).setVisible(true);
        frame.setLocation(properties.getX(), properties.getY());
        frame.setSize(properties.getWidth(), properties.getHeight());
        try {
            frame.setIcon(properties.isIcon());
            frame.setMaximum(properties.isMaximum());
        } catch (PropertyVetoException e) {
            Logger.error(e.getMessage());
        }
    }

    private void setupFrame(JDialog frame, FrameProperties properties) {
        frame.pack();
        frame.setVisible(true);
        frame.setBounds(properties.getX(), properties.getY(), properties.getWidth(), properties.getHeight());
    }

    /**
     * Метод, сохраняющий положения окон
     */
    private void saveFrames() {
        List<FrameProperties> properties = List.of(
                FrameProperties.createFrameProperties(logWindow),
                FrameProperties.createFrameProperties(gameWindow),
                FrameProperties.createFrameProperties(infoWindow)
        );

        frameStateHandler.write(properties);
    }

    protected void setupLogWindow(LogWindow logWindow) {
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug(MES_PROTOCOL_WORK);
    }

    /**
     * Метод создания панели меню
     *
     * @return панель меню
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu lookAndFeelMenu = createLookAndFeelMenu();
        JMenu testMenu = createTestMenu();
        JMenu exitMenu = createExitMenu();
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(exitMenu);
        return menuBar;
    }

    /**
     * Метод создания пункта меню
     *
     * @param itemText - название пункта меню
     * @param keyEvent - клавиша быстрого доступа
     * @param listener - слушатель событий
     * @return пункт вложенного меню
     */
    private JMenuItem createNewJMenuItem(String itemText, int keyEvent, ActionListener listener) {
        JMenuItem newItem = new JMenuItem(itemText, keyEvent);
        newItem.addActionListener(listener);
        return newItem;
    }

    /**
     * Метод создания вложенного меню
     *
     * @param title                 - название вложенного меню
     * @param keyEvent              - клавиша быстрого доступа
     * @param accessibleDescription - общее описание вложенного меню
     * @return вложенное меню
     */
    private JMenu createNewJMenu(String title, int keyEvent, String accessibleDescription) {
        JMenu newMenuBar = new JMenu(title);
        newMenuBar.setMnemonic(keyEvent);
        newMenuBar.getAccessibleContext().setAccessibleDescription(accessibleDescription);
        return newMenuBar;
    }

    /**
     * Метод создания меню для режимов отображения
     *
     * @return вложенное меню режимов отображения
     */
    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = createNewJMenu(MES_MODE_MAPPING, KeyEvent.VK_V,
                MES_MANAGEMENT_MODE_MAPPING);

        {
            JMenuItem systemLookAndFeel = createNewJMenuItem(SYSTEM_SCHEME, KeyEvent.VK_S,
                    (event) -> {
                        setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                        this.invalidate();
                    });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = createNewJMenuItem(UNIVERSAL_SCHEME, KeyEvent.VK_S,
                    (event) -> {
                        setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                        this.invalidate();
                    });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }
        return lookAndFeelMenu;
    }

    /**
     * Метод создания меню для раздела тестов
     *
     * @return вложенное меню тестов
     */
    private JMenu createTestMenu() {
        JMenu testMenu = createNewJMenu(TESTS, KeyEvent.VK_T, TESTS_COMMANDS);
        {
            JMenuItem addLogMessageItem = createNewJMenuItem(MES_TO_LOG, KeyEvent.VK_S, (event) -> Logger.debug(NEW_STRING));
            testMenu.add(addLogMessageItem);
        }
        return testMenu;
    }

    /**
     * Метод создания меню для выхода
     *
     * @return вложенное меню выхода
     */
    private JMenu createExitMenu() {
        JMenu exitMenu = new JMenu(CLOSE);
        exitMenu.setMnemonic(KeyEvent.VK_Q);
        {
            JMenuItem addExitMessageItem = new JMenuItem(EXIT, KeyEvent.VK_Q);
            addExitMessageItem.addActionListener((event) -> Toolkit.getDefaultToolkit()
                    .getSystemEventQueue().postEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
            exitMenu.add(addExitMessageItem);
        }
        return exitMenu;
    }

    /**
     * Метод, показывающий окошко согласия на закрытие программы
     */
    private void showConfirmationClosing(WindowEvent event) {
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        int select = JOptionPane.showConfirmDialog(
                event.getWindow(), QUESTION_EXIT, PROGRAM,
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (select == 0) {
            saveFrames();
            event.getWindow().setVisible(false);
            System.exit(0);
        }
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }
}
