package model;

import controller.TrafficLightContext;

public class RedState implements TrafficLightState {
    @Override
    public void handle(TrafficLightContext context) throws InterruptedException {
        context.getGui().setLightColor("RED");
        int duration = context.getGui().getRedTime();
        context.getGui().updateTimer("Rojo: " + duration + "s");
        context.getSound().patternRed(duration);

        for (int i = 0; i < duration && context.isRunning(); i++) {
            Thread.sleep(1000);
            context.getGui().updateTimer("Rojo: " + (duration - i - 1) + "s");
        }
        context.setState(new AmberState());
    }

    @Override
    public String getColor() {
        return "RED";
    }
}
