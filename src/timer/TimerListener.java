 package src.timer;

public interface TimerListener {
    void onTimerTick(int secondsRemaining);
    void onTimerStateChanged(TimerState state);
    void onCycleComplete(int cyclesCompleted);
}