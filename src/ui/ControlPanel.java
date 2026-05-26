package src.ui;

import src.timer.PomodoroTimer;
import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    private JButton scheduleStartButton;
    private JButton pauseButton;
    private JButton resetButton;
    
    public ControlPanel(PomodoroTimer timer) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        setBackground(new Color(240, 240, 240));
        
        // Create larger, more visible buttons
        scheduleStartButton = createButton("▶ Schedule Start", new Color(33, 150, 243), 16);
        pauseButton = createButton("⏸ Pause", new Color(255, 152, 0), 16);
        resetButton = createButton("🔄 Reset", new Color(244, 67, 54), 16);
        
        // Add action listeners
        scheduleStartButton.addActionListener(e -> {
            timer.startScheduled();
            highlightActiveButton(scheduleStartButton);
        });
        pauseButton.addActionListener(e -> {
            timer.pause();
            highlightActiveButton(pauseButton);
        });
        resetButton.addActionListener(e -> {
            timer.reset();
            resetButtonHighlights();
        });
        
        // Add buttons
        add(scheduleStartButton);
        add(pauseButton);
        add(resetButton);
        
        // Set preferred size to ensure visibility
        setPreferredSize(new Dimension(600, 70));
    }
    
    private JButton createButton(String text, Color color, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void highlightActiveButton(JButton activeButton) {
        // Reset all buttons to original colors
        scheduleStartButton.setBackground(new Color(33, 150, 243));
        pauseButton.setBackground(new Color(255, 152, 0));
        resetButton.setBackground(new Color(244, 67, 54));
        
        // Highlight the active button
        activeButton.setBackground(activeButton.getBackground().darker());
    }
    
    private void resetButtonHighlights() {
        scheduleStartButton.setBackground(new Color(33, 150, 243));
        pauseButton.setBackground(new Color(255, 152, 0));
        resetButton.setBackground(new Color(244, 67, 54));
    }
}