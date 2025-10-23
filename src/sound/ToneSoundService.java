// sound/ToneSoundService.java
package sound;

import javax.sound.sampled.*;

public class ToneSoundService implements SoundService {
    private volatile boolean running = true;
    private volatile boolean muted = false;

    @Override
    public void setMuted(boolean muted) { this.muted = muted; }

    @Override
    public boolean isMuted() { return muted; }

    @Override
    public void beep(int frequencyHz, int durationMs, float volume) {
        if (muted) return;
        new Thread(() -> playTone(frequencyHz, durationMs, volume), "Tone-Beep").start();
    }

    @Override
    public void patternRed(int totalSeconds) {
        if (muted) return;
        runPattern(totalSeconds, 2000, 440, 150, 0.7f);
    }

    @Override
    public void patternAmber(int totalSeconds) {
        if (muted) return;
        runPattern(totalSeconds, 1000, 700, 120, 0.7f);
    }

    @Override
    public void patternGreenStable(int totalSeconds) {
        if (muted) return;
        runPattern(totalSeconds, 2000, 880, 120, 0.8f);
    }

    @Override
    public void patternGreenBlink(int totalMs) {
        if (muted) return;
        running = true;
        new Thread(() -> {
            long end = System.currentTimeMillis() + totalMs;
            while (running && System.currentTimeMillis() < end) {
                if (!muted) playTone(1200, 120, 0.9f);
                sleep(300);
            }
        }, "Tone-Blink").start();
    }

    @Override
    public void stopAll() {
        running = false; // haría que los patrones salgan en su siguiente ciclo
    }

    // ================== Internos ==================

    private void runPattern(int totalSeconds, int intervalMs, int freq, int beepMs, float volume) {
        running = true;
        new Thread(() -> {
            long end = System.currentTimeMillis() + totalSeconds * 1000L;
            while (running && System.currentTimeMillis() < end) {
                if (!muted) playTone(freq, beepMs, volume);
                sleep(intervalMs);
            }
        }, "Tone-Pattern-" + freq).start();
    }

    private void playTone(int freq, int ms, float volume) {
        try {
            float sampleRate = 44100f;
            byte[] buf = new byte[(int)(ms * sampleRate / 1000) * 2]; // 16-bit mono
            double twoPiF = 2 * Math.PI * freq;
            for (int i = 0, sample = 0; i < buf.length; i += 2, sample++) {
                double t = sample / sampleRate;
                short val = (short) (Math.sin(twoPiF * t) * 32767 * clamp(volume));
                buf[i]     = (byte) (val & 0xff);
                buf[i + 1] = (byte) ((val >> 8) & 0xff);
            }
            AudioFormat af = new AudioFormat(sampleRate, 16, 1, true, false);
            try (SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
                line.open(af);
                line.start();
                line.write(buf, 0, buf.length);
                line.drain();
                line.stop();
            }
        } catch (Exception ignored) {}
    }

    private static float clamp(float v) { return Math.max(0f, Math.min(1f, v)); }
    private static void sleep(long ms) { try { Thread.sleep(ms); } catch (InterruptedException ignored) {} }
}
