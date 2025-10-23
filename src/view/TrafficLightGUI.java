// view/TrafficLightGUI.java
package view;

import javax.swing.*;
import java.awt.*;

public class TrafficLightGUI extends JFrame {
    private final JPanel redLight   = new JPanel();
    private final JPanel amberLight = new JPanel();
    private final JPanel greenLight = new JPanel();

    private final JLabel statusLabel   = new JLabel(" ", SwingConstants.CENTER);
    private final JButton startButton  = new JButton("Play");
    private final JButton stopButton   = new JButton("Pause");
    private final JButton resetButton  = new JButton("Reset");
    private final JCheckBox muteCheck  = new JCheckBox("Mute");

    // 1..10. Por defecto a la derecha (10) => multiplicador 1 (10/3/10s)
    private final JSlider speedSlider  = new JSlider(1, 10, 10);

    public TrafficLightGUI() {
        setTitle("🚦 Traffic Light");
        setSize(320, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel lightsPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        redLight.setBackground(Color.GRAY);
        amberLight.setBackground(Color.GRAY);
        greenLight.setBackground(Color.GRAY);
        lightsPanel.add(redLight);
        lightsPanel.add(amberLight);
        lightsPanel.add(greenLight);

        JPanel controlPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(resetButton);
        controlPanel.add(muteCheck);
        controlPanel.add(new JLabel("Speed (1=slow, 10=fast)", SwingConstants.CENTER));
        controlPanel.add(speedSlider);

        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        add(lightsPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        add(controlPanel, BorderLayout.NORTH);

        setVisible(true);
    }

    public JButton getStartButton() { return startButton; }
    public JButton getStopButton()  { return stopButton;  }
    public JButton getResetButton() { return resetButton; }
    public JCheckBox getMuteCheck() { return muteCheck;   }

    public int getRedTime()   { return 10 * getSpeedMultiplier(); }
    public int getAmberTime() { return  3 * getSpeedMultiplier(); }
    public int getGreenTime() { return 10 * getSpeedMultiplier(); }

    private int getSpeedMultiplier() { return Math.max(1, 10 - speedSlider.getValue()); }

    public void setLightColor(String color) {
        redLight.setBackground(Color.GRAY);
        amberLight.setBackground(Color.GRAY);
        greenLight.setBackground(Color.GRAY);
        switch (color) {
            case "RED"   -> redLight.setBackground(Color.RED);
            case "AMBER" -> amberLight.setBackground(Color.ORANGE);
            case "GREEN" -> greenLight.setBackground(Color.GREEN);
        }
    }

    public void toggleGreenBlink() {
        if (greenLight.getBackground() == Color.GREEN)
            greenLight.setBackground(Color.GRAY);
        else
            greenLight.setBackground(Color.GREEN);
    }

    public void updateTimer(String text) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(text));
    }
}
