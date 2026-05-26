# StudyPulse 🎯

A **time-range Pomodoro timer** built in Java Swing. Instead of running indefinitely, StudyPulse lets you define a start time and end time for your study session — it automatically calculates how many Pomodoro cycles fit in that window and runs them back-to-back with break notifications and sound alerts.

---

## Screenshots

| Main UI | Break Notification | Settings Panel |
|---|---|---|
| ![Main UI](screenshots/main-ui.png) | ![Break Notification](screenshots/break-notification.png) | ![Settings](screenshots/settings-panel.png) |

---

## Features

- **Time-range scheduling** — set a start and end time, StudyPulse calculates the number of cycles automatically
- **Configurable durations** — customize study and break lengths in minutes
- **Schedule timeline** — visual breakdown of every study and break block before you start
- **Study/break popups** — interactive dialogs at the end of each session so you control when the next one begins
- **Sound notifications** — plays your chosen sound at session transitions
- **Custom sound support** — use any `.wav` or `.mp3` file from your device via Settings → Sound Settings
- **Persistent settings** — your sound choice and timer settings are saved across restarts
- **Manual mode** — start a free-running Pomodoro without a time range
- **Session stats** — tracks cycles completed and total study time

---

## Project Structure

```
StudyPulse/
├── src/
│   ├── Main.java                    # Entry point
│   ├── model/
│   │   ├── SessionData.java         # Tracks cycles and study time
│   │   ├── UserSettings.java        # Loads/saves study & break durations
│   │   ├── ScheduleConfig.java      # Schedule configuration model
│   │   └── TimeRangeSchedule.java   # Time-range schedule with cycle calculation
│   ├── timer/
│   │   ├── PomodoroTimer.java       # Core timer logic (scheduled & manual modes)
│   │   ├── TimerListener.java       # Callback interface for timer events
│   │   └── TimerState.java          # Enum: STUDY, BREAK, PAUSED, IDLE
│   ├── ui/
│   │   ├── MainFrame.java           # Main application window
│   │   ├── TimerPanel.java          # Countdown display
│   │   ├── ControlPanel.java        # Start / Pause / Reset buttons
│   │   └── SoundSettingsDialog.java # Sound customization dialog
│   └── utils/
│       ├── SoundUtil.java           # Sound playback (WAV + MP3 via Windows)
│       └── TimeFormatter.java       # Formats seconds to MM:SS
├── resources/
│   ├── config.properties            # Saved user settings
│   └── sounds/
│       ├── bell.wav                 # Default WAV sound
│       └── intro_notification.mp3   # Default MP3 notification sound
├── screenshots/                     # UI screenshots
├── SOUND_SETUP.md                   # Detailed sound configuration guide
├── LICENSE
└── README.md
```

---

## Requirements

- **Java 11 or higher** (tested on Java 25)
- Windows OS (sound playback uses Windows Media Player and PowerShell)
- No external libraries or build tools required

---

## Building and Running

### Step 1 — Navigate to the project folder

```bash
cd StudyPulse
```

> Make sure you are inside the `StudyPulse` folder (where `src/` is visible), not the parent directory.

### Step 2 — Create the output directory

```bash
mkdir -p out
```

On Windows CMD:
```cmd
mkdir out
```

### Step 3 — Compile all source files

```bash
javac -d out src/Main.java src/model/*.java src/timer/*.java src/utils/*.java src/ui/*.java
```

### Step 4 — Run the application

```bash
java -cp out src.Main
```

### One-liner (compile + run)

```bash
javac -d out src/Main.java src/model/*.java src/timer/*.java src/utils/*.java src/ui/*.java && java -cp out src.Main
```

---

## How to Use

### Scheduled Mode (recommended)

1. Set your **Start Time** and **End Time** using the hour/minute dropdowns
2. Enter your preferred **Study Time** and **Break Time** in minutes
3. Click **📊 Calculate Schedule** — the timeline shows all your study and break blocks
4. Click **▶ Schedule Start** to begin
5. At the end of each study or break session, a popup appears — click **OK** to start the next block or **Cancel** to pause

### Manual Mode

1. Click **▶ Schedule Start** without calculating a schedule first
2. The timer runs in free Pomodoro mode using your configured study/break durations

### Changing the Notification Sound

1. Go to **Settings → Sound Settings** in the menu bar
2. Click **📁 Change Sound File**
3. Select any `.wav` or `.mp3` file from your device
4. Click **🔊 Test Sound** to preview it

See [SOUND_SETUP.md](SOUND_SETUP.md) for full details on sound configuration.

---

## Configuration

Settings are saved automatically to `resources/config.properties`:

| Property | Default | Description |
|---|---|---|
| `study.minutes` | `25` | Length of each study session |
| `break.minutes` | `5` | Length of each break |

The custom sound path is saved using Java Preferences (Windows Registry) and persists across restarts.

---

## How Cycle Calculation Works

Given a time range, StudyPulse calculates:

```
cycles = floor(total_minutes / (study_minutes + break_minutes))
```

For example, a 3-hour window (180 min) with 25-min study and 5-min break:

```
cycles = floor(180 / 30) = 6 cycles
```

The remaining time after the last cycle is shown in the schedule summary.

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Author

Built by **Aayush (Neyash)** — 2024
