package src.model;

import java.io.*;
import java.util.Properties;

public class UserSettings {
    private int studyMinutes;
    private int breakMinutes;
    private static final String CONFIG_FILE = "resources/config.properties";
    
    public UserSettings() {
        loadSettings();
    }
    
    private void loadSettings() {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);
        
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
                studyMinutes = Integer.parseInt(props.getProperty("study.minutes", "25"));
                breakMinutes = Integer.parseInt(props.getProperty("break.minutes", "5"));
            } catch (IOException | NumberFormatException e) {
                setDefaultSettings();
            }
        } else {
            setDefaultSettings();
        }
    }
    
    private void setDefaultSettings() {
        studyMinutes = 25;
        breakMinutes = 5;
    }
    
    public void saveSettings() {
        Properties props = new Properties();
        props.setProperty("study.minutes", String.valueOf(studyMinutes));
        props.setProperty("break.minutes", String.valueOf(breakMinutes));
        
        File resourcesDir = new File("resources");
        if (!resourcesDir.exists()) {
            resourcesDir.mkdirs();
        }
        
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "StudyPulse Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public int getStudyMinutes() {
        return studyMinutes;
    }
    
    public void setStudyMinutes(int minutes) {
        this.studyMinutes = minutes;
        saveSettings();
    }
    
    public int getBreakMinutes() {
        return breakMinutes;
    }
    
    public void setBreakMinutes(int minutes) {
        this.breakMinutes = minutes;
        saveSettings();
    }
    
    public int getStudySeconds() {
        return studyMinutes * 60;
    }
    
    public int getBreakSeconds() {
        return breakMinutes * 60;
    }
}