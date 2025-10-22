// Main.java
import controller.TrafficLightContext;
import sound.ToneSoundService;
import view.TrafficLightGUI;

public class Main {
    public static void main(String[] args) {
        TrafficLightGUI gui = new TrafficLightGUI();
        ToneSoundService sound = new ToneSoundService();
        TrafficLightContext context = new TrafficLightContext(gui, sound);

        gui.getStartButton().addActionListener(e -> context.start());
        gui.getStopButton().addActionListener(e -> context.stop());
        gui.getResetButton().addActionListener(e -> context.reset());
    }
}
