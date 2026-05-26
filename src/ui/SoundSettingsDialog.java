package src.ui;

import src.utils.SoundUtil;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SoundSettingsDialog extends JDialog {
    private JLabel currentSoundLabel;
    private JButton testButton;
    private JButton changeSoundButton;
    private JButton resetButton;
    
    public SoundSettingsDialog(JFrame parent) {
        super(parent, "Sound Settings", true);
        setLayout(new BorderLayout());
        setSize(400, 220);
        setLocationRelativeTo(parent);
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Info label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("Customize your notification sounds");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        infoLabel.setForeground(new Color(100, 100, 100));
        mainPanel.add(infoLabel, gbc);
        
        // Current sound label
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Current Sound:"), gbc);
        
        gbc.gridx = 1;
        currentSoundLabel = new JLabel(SoundUtil.getCurrentSoundInfo());
        currentSoundLabel.setFont(new Font("Arial", Font.BOLD, 12));
        currentSoundLabel.setForeground(new Color(76, 175, 80));
        mainPanel.add(currentSoundLabel, gbc);
        
        // Test button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        testButton = new JButton("🔊 Test Sound");
        testButton.setBackground(new Color(76, 175, 80));
        testButton.setForeground(Color.WHITE);
        testButton.setFocusPainted(false);
        testButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        testButton.addActionListener(e -> {
            SoundUtil.testSound();
        });
        mainPanel.add(testButton, gbc);
        
        // Change sound button
        gbc.gridy = 3;
        changeSoundButton = new JButton("📁 Change Sound File");
        changeSoundButton.setBackground(new Color(33, 150, 243));
        changeSoundButton.setForeground(Color.WHITE);
        changeSoundButton.setFocusPainted(false);
        changeSoundButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changeSoundButton.addActionListener(e -> changeSoundFile());
        mainPanel.add(changeSoundButton, gbc);
        
        // Reset button
        gbc.gridy = 4;
        resetButton = new JButton("🔄 Reset to Default");
        resetButton.setBackground(new Color(255, 152, 0));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> resetSound());
        mainPanel.add(resetButton, gbc);
        
        // Close button
        gbc.gridy = 5;
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(100, 100, 100));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());
        mainPanel.add(closeButton, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void changeSoundFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Custom Sound File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".wav") || name.endsWith(".mp3") || name.endsWith(".au");
            }
            
            @Override
            public String getDescription() {
                return "Sound Files (*.wav, *.mp3, *.au)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (SoundUtil.setCustomSound(selectedFile)) {
                JOptionPane.showMessageDialog(this, 
                    "✓ Sound changed successfully!\nClick 'Test Sound' to hear it.",
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                currentSoundLabel.setText(SoundUtil.getCurrentSoundInfo());
                
                // Test the new sound immediately
                SoundUtil.testSound();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Invalid sound file!\nPlease select a valid .wav, .mp3, or .au file.",
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void resetSound() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Reset to default system sound?",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SoundUtil.resetToDefaultSound();
            currentSoundLabel.setText(SoundUtil.getCurrentSoundInfo());
            JOptionPane.showMessageDialog(this, 
                "✓ Sound reset to default!",
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Test default sound
            java.awt.Toolkit.getDefaultToolkit().beep();
        }
    }
}