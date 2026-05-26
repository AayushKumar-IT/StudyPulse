package src.ui;

import src.timer.PomodoroTimer;
import src.timer.TimerState;
import src.utils.TimeFormatter;
import javax.swing.*;
import java.awt.*;

public class TimerPanel extends JPanel {
    private JLabel timeLabel;
    private JLabel progressLabel;
    private PomodoroTimer timer;
    private Font digitalFont;
    
    public TimerPanel(PomodoroTimer timer) {
        this.timer = timer;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 250));
        setBackground(Color.WHITE);
        
        // Create digital-style font
        digitalFont = new Font("Monospaced", Font.BOLD, 86);
        
        // Time label
        timeLabel = new JLabel(TimeFormatter.formatSeconds(timer.getCurrentTime()), SwingConstants.CENTER);
        timeLabel.setFont(digitalFont);
        timeLabel.setForeground(new Color(46, 125, 50));
        
        // Progress label
        progressLabel = new JLabel("", SwingConstants.CENTER);
        progressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Create center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(timeLabel, BorderLayout.CENTER);
        centerPanel.add(progressLabel, BorderLayout.SOUTH);
        
        // Add decorative border with shadow effect
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(30, 30, 30, 30),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            )
        ));
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Timer type label
        JLabel timerTypeLabel = new JLabel("🎯 TIME RANGE POMODORO TIMER", SwingConstants.CENTER);
        timerTypeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerTypeLabel.setForeground(new Color(100, 100, 100));
        add(timerTypeLabel, BorderLayout.NORTH);
    }
    
    public void updateDisplay(int secondsRemaining) {
        timeLabel.setText(TimeFormatter.formatSeconds(secondsRemaining));
        
        if (timer.isScheduledMode()) {
            int currentBlock = timer.getCurrentBlockIndex();
            int totalBlocks = timer.getTotalBlocks();
            if (totalBlocks > 0) {
                progressLabel.setText(String.format("📋 Block %d of %d", currentBlock + 1, totalBlocks));
            }
        } else {
            progressLabel.setText("⚙️ Manual Mode");
        }
    }
    
    public void setTimerStyle(TimerState state) {
        switch (state) {
            case STUDY:
                timeLabel.setForeground(new Color(46, 125, 50));  // Green
                setBackground(new Color(232, 245, 233));
                progressLabel.setForeground(new Color(46, 125, 50));
                break;
            case BREAK:
                timeLabel.setForeground(new Color(245, 124, 0));  // Orange
                setBackground(new Color(255, 248, 225));
                progressLabel.setForeground(new Color(245, 124, 0));
                break;
            case PAUSED:
                timeLabel.setForeground(Color.GRAY);
                setBackground(new Color(240, 240, 240));
                progressLabel.setForeground(Color.GRAY);
                break;
            case IDLE:
                timeLabel.setForeground(new Color(150, 150, 150));
                setBackground(Color.WHITE);
                progressLabel.setText("");
                progressLabel.setForeground(new Color(150, 150, 150));
                break;
        }
    }
}