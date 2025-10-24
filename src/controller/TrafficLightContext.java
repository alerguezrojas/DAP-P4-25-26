// controller/TrafficLightContext.java
package controller;

import model.*;
import sound.SoundService;
import view.TrafficLightGUI;

import java.util.Random;

public class TrafficLightContext {
    private TrafficLightState currentState;
    private TrafficLightState previousState; // para volver tras azul
    private final TrafficLightGUI gui;
    private final SoundService sound;

    private volatile boolean running = false;
    private volatile boolean paused = false;
    private volatile boolean resumeSignal = false;

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
    public TrafficLightState getPreviousState() { return previousState; }
    public void setPreviousState(TrafficLightState prev) { this.previousState = prev; }

    public boolean isRunning() { return running; }
    public boolean isPaused()  { return paused;  }
    public TrafficLightGUI getGui() { return gui; }
    public SoundService getSound()  { return sound; }
    public Object getLock()         { return lock; }

    public boolean consumeResumeSignal() {
        synchronized (lock) {
            if (resumeSignal) {
                resumeSignal = false;
                return true;
            }
            return false;
        }
    }

    /** Devuelve true con baja probabilidad (~10%) para activar el azul. */
    public boolean shouldGoEco() {
        return random.nextInt(10) == 0; // 10% de probabilidad
    }

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

    public void stop() {
        synchronized (lock) {
            if (!running || paused) return;
            paused = true;
            sound.stopAll();
        }
    }

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
