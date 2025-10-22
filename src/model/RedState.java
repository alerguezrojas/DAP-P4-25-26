package model;

import controller.TrafficLightContext;

public class RedState implements TrafficLightState {

    @Override
    public void handle(TrafficLightContext context) {
        context.getGui().setLightColor("RED");
        context.getGui().setSound("🔴 Sonido: stop (tono grave cada 2s)");
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                System.out.println("⏱️ Semáforo rojo: " + (10 - i) + "s restantes");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        context.setState(new AmberState());
    }

    @Override
    public String getColor() {
        return "RED";
    }
}
