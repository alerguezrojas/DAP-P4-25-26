// sound/ToneSoundService.java
package sound;

import javax.sound.sampled.*;
import java.util.concurrent.*;

public class ToneSoundService implements SoundService {
    private volatile boolean muted = false;
    private volatile ScheduledExecutorService scheduler = null;

    @Override public synchronized void setMuted(boolean muted) { this.muted = muted; }
    @Override public boolean isMuted() { return muted; }

    /** Cancela inmediatamente cualquier patrón en curso. */
    @Override
    public synchronized void stopAll() {
        if (scheduler != null) {
            scheduler.shutdownNow(); // corta sleeps y tareas pendientes al instante
            scheduler = null;
        }
    }

    private synchronized void ensureScheduler() {
        stopAll(); // garantiza uno solo
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ToneScheduler");
            t.setDaemon(true);
            return t;
        });
    }

    // --------- Patrones ---------

    @Override
    public void patternRed(int totalSeconds) {
        if (muted) return;
        long totalMs = totalSeconds * 1000L;
        schedulePattern(totalMs, 2000, 440, 150, 0.7f);
    }

    @Override
    public void patternAmber(int totalSeconds) {
        if (muted) return;
        long totalMs = totalSeconds * 1000L;
        schedulePattern(totalMs, 1000, 700, 120, 0.7f);
    }

    @Override
    public void patternGreenStable(int totalSeconds) {
        if (muted) return;
        long totalMs = totalSeconds * 1000L;
        schedulePattern(totalMs, 2000, 880, 120, 0.8f);
    }

    @Override
    public void patternGreenBlink(int totalMs) {
        if (muted) return;
        schedulePattern(totalMs, 300, 1200, 120, 0.9f);
    }

    @Override
    public void patternBlue(int totalSeconds) {
        if (muted) return;
        long totalMs = totalSeconds * 1000L;
        // tono suave ecológico
        schedulePattern(totalMs, 1000, 500, 180, 0.5f);
    }

    // --------- Infraestructura de patrones ---------

    private void schedulePattern(long totalMs, int intervalMs, int freq, int beepMs, float vol) {
        ensureScheduler();
        final long end = System.currentTimeMillis() + totalMs;

        Runnable task = () -> {
            if (muted) return;
            if (System.currentTimeMillis() >= end) {
                stopAll();
                return;
            }
            // Beep corto; si se llama stopAll(), el siguiente ciclo ya no se ejecuta
            playTone(freq, beepMs, vol);
        };

        // ejecuta inmediatamente y luego cada intervalo
        scheduler.scheduleAtFixedRate(task, 0, intervalMs, TimeUnit.MILLISECONDS);

        // tarea guardián para terminar por tiempo
        scheduler.schedule(this::stopAll, totalMs + 50, TimeUnit.MILLISECONDS);
    }

    // --------- Generación del tono ---------
    @Override
    public void beep(int frequencyHz, int durationMs, float volume) {
        if (muted) return;
        ensureScheduler();
        scheduler.execute(() -> playTone(frequencyHz, durationMs, volume));
    }

    private void playTone(int freq, int ms, float volume) {
        try {
            float sampleRate = 44100f;
            byte[] buf = new byte[(int)(ms * sampleRate / 1000) * 2]; // 16-bit mono
            double twoPiF = 2 * Math.PI * freq;
            for (int i = 0, sample = 0; i < buf.length; i += 2, sample++) {
                double t = sample / sampleRate;
                short val = (short)(Math.sin(twoPiF * t) * 32767 * clamp(volume));
                buf[i]     = (byte)(val & 0xff);
                buf[i + 1] = (byte)((val >> 8) & 0xff);
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
}
