package gui;

import localization.LanguageManager;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame {
    private final GameVisualizer m_visualizer;

    public GameWindow(LanguageManager languageManager) {
        super("Игровое поле", true, true, true, true);
        languageManager.bindField("gameWindow.title", this::setTitle);
        setName(FrameNames.GAME_WINDOW.getName());
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
}
