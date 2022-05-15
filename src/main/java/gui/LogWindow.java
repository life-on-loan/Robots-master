package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.util.Iterator;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

public class LogWindow extends JInternalFrame implements LogChangeListener {
    private final LogWindowSource m_logSource;
    private final TextArea m_logContent;

    public LogWindow(LogWindowSource logSource) {
        super("Протокол работы", true, true, true, true);
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

        //При закрытии окна произойдет отписка от обновления данных в логе
        addInternalFrameListener(new InternalFrameAdapter(){
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                m_logSource.unregisterListener(LogWindow.this);
            }
        });
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        Iterator<LogEntry> entryIterator = m_logSource.all();
        while (entryIterator.hasNext()) {
            content.append(entryIterator.next().getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }
    
    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }
}
