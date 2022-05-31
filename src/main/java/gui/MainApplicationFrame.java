package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.List;
import java.util.Locale;

import localization.LanguageManager;

import javax.swing.*;

import log.Logger;
import utils.FrameStateHandler;


public class MainApplicationFrame extends JFrame {
    private LanguageManager m_languageManager = new LanguageManager();
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource(), m_languageManager);
    private final GameWindow gameWindow = new GameWindow(m_languageManager);
    private final FrameStateHandler frameStateHandler = new FrameStateHandler();

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
                showConfirmationClosing(m_languageManager, event);
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
            for (FrameProperties properties : frameProperties) {
                switch (properties.getFrameName()) {
                    case GAME_WINDOW -> setupFrame(gameWindow, properties);
                    case LOG_WINDOW -> setupFrame(logWindow, properties);
                }
            }
        } else {
            setupLogWindow(logWindow);
            desktopPane.add(logWindow).setVisible(true);

            gameWindow.setSize(400, 400);
            desktopPane.add(gameWindow).setVisible(true);
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

    /**
     * Метод, сохраняющий положения окон
     */
    private void saveFrames() {
        List<FrameProperties> properties = List.of(
                FrameProperties.createFrameProperties(logWindow),
                FrameProperties.createFrameProperties(gameWindow)
        );

        frameStateHandler.write(properties);
    }

    protected void setupLogWindow(LogWindow logWindow) {
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
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
        JMenu languageMenu = createLanguageMenu();
        menuBar.add(languageMenu);
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        return menuBar;
    }

    private JMenu createLanguageMenu() {
        JMenu languageMenu = createNewJMenu("languageMenu.text", KeyEvent.VK_V,
                "languageMenu.description", m_languageManager);
        {
            JMenuItem english = createNewJMenuItem("languageMenu.english", KeyEvent.VK_E,
                    e -> {
                        m_languageManager.changeLocale(new Locale("en", "US"));
                    });
            languageMenu.add(english);
        }

        {
            JMenuItem russian = createNewJMenuItem("languageMenu.russian", KeyEvent.VK_E,
                    e -> {
                        m_languageManager.changeLocale(new Locale("ru", "RU"));
                    });
            languageMenu.add(russian);
        }
        return languageMenu;
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
        JMenuItem newItem = new JMenuItem();
        newItem.setMnemonic(keyEvent);
        newItem.addActionListener(listener);
        m_languageManager.bindField(itemText, newItem::setText);
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
    private JMenu createNewJMenu(String title, int keyEvent, String accessibleDescription, LanguageManager languageManager) {
        JMenu newMenuBar = new JMenu();
        languageManager.bindField(title, newMenuBar::setText);
        newMenuBar.setMnemonic(keyEvent);
        languageManager.bindField(accessibleDescription, newMenuBar.getAccessibleContext()::setAccessibleDescription);
        return newMenuBar;
    }

    /**
     * Метод создания меню для режимов отображения
     *
     * @return вложенное меню режимов отображения
     */
    private JMenu createLookAndFeelMenu() {
        JMenu lookAndFeelMenu = createNewJMenu("lookAndFeelMenu.text", KeyEvent.VK_V,
                "lookAndFeelMenu.description",m_languageManager);
        {
            JMenuItem systemLookAndFeel = createNewJMenuItem("lookAndFeelMenu.system", KeyEvent.VK_S,
                    e -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = createNewJMenuItem("lookAndFeelMenu.universal", KeyEvent.VK_S,
                    e -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));
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
        JMenu testMenu = createNewJMenu("testMenu.text", KeyEvent.VK_T,
                "testMenu.description", m_languageManager);
        {
            JMenuItem addLogMessageItem = createNewJMenuItem(
                    "testMenu.sendMessage", KeyEvent.VK_S, e -> Logger.debug("newString"));
            testMenu.add(addLogMessageItem);
        }
        return testMenu;
    }


    /**
     * Метод, показывающий окошко согласия на закрытие программы
     */
    private void showConfirmationClosing(LanguageManager languageManager, WindowEvent event) {
        int select = JOptionPane.showOptionDialog(this,
                languageManager.getString("confirmDialog.message"),
                languageManager.getString("confirmDialog.title"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                new Object[]{
                        languageManager.getString("confirmDialog.yes"),
                        languageManager.getString("confirmDialog.no")
                },
                JOptionPane.NO_OPTION);
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
