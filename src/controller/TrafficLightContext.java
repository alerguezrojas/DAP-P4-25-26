// controller/TrafficLightContext.java
package controller;

import model.*;
import sound.SoundService;
import view.TrafficLightGUI;

import java.util.Random;

public class TrafficLightContext {
    private TrafficLightState currentState;
    private TrafficLightState previousState; // estado al que volver tras azul

    private final TrafficLightGUI gui;
    private final SoundService sound;

    private volatile boolean running = false;
    private volatile boolean paused  = false;
    private volatile boolean resumeSignal = false;

    // ---- Control de ECO (anti-bucle) ----
    private volatile boolean ecoActive = false;       // estamos en Azul
    private volatile long ecoCooldownUntil = 0L;      // no se permite ECO antes de este instante
    private static final long ECO_COOLDOWN_MS = 5_000;

    private final Object lock = new Object();
    private Thread worker;
    private final Random random = new Random();

    public TrafficLightContext(TrafficLightGUI gui, SoundService sound) {
        this.gui = gui;
        this.sound = sound;
        this.currentState = new RedState();
    }

    public void setState(TrafficLightState state) { this.currentState = state; }
    public TrafficLightState getCurrentState() { return currentState; }

    public void setPreviousState(TrafficLightState prev) { this.previousState = prev; }
    public TrafficLightState getPreviousState() { return previousState; }

    public TrafficLightGUI getGui() { return gui; }
    public SoundService getSound() { return sound; }
    public Object getLock() { return lock; }

    public boolean isRunning() { return running; }
    public boolean isPaused()  { return paused;  }

    public boolean consumeResumeSignal() {
        synchronized (lock) {
            if (resumeSignal) {
                resumeSignal = false;
                return true;
            }
            return false;
        }
    }

    // ---------- ECO helpers ----------
    public void setEcoActive(boolean value) { ecoActive = value; }
    public void startEcoCooldown() { ecoCooldownUntil = System.currentTimeMillis() + ECO_COOLDOWN_MS; }

    /** Devuelve true (~10%) solo si no está activo ECO ni en cooldown */
    public boolean shouldGoEco() {
        if (ecoActive) return false;
        if (System.currentTimeMillis() < ecoCooldownUntil) return false;
        return random.nextInt(10) == 0; // 10%
    }

    // ---------- Ciclo ----------
    public void start() {
        synchronized (lock) {
            if (!running) {
                running = true;
                paused  = false;
                worker = new Thread(() -> {
                    while (running) {
                        try {
                            currentState.handle(this);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }, "TrafficLight-Worker");
                worker.start();
            } else if (paused) {
                paused = false;
                resumeSignal = true;
                lock.notifyAll();
            }
        }
    }

    /** Pausar (no mata el hilo); corta audio al instante. */
    public void stop() {
        synchronized (lock) {
            if (!running || paused) return;
            paused = true;
            sound.stopAll();
        }
    }

    /** Reset completo: detiene, resetea estado y GUI. */
    public void reset() {
        synchronized (lock) {
            running = false;
            paused  = false;
            resumeSignal = false;
            ecoActive = false;
            startEcoCooldown(); // un mínimo cooldown tras reset
            sound.stopAll();
            if (worker != null) worker.interrupt();
            gui.setLightColor("RED");
            gui.updateTimer("Listo para iniciar");
            currentState = new RedState();
        }
    }
}
