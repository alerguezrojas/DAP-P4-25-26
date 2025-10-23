// controller/TrafficLightContext.java
package controller;

import model.*;
import sound.SoundService;
import view.TrafficLightGUI;

public class TrafficLightContext {
    private TrafficLightState currentState;
    private final TrafficLightGUI gui;
    private final SoundService sound;

    private volatile boolean running = false;
    private volatile boolean paused = false;
    private volatile boolean resumeSignal = false;

    private final Object lock = new Object();
    private Thread worker;

    public TrafficLightContext(TrafficLightGUI gui, SoundService sound) {
        this.gui = gui;
        this.sound = sound;
        this.currentState = new RedState();
    }

    public void setState(TrafficLightState state) {
        this.currentState = state;
    }

    public boolean isRunning() { return running; }
    public boolean isPaused()  { return paused;  }
    public TrafficLightGUI getGui() { return gui; }
    public SoundService getSound()  { return sound; }
    public Object getLock()         { return lock; }

    /**
     * Señal que consumen los estados para relanzar el patrón de audio
     * tras reanudar una pausa.
     */
    public boolean consumeResumeSignal() {
        synchronized (lock) {
            if (resumeSignal) {
                resumeSignal = false;
                return true;
            }
            return false;
        }
    }

    /**
     * Iniciar o reanudar.
     */
    public void start() {
        synchronized (lock) {
            if (!running) {
                // Arrancar hilo de ciclo
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
                // Reanudar
                paused = false;
                resumeSignal = true; // para relanzar sonido acorde al tiempo restante
                lock.notifyAll();
            }
        }
    }

    /**
     * Pausar (no destruye el hilo ni reinicia el estado).
     */
    public void stop() {
        synchronized (lock) {
            if (!running || paused) return;
            paused = true;
            // Cortar audio inmediatamente
            sound.stopAll();
        }
    }

    /**
     * Reset total: para, limpia GUI y vuelve a rojo.
     */
    public void reset() {
        synchronized (lock) {
            running = false;
            paused  = false;
            resumeSignal = false;
            sound.stopAll();
            if (worker != null) worker.interrupt();
            gui.setLightColor("RED");
            gui.updateTimer("Listo para iniciar");
            currentState = new RedState();
        }
    }
}
