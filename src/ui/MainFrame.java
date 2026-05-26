package src.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import src.timer.PomodoroTimer;
import src.timer.TimerListener;
import src.timer.TimerState;
import src.model.SessionData;
import src.model.UserSettings;
import src.model.TimeRangeSchedule;
import java.time.LocalTime;

public class MainFrame extends JFrame implements TimerListener {
    private PomodoroTimer timer;
    private TimerPanel timerPanel;
    private ControlPanel controlPanel;
    private SessionData sessionData;
    private UserSettings settings;
    private TimeRangeSchedule schedule;
    private JLabel statusLabel;
    private JLabel statsLabel;
    private JTextArea scheduleTextArea;
    private JPanel schedulePanel;
    private JComboBox<String> startHourBox, startMinBox, endHourBox, endMinBox;
    private JTextField studyTimeField, breakTimeField;
    private JLabel cyclesLabel;
    private JPanel mainContentPanel;
    
    public MainFrame() {
        // Initialize components
        settings = new UserSettings();
        sessionData = new SessionData();
        schedule = new TimeRangeSchedule();
        timer = new PomodoroTimer(settings, sessionData, schedule);
        timer.addListener(this);
        
        // Setup frame
        setTitle("StudyPulse by Neyash");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(850, 900);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(750, 800));
        
        // Create menu bar
        createMenuBar();
        
        // Create main content panel with BorderLayout
        mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create schedule configuration panel
        JPanel configPanel = createConfigurationPanel();
        
        // Create schedule display panel
        schedulePanel = createScheduleDisplayPanel();
        
        // Create timer panel with explicit size
        timerPanel = new TimerPanel(timer);
        timerPanel.setPreferredSize(new Dimension(400, 300));
        timerPanel.setMinimumSize(new Dimension(400, 300));
        timerPanel.setMaximumSize(new Dimension(800, 300));
        
        // Create control panel with buttons
        controlPanel = new ControlPanel(timer);
        controlPanel.setPreferredSize(new Dimension(800, 80));
        controlPanel.setMinimumSize(new Dimension(600, 70));
        
        // Create status panel
        JPanel statusPanel = createStatusPanel();
        
        // Add all components vertically
        mainContentPanel.add(configPanel);
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // spacing
        mainContentPanel.add(schedulePanel);
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // spacing
        mainContentPanel.add(timerPanel);
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // spacing
        mainContentPanel.add(controlPanel);
        
