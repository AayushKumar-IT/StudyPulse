package src.model;

import java.time.LocalTime;
import java.time.Duration;

public class ScheduleConfig {
    private LocalTime startTime;
    private LocalTime endTime;
    private int gapBetweenCycles; // in minutes
    private int totalCycles;
    
    public ScheduleConfig() {
        this.startTime = LocalTime.of(9, 0);  // Default 9:00 AM
        this.endTime = LocalTime.of(17, 0);   // Default 5:00 PM
        this.gapBetweenCycles = 2;             // Default 2 minutes gap
        this.totalCycles = 0;
    }
    
    public int calculateCycles(int studyMinutes, int breakMinutes) {
        if (startTime == null || endTime == null) {
            return 0;
        }
        
        Duration totalDuration = Duration.between(startTime, endTime);
        long totalMinutes = totalDuration.toMinutes();
        
        if (totalMinutes <= 0) {
            return 0;
        }
        
        int cycleDuration = studyMinutes + breakMinutes + gapBetweenCycles;
        int calculatedCycles = (int) (totalMinutes / cycleDuration);
        
        // Ensure at least 1 cycle if time permits
        return Math.max(calculatedCycles, 0);
    }
    
    public double getTotalDurationMinutes() {
        if (startTime == null || endTime == null) {
            return 0;
        }
        return Duration.between(startTime, endTime).toMinutes();
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public int getGapBetweenCycles() {
        return gapBetweenCycles;
    }
    
    public void setGapBetweenCycles(int gapBetweenCycles) {
        this.gapBetweenCycles = gapBetweenCycles;
    }
    
    public int getTotalCycles() {
        return totalCycles;
    }
    
    public void setTotalCycles(int totalCycles) {
        this.totalCycles = totalCycles;
    }
    
    public void reset() {
        this.totalCycles = 0;
        this.startTime = LocalTime.of(9, 0);
        this.endTime = LocalTime.of(17, 0);
        this.gapBetweenCycles = 2;
    }
}