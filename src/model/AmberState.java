package model;

import controller.TrafficLightContext;

public class AmberState implements TrafficLightState {

    @Override
    public void handle(TrafficLightContext context) {
        context.getGui().setLightColor("AMBER");
        context.getGui().setSound("🟡 Sonido: precaución (tono medio)");
        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(1000);
                System.out.println("⏱️ Semáforo ámbar: " + (3 - i) + "s restantes");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        context.setState(new GreenState());
    }

    @Override
    public String getColor() {
        return "AMBER";
    }
}
