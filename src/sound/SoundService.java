// sound/SoundService.java
package sound;

public interface SoundService {
    void beep(int frequencyHz, int durationMs, float volume);
    void patternRed(int totalSeconds);
    void patternAmber(int totalSeconds);
    void patternGreenStable(int totalSeconds);
    void patternGreenBlink(int totalMs);
    void patternBlue(int totalSeconds); // nuevo sonido ecológico
    void stopAll();
    void setMuted(boolean muted);
    boolean isMuted();
}
