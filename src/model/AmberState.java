// model/AmberState.java
package model;

import controller.TrafficLightContext;

public class AmberState implements TrafficLightState {
    @Override
    public void handle(TrafficLightContext context) throws InterruptedException {
        context.getGui().setLightColor("AMBER");

        int total = context.getGui().getAmberTime();
        int remaining = total;

        context.getSound().patternAmber(remaining);
        context.getGui().updateTimer("Amber: " + remaining + "s");

        while (remaining > 0 && context.isRunning()) {
            waitIfPaused(context);

            Thread.sleep(1000);
            remaining--;
            context.getGui().updateTimer("Amber: " + remaining + "s");

            if (context.consumeResumeSignal() && remaining > 0) {
                context.getSound().patternAmber(remaining);
            }
        }

        if (!context.isRunning()) return;
        context.setState(new GreenState());
    }

    private void waitIfPaused(TrafficLightContext context) throws InterruptedException {
        synchronized (context.getLock()) {
            while (context.isPaused() && context.isRunning()) {
                context.getLock().wait();
            }
        }
    }

    @Override public String getColor() { return "AMBER"; }
}
