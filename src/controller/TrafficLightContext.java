// controller/TrafficLightContext.java
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

    /**
     * Inicia el ciclo del semáforo en un hilo independiente.
     */
    public void start() {
        if (running) return;
        running = true;
        sound.stopAll();

        worker = new Thread(() -> {
            while (running) {
                try {
                    // Actualiza el estado de "mute" dinámicamente
                    sound.setMuted(gui.getMuteCheck().isSelected());
                    currentState.handle(this);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        worker.start();
    }

    /**
     * Detiene el ciclo del semáforo y todos los sonidos activos.
     */
    public void stop() {
        running = false;
        sound.stopAll();
        if (worker != null) {
            worker.interrupt();
        }
    }

    /**
     * Reinicia el semáforo al estado inicial (rojo), deteniendo todo.
     */
    public void reset() {
        stop();
        gui.setLightColor("RED");
        gui.updateTimer("Listo para iniciar");
        currentState = new RedState();
    }
}
