package othello2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClockComponent extends JLabel {
    private long startTime;

    ClockComponent() {
        super("00:00:00", SwingConstants.CENTER);
        startTime = System.currentTimeMillis();
        Timer t = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setText(getTimeString());
            }
        });
        setFont(new Font("Sans Serif", Font.PLAIN, 24));
        t.start();
    }
    String getTimeString() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        long seconds = (elapsedTime / 1000) % 60;
        long minutes = (elapsedTime / (1000 * 60)) % 60;
        long hours = (elapsedTime / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, 50);
    }
}