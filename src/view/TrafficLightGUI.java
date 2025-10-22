package view;

import javax.swing.*;
import java.awt.*;

public class TrafficLightGUI extends JFrame {
    private final JPanel redLight = new JPanel();
    private final JPanel amberLight = new JPanel();
    private final JPanel greenLight = new JPanel();
    private final JLabel soundLabel = new JLabel(" ", SwingConstants.CENTER);

    public TrafficLightGUI() {
        setTitle("Semáforo Accesible");
        setSize(250, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel lightsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        redLight.setBackground(Color.GRAY);
        amberLight.setBackground(Color.GRAY);
        greenLight.setBackground(Color.GRAY);

        lightsPanel.add(redLight);
        lightsPanel.add(amberLight);
        lightsPanel.add(greenLight);

        soundLabel.setFont(new Font("Arial", Font.BOLD, 14));

        add(lightsPanel, BorderLayout.CENTER);
        add(soundLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

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

    public void setSound(String sound) {
        soundLabel.setText(sound);
        System.out.println(sound);
    }

    public void toggleGreenBlink() {
        if (greenLight.getBackground() == Color.GREEN) {
            greenLight.setBackground(Color.GRAY);
        } else {
            greenLight.setBackground(Color.GREEN);
        }
    }
}