        // Add to frame
        JScrollPane scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Debug: Ensure timer panel is visible
        SwingUtilities.invokeLater(() -> {
            timerPanel.setVisible(true);
            timerPanel.revalidate();
            timerPanel.repaint();
        });
    }
    
    private JPanel createConfigurationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(33, 150, 243), 2),
            "Time Range Configuration",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Start Time
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel startLabel = new JLabel("Start Time:");
        startLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(startLabel, gbc);
        
        gbc.gridx = 1;
        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        startHourBox = new JComboBox<>();
        startMinBox = new JComboBox<>();
        for (int i = 0; i <= 23; i++) startHourBox.addItem(String.format("%02d", i));
        for (int i = 0; i <= 59; i++) startMinBox.addItem(String.format("%02d", i));
        startHourBox.setSelectedItem("23");
        startMinBox.setSelectedItem("00");
        startPanel.add(new JLabel("Hour:"));
        startPanel.add(startHourBox);
        startPanel.add(Box.createHorizontalStrut(10));
        startPanel.add(new JLabel("Min:"));
        startPanel.add(startMinBox);
        panel.add(startPanel, gbc);
        
        // End Time
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel endLabel = new JLabel("End Time:");
        endLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(endLabel, gbc);
        
        gbc.gridx = 1;
        JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        endHourBox = new JComboBox<>();
        endMinBox = new JComboBox<>();
        for (int i = 0; i <= 23; i++) endHourBox.addItem(String.format("%02d", i));
        for (int i = 0; i <= 59; i++) endMinBox.addItem(String.format("%02d", i));
        endHourBox.setSelectedItem("23");
        endMinBox.setSelectedItem("10");
        endPanel.add(new JLabel("Hour:"));
        endPanel.add(endHourBox);
        endPanel.add(Box.createHorizontalStrut(10));
        endPanel.add(new JLabel("Min:"));
        endPanel.add(endMinBox);
        panel.add(endPanel, gbc);
        
        // Study Time
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel studyLabel = new JLabel("Study Time (min):");
        studyLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(studyLabel, gbc);
        
        gbc.gridx = 1;
        studyTimeField = new JTextField("2", 5);
        studyTimeField.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(studyTimeField, gbc);
        
        // Break Time
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel breakLabel = new JLabel("Break Time (min):");
        breakLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(breakLabel, gbc);
        
        gbc.gridx = 1;
        breakTimeField = new JTextField("5", 5);
        breakTimeField.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(breakTimeField, gbc);
        
        // Calculate Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton calculateButton = new JButton("📊 Calculate Schedule");
        calculateButton.setBackground(new Color(33, 150, 243));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFont(new Font("Arial", Font.BOLD, 13));
        calculateButton.setFocusPainted(false);
        calculateButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        calculateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        calculateButton.addActionListener(e -> calculateAndDisplaySchedule());
        panel.add(calculateButton, gbc);
        
        // Cycles display
        gbc.gridy = 5;
        cyclesLabel = new JLabel("📈 Estimated Cycles: --", SwingConstants.CENTER);
        cyclesLabel.setFont(new Font("Arial", Font.BOLD, 13));
        cyclesLabel.setForeground(new Color(76, 175, 80));
        panel.add(cyclesLabel, gbc);
        
        return panel;
    }
    
    private JPanel createScheduleDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(33, 150, 243), 2),
            "Schedule Timeline",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));
        
        scheduleTextArea = new JTextArea(10, 50);
        scheduleTextArea.setEditable(false);
        scheduleTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        scheduleTextArea.setBackground(new Color(252, 252, 252));
        JScrollPane scrollPane = new JScrollPane(scheduleTextArea);
        scrollPane.setPreferredSize(new Dimension(750, 180));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private void calculateAndDisplaySchedule() {
        try {
            // Get start time
            int startHour = Integer.parseInt((String) startHourBox.getSelectedItem());
            int startMin = Integer.parseInt((String) startMinBox.getSelectedItem());
            LocalTime startTime = LocalTime.of(startHour, startMin);
            
            // Get end time
            int endHour = Integer.parseInt((String) endHourBox.getSelectedItem());
            int endMin = Integer.parseInt((String) endMinBox.getSelectedItem());
            LocalTime endTime = LocalTime.of(endHour, endMin);
            
            // Get study and break times
            int studyTime = Integer.parseInt(studyTimeField.getText());
            int breakTime = Integer.parseInt(breakTimeField.getText());
            
            if (studyTime <= 0 || breakTime <= 0) {
                JOptionPane.showMessageDialog(this, "Study and break times must be positive!");
                return;
            }
            
            if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
                JOptionPane.showMessageDialog(this, "End time must be after start time!");
                return;
            }
            
            // Update schedule
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
            schedule.setStudyMinutes(studyTime);
            schedule.setBreakMinutes(breakTime);
            
            // Generate schedule
            schedule.generateSchedule();
            int cycles = schedule.calculateCycles();
            
            // Update display
            cyclesLabel.setText(String.format("📈 Estimated Cycles: %d", cycles));
            scheduleTextArea.setText(schedule.getScheduleSummary());
            
            // Update timer settings
            settings.setStudyMinutes(studyTime);
            settings.setBreakMinutes(breakTime);
            
            // Reset timer with new settings
            timer.reset();
            
            // Show summary dialog
            JOptionPane.showMessageDialog(this, 
                String.format("✅ Schedule Calculated Successfully!\n\n📊 Total Cycles: %d\n📚 Total Study: %d min\n☕ Total Break: %d min\n\n🎯 Click 'Schedule Start' to begin!",
                    cycles, cycles * studyTime, (cycles - 1) * breakTime),
                "Schedule Ready", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers!");
        }
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // Add Sound Settings menu
        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem soundSettingsItem = new JMenuItem("Sound Settings");
        soundSettingsItem.addActionListener(e -> showSoundSettings());
        settingsMenu.add(soundSettingsItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);  // Add settings menu
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void showSoundSettings() {
        SoundSettingsDialog dialog = new SoundSettingsDialog(this);
        dialog.setVisible(true);
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        panel.setBackground(new Color(240, 240, 240));
        
        statusLabel = new JLabel("⚙️ Status: Configure your time range first", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        
        statsLabel = new JLabel("📊 Cycles: 0 | ⏱️ Total Time: 0 min", SwingConstants.CENTER);
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panel.add(statusLabel);
        panel.add(statsLabel);
        return panel;
    }
    
    private void showAboutDialog() {
        String message = "═══════════════════════════════\n" +
                        "   StudyPulse " +
                        "═══════════════════════════════\n\n" +
                        "📅 Set your start time and end time\n" +
                        "⚙️ Configure study and break durations\n" +
                        "🔄 System calculates optimal cycles\n" +
                        "🎯 Automated Pomodoro sessions\n\n" +
                        "Version 2.0\n" +
                        "© 2024 Neyash\n" +
                        "═══════════════════════════════";
        JOptionPane.showMessageDialog(this, message, "About StudyPulse", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public void onTimerTick(int secondsRemaining) {
        timerPanel.updateDisplay(secondsRemaining);
    }
    
    @Override
    public void onTimerStateChanged(TimerState state) {
        String status = "";
        switch (state) {
            case STUDY:
                status = "📚 Status: Study Time! Focus! 🎯";
                break;
            case BREAK:
                status = "☕ Status: Break Time! Relax! 🧘";
                break;
            case PAUSED:
                status = "⏸️ Status: Paused";
                break;
            case IDLE:
                status = "⚙️ Status: Configure time range and click Schedule Start";
                break;
        }
        statusLabel.setText(status);
        timerPanel.setTimerStyle(state);
    }
    
    @Override
    public void onCycleComplete(int cyclesCompleted) {
        statsLabel.setText(String.format("📊 Cycles: %d | ⏱️ Total Time: %d min",
            sessionData.getCyclesCompleted(),
            sessionData.getTotalStudyMinutes()));
    }
}