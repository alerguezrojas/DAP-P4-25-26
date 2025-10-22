// model/GreenState.java
package model;

import controller.TrafficLightContext;

public class GreenState implements TrafficLightState {
    private static final int BLINK_SECONDS = 3;
    private static final int BLINK_INTERVAL_MS = 200; // parpadeo más rápido

    @Override
    public void handle(TrafficLightContext context) throws InterruptedException {
        context.getGui().setLightColor("GREEN");
        int duration = context.getGui().getGreenTime();

        // Fase verde estable antes del parpadeo
        int stable = Math.max(0, duration - BLINK_SECONDS);
        context.getGui().updateTimer("Verde: " + duration + "s");
        context.getSound().patternGreenStable(stable);

        for (int i = 0; i < stable && context.isRunning(); i++) {
            Thread.sleep(1000);
            context.getGui().updateTimer("Verde: " + (duration - i - 1) + "s");
        }

        // Fase de parpadeo rápido con contador descendente 3 → 2 → 1
        long end = System.currentTimeMillis() + BLINK_SECONDS * 1000L;
        context.getSound().patternGreenBlink(BLINK_SECONDS * 1000);

        int lastShown = -1;
        while (context.isRunning() && System.currentTimeMillis() < end) {
            long remainingMs = end - System.currentTimeMillis();
            int secs = (int) Math.ceil(remainingMs / 1000.0);

            if (secs != lastShown) {
                context.getGui().updateTimer("Verde: " + secs + "s");
                lastShown = secs;
            }

            context.getGui().toggleGreenBlink();
            Thread.sleep(BLINK_INTERVAL_MS);
        }

        // Asegura luz verde encendida al terminar el parpadeo
        context.getGui().setLightColor("GREEN");

        // Siguiente estado
        context.setState(new RedState());
    }

    @Override
    public String getColor() {
        return "GREEN";
    }
}
