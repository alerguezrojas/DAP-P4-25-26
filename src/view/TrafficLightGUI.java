// view/TrafficLightGUI.java
package view;

import javax.swing.*;
import java.awt.*;

public class TrafficLightGUI extends JFrame {
    private final JPanel redLight = new JPanel();
    private final JPanel amberLight = new JPanel();
    private final JPanel greenLight = new JPanel();

    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JButton startButton = new JButton("Iniciar");
    private final JButton stopButton = new JButton("Pausar");
    private final JButton resetButton = new JButton("Reset");
    private final JCheckBox muteCheck = new JCheckBox("Silenciar sonidos");

    private final JSlider speedSlider = new JSlider(1, 10, 10);

    public TrafficLightGUI() {
        setTitle("🚦 Semáforo Accesible Deluxe");
        setSize(320, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Luces
        JPanel lightsPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        redLight.setBackground(Color.GRAY);
        amberLight.setBackground(Color.GRAY);
        greenLight.setBackground(Color.GRAY);
        lightsPanel.add(redLight);
        lightsPanel.add(amberLight);
        lightsPanel.add(greenLight);

        // Panel de controles
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(5, 1, 5, 5));
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(resetButton);
        controlPanel.add(muteCheck);
        controlPanel.add(new JLabel("Velocidad (1=lento, 10=rápido)", SwingConstants.CENTER));
        controlPanel.add(speedSlider);

        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        add(lightsPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        add(controlPanel, BorderLayout.NORTH);

        setVisible(true);
    }

    // Getters
    public JButton getStartButton() { return startButton; }
    public JButton getStopButton() { return stopButton; }
    public JButton getResetButton() { return resetButton; }
    public JCheckBox getMuteCheck() { return muteCheck; }

    public int getRedTime() { return 10 * getSpeedMultiplier(); }
    public int getAmberTime() { return 3 * getSpeedMultiplier(); }
    public int getGreenTime() { return 10 * getSpeedMultiplier(); }

    private int getSpeedMultiplier() {
        return Math.max(1, 10 - speedSlider.getValue());
    }

    // Métodos de actualización visual
    public void setLightColor(String color) {
        redLight.setBackground(Color.GRAY);
        amberLight.setBackground(Color.GRAY);
        greenLight.setBackground(Color.GRAY);

        switch (color) {
            case "RED" -> redLight.setBackground(Color.RED);
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
