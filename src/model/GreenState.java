package model;

import controller.TrafficLightContext;

public class GreenState implements TrafficLightState {

    @Override
    public void handle(TrafficLightContext context) {
        context.getGui().setLightColor("GREEN");
        context.getGui().setSound("🟢 Sonido: paso permitido (tono agudo cada 2s)");

        // Fase verde estable
        try {
            for (int i = 0; i < 7; i++) {
                Thread.sleep(1000);
                System.out.println("⏱️ Semáforo verde: " + (10 - i) + "s restantes");
            }

            // Parpadeo rápido (3 segundos)
            for (int i = 0; i < 3; i++) {
                context.getGui().toggleGreenBlink();
                context.getGui().setSound("⚡ Sonido rápido (1 beep/0.3s)");
                Thread.sleep(500);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        context.setState(new RedState());
    }

    @Override
    public String getColor() {
        return "GREEN";
    }
}
