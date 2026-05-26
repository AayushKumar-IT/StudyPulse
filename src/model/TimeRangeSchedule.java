package src.model;

import java.time.LocalTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TimeRangeSchedule {
    private LocalTime startTime;
    private LocalTime endTime;
    private int studyMinutes;
    private int breakMinutes;
    private List<TimeBlock> timeBlocks;
    
    public TimeRangeSchedule() {
        this.startTime = LocalTime.of(10, 0);  // Default 10:00 AM
        this.endTime = LocalTime.of(13, 30);   // Default 1:30 PM
        this.studyMinutes = 25;
        this.breakMinutes = 5;
        this.timeBlocks = new ArrayList<>();
    }
    
    public static class TimeBlock {
        public enum BlockType { STUDY, BREAK }
        private BlockType type;
        private LocalTime startTime;
        private LocalTime endTime;
        private int durationMinutes;
        
        public TimeBlock(BlockType type, LocalTime startTime, int durationMinutes) {
            this.type = type;
            this.startTime = startTime;
            this.durationMinutes = durationMinutes;
            this.endTime = startTime.plusMinutes(durationMinutes);
        }
        
        public BlockType getType() { return type; }
        public LocalTime getStartTime() { return startTime; }
        public LocalTime getEndTime() { return endTime; }
        public int getDurationMinutes() { return durationMinutes; }
        
        @Override
        public String toString() {
            return String.format("%s: %s - %s (%d min)", 
                type == BlockType.STUDY ? "Study" : "Break",
                startTime.toString(), endTime.toString(), durationMinutes);
        }
    }
    
    public int calculateCycles() {
        if (startTime == null || endTime == null || studyMinutes <= 0 || breakMinutes <= 0) {
            return 0;
        }
        
        Duration totalDuration = Duration.between(startTime, endTime);
        long totalMinutes = totalDuration.toMinutes();
        
        if (totalMinutes <= 0) {
            return 0;
        }
        
        int cycleDuration = studyMinutes + breakMinutes;
        int cycles = (int) (totalMinutes / cycleDuration);
        
        return Math.max(cycles, 0);
    }
    
    public List<TimeBlock> generateSchedule() {
        timeBlocks.clear();
        
        if (startTime == null || endTime == null) {
            return timeBlocks;
        }
        
        int cycles = calculateCycles();
        LocalTime currentTime = startTime;
        
        for (int i = 0; i < cycles; i++) {
            // Add study block
            timeBlocks.add(new TimeBlock(TimeBlock.BlockType.STUDY, currentTime, studyMinutes));
            currentTime = currentTime.plusMinutes(studyMinutes);
            
            // Add break block (except after last cycle)
            if (i < cycles - 1) {
                timeBlocks.add(new TimeBlock(TimeBlock.BlockType.BREAK, currentTime, breakMinutes));
                currentTime = currentTime.plusMinutes(breakMinutes);
            }
        }
        
        return timeBlocks;
    }
    
    public String getScheduleSummary() {
        int cycles = calculateCycles();
        long totalMinutes = Duration.between(startTime, endTime).toMinutes();
        long usedMinutes = (long) cycles * (studyMinutes + breakMinutes);
        long remainingMinutes = totalMinutes - usedMinutes;
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== SCHEDULE SUMMARY ===\n");
        sb.append(String.format("Time Range: %s to %s\n", startTime.toString(), endTime.toString()));
        sb.append(String.format("Total Duration: %.1f hours (%d minutes)\n", totalMinutes / 60.0, totalMinutes));
        sb.append(String.format("Study Duration: %d minutes\n", studyMinutes));
        sb.append(String.format("Break Duration: %d minutes\n", breakMinutes));
        sb.append(String.format("Complete Cycles: %d\n", cycles));
        sb.append(String.format("Total Study Time: %d minutes\n", cycles * studyMinutes));
        sb.append(String.format("Total Break Time: %d minutes\n", cycles * breakMinutes));
        sb.append(String.format("Remaining Time: %d minutes\n", remainingMinutes));
        sb.append("\n=== DETAILED SCHEDULE ===\n");
        
        for (int i = 0; i < timeBlocks.size(); i++) {
            TimeBlock block = timeBlocks.get(i);
            sb.append(String.format("%d. %s\n", i + 1, block.toString()));
        }
        
        return sb.toString();
    }
    
    // Getters and Setters
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    
    public int getStudyMinutes() { return studyMinutes; }
    public void setStudyMinutes(int studyMinutes) { this.studyMinutes = studyMinutes; }
    
    public int getBreakMinutes() { return breakMinutes; }
    public void setBreakMinutes(int breakMinutes) { this.breakMinutes = breakMinutes; }
    
    public List<TimeBlock> getTimeBlocks() { return timeBlocks; }
    
    public long getTotalMinutes() {
        return Duration.between(startTime, endTime).toMinutes();
    }
}