package othello2;
// 박관호 수정 3
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.Border;

public class GUI extends JFrame implements ActionListener {
	private JPanel panel;

	private JButton[][] buttons;
	//게임 상태를 나타내는 말 배열
	private Piece[][] board;
	//게임 로직을 처리하는 객체
	private OthelloGame game;
	private JToolBar toolBar;
	private JLabel blackScoreLabel;
	private JLabel whiteScoreLabel;
    private ClockComponent clockComponent;
    private JLabel turnLabel;
    private boolean isBlackTurn = true; // 게임 시작 시 흑돌
    private JButton menuButton;
    private JPopupMenu popupMenu;
    private JButton switchButton;
    private JLabel userLabel;
    private String currentUser;
    private JPanel recordPanel;
    private JPanel currentPanel; // 현재 표시 중인 패널을 추적하는 변수 추가
    private LevelSelector levelSelector;

    public GUI(int level, String username, LevelSelector levelSelector) {
        game = new OthelloGame(level);
        this.currentUser = username;
        this.levelSelector = levelSelector;
        this.setBoard(game.getBoard());
        initUI();
        this.setVisible(true);
    }

    public void initUI() {
        setTitle("오셀로 게임");
        setSize(700, 750); // 하단 버튼을 위한 추가 공간
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 게임 보드를 표시할 패널
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setLayout(new GridLayout(8, 8, 5, 5));

        // 게임 보드의 각 칸을 나타내는 버튼 배열
        this.buttons = new JButton[8][8];

        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                if (this.board[i][j] != null) {
                    this.buttons[i][j] = new JButton(this.board[i][j].toString());
                } else {
                    this.buttons[i][j] = new JButton(".");
                }
                buttons[i][j].addActionListener(this);
                updateButtonColor(buttons[i][j]);
                panel.add(buttons[i][j]);
            }
        }

        add(panel, BorderLayout.CENTER);

        // 툴바 설정
        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        blackScoreLabel = new JLabel("흑돌 : 2", SwingConstants.CENTER);
        blackScoreLabel.setFont(new Font("Default", Font.BOLD, 20));

        whiteScoreLabel = new JLabel("백돌 : 2", SwingConstants.CENTER);
        whiteScoreLabel.setFont(new Font("Default", Font.BOLD, 20));

        turnLabel = new JLabel("흑돌 턴", SwingConstants.CENTER);
        turnLabel.setFont(new Font("SanSerif", Font.BOLD, 30));
        Border lineBorder = BorderFactory.createLineBorder(Color.GRAY, 2);
        turnLabel.setBorder(lineBorder);

        clockComponent = new ClockComponent();

        toolBar.add(blackScoreLabel);
        toolBar.addSeparator(new Dimension(20, 0));
        toolBar.add(whiteScoreLabel);
        toolBar.addSeparator(new Dimension(30, 0));
        toolBar.add(turnLabel);
        toolBar.addSeparator(new Dimension(60, 0));
        toolBar.add(clockComponent);

        menuButton = new JButton("메뉴");
        menuButton.setFont(new Font("Default", Font.BOLD, 20));
        menuButton.addActionListener(this);

        popupMenu = new JPopupMenu();
        JMenuItem restartItem = new JMenuItem("게임 재시작");
        restartItem.setFont(new Font("Default", Font.BOLD, 20));
        restartItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        popupMenu.add(restartItem);

        JMenuItem giveUpItem = new JMenuItem("게임 포기");
        giveUpItem.setFont(new Font("Default", Font.BOLD, 20));
        giveUpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.gameOver();
                endGame();
            }
        });
        popupMenu.add(giveUpItem);

        toolBar.add(Box.createHorizontalGlue()); // 오른쪽 정렬을 위한 공간
        toolBar.add(menuButton);

        // JToolBar를 프레임의 북쪽에 추가
        add(toolBar, BorderLayout.NORTH);

        // 하단 패널 설정
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        switchButton = new JButton("전적");
        switchButton.addActionListener(this);
        bottomPanel.add(switchButton);

        userLabel = new JLabel("사용자: " + currentUser);
        bottomPanel.add(userLabel);

        add(bottomPanel, BorderLayout.SOUTH);
        currentPanel = panel; // 초기 패널을 게임 패널로 설정

        // 전적 패널 초기화
        recordPanel = new JPanel();
        recordPanel.setLayout(new BoxLayout(recordPanel, BoxLayout.Y_AXIS));
        JLabel recordLabel = new JLabel("이곳에 전적이 표시됩니다");
        recordPanel.add(recordLabel);

        updateScore();
    }

    private void updateTurnLabel() {
        if (isBlackTurn) {
            turnLabel.setText("흑돌 턴");
        } else {
            turnLabel.setText("백돌 턴");
        }
    }

    private void switchPanel() {
        if (currentPanel == panel) {
            remove(panel);
            add(recordPanel, BorderLayout.CENTER);
            switchButton.setText("게임으로 돌아가기");
            currentPanel = recordPanel;
            updateRecordPanel();
        } else {
            remove(recordPanel);
            add(panel, BorderLayout.CENTER);
            switchButton.setText("전적");
            currentPanel = panel;
            printBoardGUI();
        }
        revalidate();
        repaint();
    }

    private void updateRecordPanel() {
        recordPanel.removeAll();
        ArrayList<GameRecord> records = LevelSelector.getGameRecords();
        for (GameRecord record : records) {
            String difficulty;
            switch (record.level) {
                case 0:
                    difficulty = "Easy";
                    break;
                case 1:
                    difficulty = "Normal";
                    break;
                case 2:
                    difficulty = "Hard";
                    break;
                case 3:
                    difficulty = "Extreme";
                    break;
                default:
                    difficulty = "Unknown";
            }
            
            JLabel levelLabel = new JLabel(String.format("[%s]", difficulty));
            recordPanel.add(levelLabel);
            
            String result = record.isWin ? "승 / 패" : "패 / 승";
            JLabel resultLabel = new JLabel(String.format("%s %s PC", currentUser, result));
            recordPanel.add(resultLabel);
            
            String color = record.isWin ? "흑" : "백";
            JLabel scoreLabel = new JLabel(String.format("%s %d : %d %s", 
                color, record.playerScore, record.computerScore, color.equals("흑") ? "백" : "흑"));
            recordPanel.add(scoreLabel);
            
            recordPanel.add(Box.createVerticalStrut(10)); // 간격 추가
        }
        recordPanel.revalidate();
        recordPanel.repaint();
    }

    private void endGame() {
        int playerScore = 0;
        int computerScore = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    if (board[i][j].toString().equals("X")) {
                        playerScore++;
                    } else if (board[i][j].toString().equals("O")) {
                        computerScore++;
                    }
                }
            }
        }
        boolean isPlayerWin = playerScore > computerScore;
        
        game.addGameRecord(isPlayerWin, playerScore, computerScore);

        String message = String.format("게임 종료!\n플레이어: %d\n컴퓨터: %d\n%s 승리!", 
                                       playerScore, computerScore, isPlayerWin ? "플레이어" : "컴퓨터");
        int option = JOptionPane.showConfirmDialog(this, message + "\n다시 시작하시겠습니까?", "게임 종료", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }

    private void restartGame() {
        this.dispose();
        levelSelector.setVisible(true);
    }

    
	public void setBoard(Piece[][] board) {
		this.board = board;
	}

	private void updateScore() {
		int blackCount = 0;
		int whiteCount = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] != null) {
					if (board[i][j].toString().equals("X")) {
						blackCount++;
					} else if (board[i][j].toString().equals("O")) {
						whiteCount++;
					}
				}
			}
		}
		blackScoreLabel.setText("흑돌: " + blackCount);
		whiteScoreLabel.setText("백돌: " + whiteCount);
	}

	//버튼 클릭 이벤트를 처리.
	//클릭된 위치를 찾아 humanPart() 메서드를 호출하고, 성공 시 computerPart() 메서드를 호출.
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuButton) {
            popupMenu.show(menuButton, 0, menuButton.getHeight());
        }
        else if (e.getSource() == switchButton) {
            switchPanel();
        } else {
            for (int i = 0; i < buttons.length; i++) {
                for (int j = 0; j < buttons[i].length; j++) {
                    if (buttons[i][j] == e.getSource()) {
                        if (humanPart(i, j)) {
                            updateScore();
                            isBlackTurn = false; // 사람의 턴(흑돌) 끝
                            updateTurnLabel();
                            computerPart();
                        }
                    }
                }
            }
        }
    }

	//사용자의 턴을 처리.
	//게임 로직을 통해 움직임의 유효성을 확인하고 보드를 업데이트.
    public boolean humanPart(int i, int j) {
        boolean tf = false;
        if (this.game.humansTurn(i, j))
            tf = true;
        setBoard(game.getBoard());
        this.printBoardGUI();
        if (game.gameOver()) {
            endGame();
        }
        if (!game.humanCanMove() && !game.gameOver()) {
            System.out.println("You can't move! So it's the AI's turn now");
        }
        return tf;
    }

	//컴퓨터의 턴을 처리.
	//2초 지연 후 컴퓨터의 움직임을 실행하고 보드를 업데이트.
    public void computerPart() {
        Thread thread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                game.computersTurn();
                setBoard(game.getBoard());
                printBoardGUI();
                updateScore();
                isBlackTurn = true; // 컴퓨터의 턴(백돌) 끝
                updateTurnLabel();
                if (game.gameOver()) {
                    endGame();
                }
            }
        };
        thread.start();
    }

	//현재 게임 상태에 따라 UI를 업데이트.
	//각 버튼의 텍스트와 배경색을 설정.
    private void updateButtonColor(JButton button) {
        if (button.getText().equals("X"))
            button.setBackground(Color.BLACK);
        else if (button.getText().equals("O"))
            button.setBackground(Color.WHITE);
        else if (button.getText().equals("."))
            button.setBackground(new Color(0, 100, 0));
    }

    public void printBoardGUI() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != null)
                    buttons[i][j].setText(board[i][j].toString());
                else
                    buttons[i][j].setText(".");
                updateButtonColor(buttons[i][j]);
            }
        }
    }
}
