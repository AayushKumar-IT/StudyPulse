# Sound Setup Guide for StudyPulse

## Default Sounds
The application uses system beep as default fallback.

## Adding Custom Sounds

### Option 1: Use Built-in Sound Settings (Recommended)
1. Run StudyPulse
2. Go to `Settings` → `Sound Settings`
3. Click "Change Sound File"
4. Select any `.wav`, `.mp3`, or `.au` file from your computer
5. Click "Test Sound" to verify
6. Your sound preference is saved automatically

### Option 2: Manual File Placement
1. Create folder: `resources/sounds/`
2. Add sound files:
   - `bell.wav` - For study completion and general notifications
   - `break_end.wav` - For break completion (optional)
3. Restart the application

## Where to Find Free Sound Files

### Recommended Websites:
- **Freesound.org** - Large collection of royalty-free sounds
- **Zapsplat.com** - Professional sound effects
- **Pixabay Music** - Free sound effects
- **Microsoft Sound Library** - Built-in Windows sounds

### Suggested Sound Types:
- **Productivity**: Soft chimes, gentle bells
- **Motivation**: Short fanfares, achievement sounds
- **Minimalist**: Single beep, click sounds
- **Nature**: Bird chirps, water drops

## Sound File Requirements:
- Format: WAV, MP3, or AU
- Duration: 1-3 seconds recommended
- Size: < 1 MB for best performance
- Sample rate: Any (auto-converted by Java)

## Troubleshooting

### No sound playing?
1. Check system volume
2. Try different sound file format
3. Use "Test Sound" in Sound Settings
4. Restart the application

### Want to revert to default?
Go to `Settings` → `Sound Settings` → "Reset to Default"

### Custom sound not working?
- Ensure file isn't corrupted
- Try converting to WAV format
- Check file permissions














System file data
# Sound Setup Guide — StudyPulse

This guide explains how the sound system works in StudyPulse and how to configure it.

---

## How It Works

StudyPulse plays a notification sound at the end of every study session and break. The sound system has three layers, tried in order:

1. **Custom sound** — a file you choose via Settings → Sound Settings
2. **Default bundled sound** — `resources/sounds/bell.wav` (included in the project)
3. **System beep** — fallback if no audio file is found

Your custom sound choice is saved automatically using Java Preferences and persists across restarts.

---

## Default Sound

The default sound file is located at:

```
StudyPulse/resources/sounds/bell.wav
```

This file is loaded from the classpath at `/sounds/bell.wav`. If it is missing, the app falls back to the system beep.

---

## Changing the Sound

1. Run the app
2. Go to **Settings → Sound Settings** in the menu bar
3. Click **📁 Change Sound File**
4. Select any `.wav`, `.mp3`, or `.au` file from your computer
5. The sound plays immediately so you can preview it
6. Click **🔊 Test Sound** at any time to hear the current selection

Supported formats: `.wav`, `.mp3`, `.au`

---

## Resetting to Default

1. Go to **Settings → Sound Settings**
2. Click **🔄 Reset to Default**
3. Confirm the prompt

This removes the saved custom path and reverts to `bell.wav`.

---

## Adding Your Own Default Sound

To replace the bundled bell sound permanently:

1. Prepare a `.wav` file (recommended format for best compatibility)
2. Rename it to `bell.wav`
3. Replace the file at `StudyPulse/resources/sounds/bell.wav`
4. Recompile and run

---

## Compiling and Running

From the `StudyPulse` directory:

```bash
# Compile all sources
javac -d out src/Main.java src/model/*.java src/timer/*.java src/utils/*.java src/ui/*.java

# Run (include resources on classpath)
java -cp out:resources src.Main
```

On Windows:

```bash
javac -d out src/Main.java src/model/*.java src/timer/*.java src/utils/*.java src/ui/*.java
java -cp "out;resources" src.Main
```

> The `resources` directory must be on the classpath so `bell.wav` is found at `/sounds/bell.wav`.

---

## Troubleshooting

| Problem | Cause | Fix |
|---|---|---|
| No sound plays | `bell.wav` not on classpath | Run with `-cp "out;resources"` |
| Custom sound not playing | File was moved or deleted | Re-select the file in Sound Settings |
| `UnsupportedAudioFileException` | `.mp3` not supported by JVM | Use a `.wav` file instead |
| Sound plays but very quiet | System volume | Increase OS volume |

---

## Sound File Recommendations

- **Format**: `.wav` (PCM, 16-bit, 44100 Hz) — most compatible
- **Duration**: 1–3 seconds
- **Volume**: Normalised to avoid clipping
- Free sound resources: [freesound.org](https://freesound.org), [zapsplat.com](https://www.zapsplat.com)
