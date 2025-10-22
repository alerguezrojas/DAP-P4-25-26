package controller;

import model.*;
import sound.SoundService;
import view.TrafficLightGUI;

public class TrafficLightContext {
    private TrafficLightState currentState;
    private final TrafficLightGUI gui;
    private final SoundService sound;
    private boolean running = false;
    private Thread worker;

    public TrafficLightContext(TrafficLightGUI gui, SoundService sound) {
        this.gui = gui;
        this.sound = sound;
        this.currentState = new RedState();
    }

    public void setState(TrafficLightState state) {
        this.currentState = state;
    }

    public boolean isRunning() {
        return running;
    }

    public TrafficLightGUI getGui() {
        return gui;
    }

    public SoundService getSound() {
        return sound;
    }

    public void start() {
        if (running) return;
        running = true;
        sound.stopAll();

        worker = new Thread(() -> {
            while (running) {
                try {
                    currentState.handle(this);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        worker.start();
    }

    public void stop() {
        running = false;
        sound.stopAll();
        if (worker != null) worker.interrupt();
    }
}
