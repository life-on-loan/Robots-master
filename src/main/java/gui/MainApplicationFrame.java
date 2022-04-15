package gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.List;

import javax.swing.*;

import log.Logger;
import utils.FrameLoader;
import utils.FrameSaver;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
    private final GameWindow gameWindow = new GameWindow();

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);

        loadFrames();

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        WindowAdapter windowAdapter = new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                showConfirmationClosing(event);
            }
        };
        addWindowListener(windowAdapter);
    }

    /**
     * Метод восстанавливающий положения окон
     */
    private void loadFrames() {
        if (new File(FrameSaver.SAVED_STATE_PATH).exists()) {
            List<FrameProperties> frameProperties;
            try (FrameLoader frameLoader = new FrameLoader()) {
                frameProperties = frameLoader.loadProperties();
            }
            for (FrameProperties properties : frameProperties) {
                switch (properties.getFrameName()) {
                    case GAME_WINDOW -> setupFrame(gameWindow, properties);
                    case LOG_WINDOW -> setupFrame(logWindow, properties);
                }
            }
        } else {
            setupLogWindow(logWindow);
            desktopPane.add(logWindow).setVisible(true);

            gameWindow.setSize(400,  400);
            desktopPane.add(gameWindow).setVisible(true);
        }
    }

    /**
     * Метод настраивающий параметры окон
     */
    private void setupFrame(JInternalFrame frame, FrameProperties properties) {
        frame.setLocation(properties.getX(), properties.getY());
        frame.setSize(properties.getWidth(), properties.getHeight());
        try {
            frame.setIcon(properties.isIcon());
            frame.setClosed(properties.isClosed());
            frame.setMaximum(properties.isMaximum());
        } catch (PropertyVetoException e) {
            System.out.println(e.getMessage());
        }
        desktopPane.add(frame).setVisible(true);
    }

    /**
     * Метод сохраняющий положения окон
     */
    private void saveFrames() {
        List<FrameProperties> properties = List.of(
                prepareFrameProperties(logWindow),
                prepareFrameProperties(gameWindow)
        );

        try (FrameSaver frameSaver = new FrameSaver()) {
            frameSaver.write(properties);
        }
    }

    /**
     * Метод подготавливающий параметры окна для сохранения
     * @param frame - окно
     * @return параметры окна, собранные в структуру FrameProperties
     */
    private FrameProperties prepareFrameProperties(JInternalFrame frame) {
        return new FrameProperties(
                FrameNames.getTypeByFrameName(frame.getName()),
                frame.getX(),
                frame.getY(),
                frame.getWidth(),
                frame.getHeight(),
                frame.isIcon(),
                frame.isMaximum(),
                frame.isClosed()
        );
    }

    protected void setupLogWindow(LogWindow logWindow) {
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
    }

    /*protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        //Set up the lone menu.
        JMenu menu = new JMenu("Документ");
        menu.setMnemonic(KeyEvent.VK_D);
        menuBar.add(menu);
        //Set up the first menu item.
        JMenuItem menuItem = new JMenuItem("Новый");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("new");
        //menuItem.addActionListener(this);
        menu.add(menuItem);
        //Set up the second menu item.
        menuItem = new JMenuItem("Закрыть");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
        menuItem.addActionListener((event) -> onClose());
        menu.add(menuItem);
        return menuBar;
    }*/

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");

        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext()
            .setAccessibleDescription("Управление режимом отображения приложения");

        {
            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext()
            .setAccessibleDescription("Тестовые команды");

        {
            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> Logger.debug("Новая строка"));
            testMenu.add(addLogMessageItem);
        }

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);

        JMenu exitMenu = new JMenu("Закрыть");
        exitMenu.setMnemonic(KeyEvent.VK_Q);
        {
            JMenuItem addExitMessageItem = new JMenuItem("Выйти", KeyEvent.VK_Q);
            addExitMessageItem.addActionListener(
                (event) -> Toolkit.getDefaultToolkit().getSystemEventQueue()
                    .postEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
            exitMenu.add(addExitMessageItem);
        }

        menuBar.add(exitMenu);
        return menuBar;
    }

    /**
     * Метод, показывающий окошко согласия на закрытие программы
     */
    private void showConfirmationClosing(WindowEvent event) {
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        int select = JOptionPane.showConfirmDialog(
            event.getWindow(), "Закрыть программу?", "Программа",
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
