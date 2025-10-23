// model/RedState.java
package model;

import controller.TrafficLightContext;

public class RedState implements TrafficLightState {
    @Override
    public void handle(TrafficLightContext context) throws InterruptedException {
        context.getGui().setLightColor("RED");

        int total = context.getGui().getRedTime();
        int remaining = total;

        // Lanzar sonido para toda la fase al inicio
        context.getSound().patternRed(remaining);
        context.getGui().updateTimer("⏱️ Rojo: " + remaining + "s");

        while (remaining > 0 && context.isRunning()) {
            // Pausa cooperativa
            waitIfPaused(context);

            // Esperar 1 segundo de trabajo
            Thread.sleep(1000);
            remaining--;
            context.getGui().updateTimer("⏱️ Rojo: " + remaining + "s");

            // Si venimos de reanudar, relanzar audio con el tiempo restante
            if (context.consumeResumeSignal() && remaining > 0) {
                context.getSound().patternRed(remaining);
            }
        }

        if (!context.isRunning()) return;
        context.setState(new AmberState());
    }

    private void waitIfPaused(TrafficLightContext context) throws InterruptedException {
        synchronized (context.getLock()) {
            while (context.isPaused() && context.isRunning()) {
                context.getLock().wait();
            }
        }
    }

    @Override public String getColor() { return "RED"; }
}
