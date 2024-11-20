package othello2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class LevelSelector extends JFrame {
    private int selectedLevel = 0;
    private String currentUser = null;
    private static ArrayList<GameRecord> gameRecords = new ArrayList<>();

    public LevelSelector() {
        showLoginDialog();
        if (currentUser != null) {
            initUI();
        } else {
            System.exit(0);
        }
    }

    private void initUI() {
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

    private void showLoginDialog() {
        JTextField usernameField = new JTextField(10);
        JPasswordField passwordField = new JPasswordField(10);
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("아이디:"));
        panel.add(usernameField);
        panel.add(new JLabel("비밀번호:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(null, panel, "로그인", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            currentUser = usernameField.getText();
            JOptionPane.showMessageDialog(null, "로그인 성공: " + currentUser);
        }
    }
    
    public void setCurrentUser(String user) {
        this.currentUser = user;
    }

    public static void addGameRecord(GameRecord record) {
        gameRecords.add(record);
    }

    public static ArrayList<GameRecord> getGameRecords() {
        return gameRecords;
    }

    private void startGame() {
        this.setVisible(false);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUI(selectedLevel, currentUser, LevelSelector.this);
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