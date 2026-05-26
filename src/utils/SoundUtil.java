package src.utils;

import javax.sound.sampled.*;
import java.io.*;
import java.util.prefs.Preferences;

/**
 * Sound utility for StudyPulse.
 *
 * Priority order:
 *   1. Custom sound file set by user (WAV or MP3)
 *   2. Bundled default: resources/sounds/intro_notification.mp3
 *   3. Windows system notification sounds (PCM WAV)
 *   4. System beep (last resort)
 */
public class SoundUtil {

    private static final String DEFAULT_SOUND_RELATIVE = "resources/sounds/intro_notification.mp3";

    private static final String[] WINDOWS_SOUNDS = {
        System.getenv("SystemRoot") + "\\Media\\Windows Notify Calendar.wav",
        System.getenv("SystemRoot") + "\\Media\\Windows Notify.wav",
        System.getenv("SystemRoot") + "\\Media\\Windows Exclamation.wav",
        System.getenv("SystemRoot") + "\\Media\\Windows Ding.wav",
        System.getenv("SystemRoot") + "\\Media\\chord.wav"
    };

    private static String customSoundPath = null;
    private static final Preferences prefs = Preferences.userNodeForPackage(SoundUtil.class);

    static {
        customSoundPath = prefs.get("custom.sound.path", null);
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public static void playStudyEndSound() { play(); }
    public static void playBreakEndSound() { play(); }
    public static void playBeep()          { play(); }
    public static void testSound()         { play(); }

    // -------------------------------------------------------------------------
    // Core playback
    // -------------------------------------------------------------------------

    private static void play() {
        // 1. User-selected custom sound
        if (customSoundPath != null) {
            File f = new File(customSoundPath);
            if (f.exists() && playWithOs(f)) return;
        }

        // 2. Bundled default: intro_notification.mp3
        File defaultSound = getDefaultSoundFile();
        if (defaultSound != null && playWithOs(defaultSound)) return;

        // 3. Windows system sounds (PCM WAV — Java audio safe)
        for (String path : WINDOWS_SOUNDS) {
            if (path != null) {
                File f = new File(path);
                if (f.exists() && playWavFile(f)) return;
            }
        }

        // 4. System beep
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

    // -------------------------------------------------------------------------
    // Default sound file resolution
    // -------------------------------------------------------------------------

    private static File getDefaultSoundFile() {
        // Try relative to working directory (normal run from StudyPulse/)
        File f = new File(DEFAULT_SOUND_RELATIVE);
        if (f.exists()) return f;

        // Try relative to the compiled class location
        try {
            File classDir = new File(SoundUtil.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI());
            File projectRoot = classDir.getParentFile();
            f = new File(projectRoot, DEFAULT_SOUND_RELATIVE);
            if (f.exists()) return f;
        } catch (Exception ignored) {}

        return null;
    }

    // -------------------------------------------------------------------------
    // OS-level playback (handles MP3, compressed WAV, any format Windows supports)
    // -------------------------------------------------------------------------

    private static boolean playWithOs(File file) {
        String path = file.getAbsolutePath();
        String ext  = path.toLowerCase();

        return playWithWmplayer(path)
            || playWithPowerShellSoundPlayer(path, ext)
            || playWithStartProcess(path);
    }

    /** Windows Media Player CLI — plays any format Windows supports */
    private static boolean playWithWmplayer(String path) {
        try {
            new ProcessBuilder(
                "cmd", "/c", "start", "/min", "",
                "wmplayer", "/play", "/close", path
            ).redirectErrorStream(true).start();
            return true;
        } catch (Exception e) {
            System.err.println("wmplayer failed: " + e.getMessage());
            return false;
        }
    }

    /** PowerShell SoundPlayer — reliable for WAV files */
    private static boolean playWithPowerShellSoundPlayer(String path, String ext) {
        if (!ext.endsWith(".wav") && !ext.endsWith(".au")) return false;
        try {
            File tempScript = File.createTempFile("sp_", ".ps1");
            tempScript.deleteOnExit();
            try (FileWriter fw = new FileWriter(tempScript)) {
                fw.write("(New-Object System.Media.SoundPlayer '"
                    + path.replace("'", "''") + "').PlaySync()\n");
            }
            new ProcessBuilder(
                "powershell", "-NoProfile", "-NonInteractive",
                "-WindowStyle", "Hidden", "-ExecutionPolicy", "Bypass",
                "-File", tempScript.getAbsolutePath()
            ).redirectErrorStream(true).start();
            return true;
        } catch (Exception e) {
            System.err.println("SoundPlayer failed: " + e.getMessage());
            return false;
        }
    }

    /** Last resort: open with default associated app */
    private static boolean playWithStartProcess(String path) {
        try {
            new ProcessBuilder("cmd", "/c", "start", "", path)
                .redirectErrorStream(true).start();
            return true;
        } catch (Exception e) {
            System.err.println("start failed: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // WAV playback via Java audio (PCM only — used for Windows system sounds)
    // -------------------------------------------------------------------------

    private static boolean playWavFile(File file) {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            AudioFormat fmt = stream.getFormat();

            if (fmt.getEncoding() != AudioFormat.Encoding.PCM_SIGNED
                    && fmt.getEncoding() != AudioFormat.Encoding.PCM_UNSIGNED) {
                AudioFormat pcm = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        fmt.getSampleRate(), 16,
                        fmt.getChannels(), fmt.getChannels() * 2,
                        fmt.getSampleRate(), false);
                stream = AudioSystem.getAudioInputStream(pcm, stream);
            }

            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clip.start();
            return true;
        } catch (Exception e) {
            System.err.println("WAV playback failed for " + file.getName() + ": " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Custom sound management
    // -------------------------------------------------------------------------

    public static boolean setCustomSound(File soundFile) {
        if (soundFile == null || !soundFile.exists()) return false;
        String name = soundFile.getName().toLowerCase();
        if (name.endsWith(".wav") || name.endsWith(".mp3") || name.endsWith(".au")) {
            customSoundPath = soundFile.getAbsolutePath();
            prefs.put("custom.sound.path", customSoundPath);
            return true;
        }
        return false;
    }

    public static void resetToDefaultSound() {
        customSoundPath = null;
        prefs.remove("custom.sound.path");
    }

    public static String getCurrentSoundInfo() {
        if (customSoundPath != null) {
            File f = new File(customSoundPath);
            if (f.exists()) return "✓ " + f.getName();
            customSoundPath = null;
            prefs.remove("custom.sound.path");
            return "Default (custom file missing)";
        }
        File defaultSound = getDefaultSoundFile();
        if (defaultSound != null) return "Default: " + defaultSound.getName();
        for (String path : WINDOWS_SOUNDS) {
            if (path != null && new File(path).exists()) {
                return "Windows: " + new File(path).getName();
            }
        }
        return "System Beep";
    }
}
