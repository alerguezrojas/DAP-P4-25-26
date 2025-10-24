// model/BlueState.java
package model;

import controller.TrafficLightContext;

public class BlueState implements TrafficLightState {
    @Override
    public void handle(TrafficLightContext context) throws InterruptedException {
        context.setEcoActive(true);             // estamos en ECO
        context.getGui().setLightColor("BLUE");

        int duration = 3 + (int)(Math.random() * 4); // 3..6 s
        int remaining = duration;

        context.getSound().stopAll();           // 🔇 corta patrón previo
        context.getSound().patternBlue(duration);
        context.getGui().updateTimer("Azul ecológico: " + remaining + "s");

        while (remaining > 0 && context.isRunning()) {
            waitIfPaused(context);
            Thread.sleep(1000);
            remaining--;
            context.getGui().updateTimer("Azul ecológico: " + remaining + "s");

            if (context.consumeResumeSignal() && remaining > 0)
                context.getSound().patternBlue(remaining);
        }

        if (!context.isRunning()) return;

        // salimos de ECO y activamos cooldown para evitar bucles
        context.setEcoActive(false);
        context.startEcoCooldown();

        TrafficLightState prev = context.getPreviousState();
        if (prev != null) {
            context.setState(prev);
        } else {
            context.setState(new RedState());
        }
    }

    private void waitIfPaused(TrafficLightContext context) throws InterruptedException {
        synchronized (context.getLock()) {
            while (context.isPaused() && context.isRunning())
                context.getLock().wait();
        }
    }

    @Override public String getColor() { return "BLUE"; }
}
