// view/TrafficLightGUI.java
package view;

import javax.swing.*;
import java.awt.*;

public class TrafficLightGUI extends JFrame {
    private final JPanel redLight   = new JPanel();
    private final JPanel amberLight = new JPanel();
    private final JPanel greenLight = new JPanel();
    private final JPanel blueLight  = new JPanel();

    private final JLabel statusLabel   = new JLabel(" ", SwingConstants.CENTER);
    private final JButton startButton  = new JButton("Iniciar");
    private final JButton stopButton   = new JButton("Pausar");
    private final JButton resetButton  = new JButton("Reset");
    private final JCheckBox muteCheck  = new JCheckBox("Silenciar sonidos");
    private final JSlider speedSlider  = new JSlider(1, 10, 10); // a la derecha por defecto

    public TrafficLightGUI() {
        setTitle("🚦 Semáforo Ecológico Accesible");
        setSize(350, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // === Panel de luces ===
        JPanel lightsPanel = new JPanel(new GridLayout(4, 1, 15, 15));
        redLight.setBackground(Color.GRAY);
        amberLight.setBackground(Color.GRAY);
        greenLight.setBackground(Color.GRAY);
        blueLight.setBackground(Color.GRAY);

        lightsPanel.add(redLight);
        lightsPanel.add(amberLight);
        lightsPanel.add(greenLight);
        lightsPanel.add(blueLight);

        // === Panel de control ===
        JPanel controlPanel = new JPanel(new GridLayout(6, 1, 5, 5));
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

    // === Getters para controladores ===
    public JButton getStartButton() { return startButton; }
    public JButton getStopButton()  { return stopButton;  }
    public JButton getResetButton() { return resetButton; }
    public JCheckBox getMuteCheck() { return muteCheck;   }

    // === Tiempos base ajustables por velocidad ===
    public int getRedTime()   { return 10 * getSpeedMultiplier(); }
    public int getAmberTime() { return  3 * getSpeedMultiplier(); }
    public int getGreenTime() { return 10 * getSpeedMultiplier(); }

    private int getSpeedMultiplier() {
        // Cuanto más a la derecha el slider, más rápido
        return Math.max(1, 10 - speedSlider.getValue());
    }

    // === Control de luces ===
    public void setLightColor(String color) {
        redLight.setBackground(Color.GRAY);
        amberLight.setBackground(Color.GRAY);
        greenLight.setBackground(Color.GRAY);
        blueLight.setBackground(Color.GRAY);

        switch (color) {
            case "RED"   -> redLight.setBackground(Color.RED);
            case "AMBER" -> amberLight.setBackground(Color.ORANGE);
            case "GREEN" -> greenLight.setBackground(Color.GREEN);
            case "BLUE"  -> blueLight.setBackground(new Color(80, 150, 255)); // tono azul suave
        }
    }

    public void toggleGreenBlink() {
        if (greenLight.getBackground() == Color.GREEN)
            greenLight.setBackground(Color.GRAY);
        else
            greenLight.setBackground(Color.GREEN);
    }

    // === Actualizar contador ===
    public void updateTimer(String text) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(text));
    }
}
