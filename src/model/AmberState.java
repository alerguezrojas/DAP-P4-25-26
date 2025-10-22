package model;

import controller.TrafficLightContext;

public class AmberState implements TrafficLightState {
    @Override
    public void handle(TrafficLightContext context) throws InterruptedException {
        context.getGui().setLightColor("AMBER");
        int duration = context.getGui().getAmberTime();
        context.getGui().updateTimer("Ámbar: " + duration + "s");
        context.getSound().patternAmber(duration);

        for (int i = 0; i < duration && context.isRunning(); i++) {
            Thread.sleep(1000);
            context.getGui().updateTimer("Ámbar: " + (duration - i - 1) + "s");
        }
        context.setState(new GreenState());
    }

    @Override
    public String getColor() {
        return "AMBER";
    }
}
