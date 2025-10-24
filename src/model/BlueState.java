// model/BlueState.java
package model;

import controller.TrafficLightContext;

/**
 * Estado azul ecológico.
 * Indica que los vehículos deben apagar el motor temporalmente.
 * Dura entre 3 y 6 segundos y devuelve al estado anterior al finalizar.
 */
public class BlueState implements TrafficLightState {
    @Override
    public void handle(TrafficLightContext context) throws InterruptedException {
        context.getGui().setLightColor("BLUE");

        int duration = 3 + (int)(Math.random() * 4); // entre 3 y 6 s
        int remaining = duration;

        // 🚫 Detener sonidos previos para evitar solapamiento
        context.getSound().stopAll();
        context.getSound().patternBlue(duration);

        context.getGui().updateTimer("🌱 Azul ecológico: " + remaining + "s");

        while (remaining > 0 && context.isRunning()) {
            waitIfPaused(context);
            Thread.sleep(1000);
            remaining--;
            context.getGui().updateTimer("🌱 Azul ecológico: " + remaining + "s");

            if (context.consumeResumeSignal() && remaining > 0)
                context.getSound().patternBlue(remaining);
        }

        if (!context.isRunning()) return;

        // Retornar al color anterior (el que estaba antes de la parada ecológica)
        TrafficLightState prev = context.getPreviousState();
        if (prev != null)
            context.setState(prev);
        else
            context.setState(new RedState());
    }

    private void waitIfPaused(TrafficLightContext context) throws InterruptedException {
        synchronized (context.getLock()) {
            while (context.isPaused() && context.isRunning())
                context.getLock().wait();
        }
    }

    @Override public String getColor() { return "BLUE"; }
}
