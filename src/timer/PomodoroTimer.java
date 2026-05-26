package src.timer;

import src.model.SessionData;
import src.model.UserSettings;
import src.model.TimeRangeSchedule;
import src.utils.SoundUtil;
import javax.swing.*;
import java.util.List;

public class PomodoroTimer {
    private Timer timer;
    private int currentTime;
    private TimerState currentState;
    private UserSettings settings;
    private SessionData sessionData;
    private TimeRangeSchedule schedule;
    private java.util.List<TimerListener> listeners;
    private boolean isRunning;
    private int currentBlockIndex;
    private List<TimeRangeSchedule.TimeBlock> timeBlocks;
    private boolean scheduledMode;
    
    public PomodoroTimer(UserSettings settings, SessionData sessionData, TimeRangeSchedule schedule) {
        this.settings = settings;
        this.sessionData = sessionData;
        this.schedule = schedule;
        this.listeners = new java.util.ArrayList<>();
        this.currentState = TimerState.IDLE;
        this.isRunning = false;
        this.currentBlockIndex = 0;
        this.scheduledMode = false;
        reset();
    }
    
    public void addListener(TimerListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(TimerListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyTick() {
        for (TimerListener listener : listeners) {
            listener.onTimerTick(currentTime);
        }
    }
    
    private void notifyStateChanged() {
        for (TimerListener listener : listeners) {
            listener.onTimerStateChanged(currentState);
        }
    }
    
    private void notifyCycleComplete() {
        for (TimerListener listener : listeners) {
            listener.onCycleComplete(sessionData.getCyclesCompleted());
        }
    }
    
    public void startScheduled() {
        timeBlocks = schedule.getTimeBlocks();
        
        if (timeBlocks == null || timeBlocks.isEmpty()) {
            System.out.println("Please calculate schedule first!");
            JOptionPane.showMessageDialog(null, 
                "Please configure and calculate schedule first!",
                "No Schedule", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        scheduledMode = true;
        currentBlockIndex = 0;
        startNextBlock();
    }
    
    private void startNextBlock() {
        if (currentBlockIndex >= timeBlocks.size()) {
            completeSchedule();
            return;
        }
        
        TimeRangeSchedule.TimeBlock block = timeBlocks.get(currentBlockIndex);
        
        if (block.getType() == TimeRangeSchedule.TimeBlock.BlockType.STUDY) {
            currentState = TimerState.STUDY;
            currentTime = block.getDurationMinutes() * 60;
        } else {
            currentState = TimerState.BREAK;
            currentTime = block.getDurationMinutes() * 60;
        }
        
        notifyStateChanged();
        startTimer();
    }
    
    public void start() {
        if (currentState == TimerState.PAUSED) {
            startTimer();
        } else if (currentState == TimerState.IDLE && !scheduledMode) {
            // Manual mode
            scheduledMode = false;
            currentState = TimerState.STUDY;
            currentTime = settings.getStudySeconds();
            notifyStateChanged();
            startTimer();
        }
    }
    
    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }
        
        isRunning = true;
        timer = new Timer(1000, e -> {
            if (currentTime > 0) {
                currentTime--;
                notifyTick();
                
                // Play warning sound 10 seconds before break ends
                if (currentState == TimerState.BREAK && currentTime == 10) {
                    SoundUtil.playBeep(); // Warning sound
                }
            } else {
                timerComplete();
            }
        });
        timer.start();
    }
    
    private void timerComplete() {
        timer.stop();
        
        if (currentState == TimerState.STUDY) {
            // Study session completed - show popup and wait for user to start break
            SoundUtil.playStudyEndSound();
            
            // Show notification and wait for user
            JOptionPane.getRootFrame().setAlwaysOnTop(true);
            int response = JOptionPane.showConfirmDialog(null, 
                "📚 Study session completed!\n\nTime for a break! ☕\n\nClick OK to start break timer.",
                "Study Complete",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
            JOptionPane.getRootFrame().setAlwaysOnTop(false);
            
            if (response == JOptionPane.OK_OPTION) {
                // User clicked OK - start break
                if (scheduledMode) {
                    // Record the completed study session
                    TimeRangeSchedule.TimeBlock currentBlock = timeBlocks.get(currentBlockIndex);
                    sessionData.addStudyMinutes(currentBlock.getDurationMinutes());
                    sessionData.incrementCycles();
                    notifyCycleComplete();
                    
                    // Move to break
                    currentBlockIndex++;
                    startNextBlock();
                } else {
                    // Manual mode
                    sessionData.addStudyMinutes(settings.getStudyMinutes());
                    sessionData.incrementCycles();
                    notifyCycleComplete();
                    
                    currentState = TimerState.BREAK;
                    currentTime = settings.getBreakSeconds();
                    notifyStateChanged();
                    startTimer();
                }
            } else {
                // User cancelled - pause the timer
                currentState = TimerState.PAUSED;
                notifyStateChanged();
                isRunning = false;
            }
            
        } else if (currentState == TimerState.BREAK) {
            // Break completed - show popup and wait for user to start study
            SoundUtil.playBreakEndSound();
            
            // Show notification and wait for user
            JOptionPane.getRootFrame().setAlwaysOnTop(true);
            int response = JOptionPane.showConfirmDialog(null, 
                "☕ Break time is over!\n\nTime to get back to work! 📚\n\nClick OK to start next study session.",
                "Break Complete",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
            JOptionPane.getRootFrame().setAlwaysOnTop(false);
            
            if (response == JOptionPane.OK_OPTION) {
                // User clicked OK - start next study
                if (scheduledMode) {
                    currentBlockIndex++;
                    startNextBlock();
                } else {
                    currentState = TimerState.STUDY;
                    currentTime = settings.getStudySeconds();
                    notifyStateChanged();
                    startTimer();
                }
            } else {
                // User cancelled - pause the timer
                currentState = TimerState.PAUSED;
                notifyStateChanged();
                isRunning = false;
            }
        }
    }
    
    private void completeSchedule() {
        scheduledMode = false;
        currentState = TimerState.IDLE;
        currentTime = settings.getStudySeconds();
        isRunning = false;
        notifyStateChanged();
        notifyTick();
        
        // Play completion sound twice
        SoundUtil.playBeep();
        SoundUtil.playBeep();
        
        JOptionPane.showMessageDialog(null,
            String.format("🎉 Schedule Completed! 🎉\n\n📊 Total Cycles: %d\n📚 Total Study Time: %d minutes\n\nGreat job staying focused!",
                sessionData.getCyclesCompleted(),
                sessionData.getTotalStudyMinutes()),
            "Schedule Complete",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void pause() {
        if (timer != null && isRunning && currentState != TimerState.PAUSED && currentState != TimerState.IDLE) {
            timer.stop();
            isRunning = false;
            currentState = TimerState.PAUSED;
            notifyStateChanged();
        }
    }
    
    public void reset() {
        if (timer != null) {
            timer.stop();
        }
        isRunning = false;
        scheduledMode = false;
        currentBlockIndex = 0;
        currentState = TimerState.IDLE;
        currentTime = settings.getStudySeconds();
        notifyStateChanged();
        notifyTick();
    }
    
    public int getCurrentTime() {
        return currentTime;
    }
    
    public TimerState getCurrentState() {
        return currentState;
    }
    
    public boolean isScheduledMode() {
        return scheduledMode;
    }
    
    public int getCurrentBlockIndex() {
        return currentBlockIndex;
    }
    
    public int getTotalBlocks() {
        return timeBlocks == null ? 0 : timeBlocks.size();
    }
}