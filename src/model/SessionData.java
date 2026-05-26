 package src.model;

public class SessionData {
    private int cyclesCompleted;
    private int totalStudyMinutes;
    
    public SessionData() {
        this.cyclesCompleted = 0;
        this.totalStudyMinutes = 0;
    }
    
    public void incrementCycles() {
        cyclesCompleted++;
    }
    
    public void addStudyMinutes(int minutes) {
        totalStudyMinutes += minutes;
    }
    
    public int getCyclesCompleted() {
        return cyclesCompleted;
    }
    
    public int getTotalStudyMinutes() {
        return totalStudyMinutes;
    }
    
    public void reset() {
        cyclesCompleted = 0;
        totalStudyMinutes = 0;
    }
}