package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import localization.LanguageManager;
import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

public class LogWindow extends JInternalFrame implements LogChangeListener {
    private final LogWindowSource m_logSource;
    private final TextArea m_logContent;
    private LanguageManager languageManager;

    public LogWindow(LogWindowSource logSource, LanguageManager languageManager) {
        super("Протокол работы", true, true, true, true);
        this.languageManager = languageManager;
        languageManager.bindField("logWindow.title", this::setTitle);
        setName(FrameNames.LOG_WINDOW.getName());
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all()) {
            content.append(languageManager.getString(entry.getMessage())).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }
    
    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }

    public void onLocaleChange(){
        languageManager.bindField("logWindow.title", this::setTitle);
    }
}
