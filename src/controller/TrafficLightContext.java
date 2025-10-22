package controller;

import model.TrafficLightState;
import model.RedState;
import view.TrafficLightGUI;

public class TrafficLightContext {
    private TrafficLightState currentState;
    private final TrafficLightGUI gui;

    public TrafficLightContext(TrafficLightGUI gui) {
        this.gui = gui;
        this.currentState = new RedState();
    }

    public void setState(TrafficLightState state) {
        this.currentState = state;
        run();
    }

    public TrafficLightState getState() {
        return currentState;
    }

    public TrafficLightGUI getGui() {
        return gui;
    }

    public void run() {
        new Thread(() -> currentState.handle(this)).start();
    }
}
