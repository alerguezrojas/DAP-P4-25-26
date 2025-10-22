// sound/SoundService.java
package sound;

public interface SoundService {
    void beep(int frequencyHz, int durationMs, float volume);
    void patternRed(int totalSeconds);
    void patternAmber(int totalSeconds);
    void patternGreenStable(int totalSeconds);
    void patternGreenBlink(int totalMs);
    void stopAll();

    // NUEVO: soporte modo silencioso
    void setMuted(boolean muted);
    default boolean isMuted() { return false; } // opcional
}
