// model/GreenState.java
package model;

import controller.TrafficLightContext;

public class GreenState implements TrafficLightState {
    private static final int BLINK_SECONDS = 3;
    private static final int BLINK_STEP_MS = 200;

    @Override
    public void handle(TrafficLightContext context) throws InterruptedException {
        context.getGui().setLightColor("GREEN");

        int total = context.getGui().getGreenTime();
        int stable = Math.max(0, total - BLINK_SECONDS);
        int remainingStable = stable;

        if (remainingStable > 0)
            context.getSound().patternGreenStable(remainingStable);
        context.getGui().updateTimer("Verde: " + (remainingStable + BLINK_SECONDS) + "s");

        while (remainingStable > 0 && context.isRunning()) {
            waitIfPaused(context);

            Thread.sleep(1000);
            remainingStable--;
            context.getGui().updateTimer("Verde: " + (remainingStable + BLINK_SECONDS) + "s");

            if (context.consumeResumeSignal() && remainingStable > 0)
                context.getSound().patternGreenStable(remainingStable);

            if (context.shouldGoEco()) {
                context.setPreviousState(this);
                context.setState(new BlueState());
                return;
            }
        }

        if (!context.isRunning()) return;

        // --- Parpadeo final ---
        int msRemaining = BLINK_SECONDS * 1000;
        int lastShown = -1;
        context.getSound().patternGreenBlink(msRemaining);

        while (msRemaining > 0 && context.isRunning()) {
            waitIfPaused(context);

            int secs = (int) Math.ceil(msRemaining / 1000.0);
            if (secs != lastShown) {
                context.getGui().updateTimer("Verde (parpadeo): " + secs + "s");
                lastShown = secs;
            }

            context.getGui().toggleGreenBlink();

            int step = Math.min(BLINK_STEP_MS, msRemaining);
            Thread.sleep(step);
            msRemaining -= step;

            if (context.consumeResumeSignal() && msRemaining > 0)
                context.getSound().patternGreenBlink(msRemaining);

            if (context.shouldGoEco()) {
                context.setPreviousState(this);
                context.setState(new BlueState());
                return;
            }
        }

        context.getGui().setLightColor("GREEN");
        if (!context.isRunning()) return;
        context.setState(new RedState());
    }

    private void waitIfPaused(TrafficLightContext context) throws InterruptedException {
        synchronized (context.getLock()) {
            while (context.isPaused() && context.isRunning())
                context.getLock().wait();
        }
    }

    @Override public String getColor() { return "GREEN"; }
}
