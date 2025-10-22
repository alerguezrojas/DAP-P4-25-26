package model;

import controller.TrafficLightContext;

public interface TrafficLightState {
    void handle(TrafficLightContext context);
    String getColor();
}
