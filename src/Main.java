import controller.TrafficLightContext;
import view.TrafficLightGUI;

public class Main {
    public static void main(String[] args) {
        TrafficLightGUI gui = new TrafficLightGUI();
        TrafficLightContext context = new TrafficLightContext(gui);
        context.run();
    }
}
