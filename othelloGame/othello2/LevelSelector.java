package othello2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LevelSelector extends JFrame {
    private int selectedLevel = 0;

    public LevelSelector() {
        setTitle("오셀로 게임 레벨 선택");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        JButton[] levelButtons = new JButton[4];
        String[] levelNames = {"EASY", "NORMAL", "HARD", "EXTREME"};

        for (int i = 0; i < 4; i++) {
            final int level = i;
            levelButtons[i] = new JButton(levelNames[i]);
            levelButtons[i].setFont(new Font("Arial", Font.BOLD, 20));
            levelButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedLevel = level;
                    startGame();
                }
            });
            add(levelButtons[i]);
        }

        setLocationRelativeTo(null);
    }

    private void startGame() {
        this.dispose();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUI(selectedLevel);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LevelSelector().setVisible(true);
            }
        });
    }
}