package othello2;

import java.awt.*;
import java.awt.event.*;
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
	private boolean isBlackTurn = true; // 게임 시작 시 흑돌 턴
	private JButton menuButton;
	private JPopupMenu popupMenu;

	//게임 보드의 UI를 초기화.
	//8x8 그리드 레이아웃으로 버튼을 배치.
	//각 버튼에 액션 리스너를 추가하고 초기 상태에 따라 색상을 설정.
	public GUI(int level) {
		game = new OthelloGame(level);
		this.setBoard(game.getBoard());
		initUI();
		this.setVisible(true);
	}

	public GUI(Piece[][] board) {
		this.setBoard(board);
		initUI();
		this.setVisible(true);
	}

	public void initUI() {
		//게임 보드를 표시할 패널
		panel = new JPanel();

		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.setLayout(new GridLayout(8, 8, 5, 5));

		//게임 보드의 각 칸을 나타내는 버튼 배열
		this.buttons = new JButton[8][8];

		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board[i].length; j++) {
				if (this.board[i][j] != null) {
					this.buttons[i][j] = new JButton(
							this.board[i][j].toString());
					panel.add(this.buttons[i][j]);
				} else {
					this.buttons[i][j] = new JButton(".");
					panel.add(this.buttons[i][j]);
				}
				buttons[i][j].addActionListener(this);
				if (buttons[i][j].getText().equals("X"))
					buttons[i][j].setBackground(Color.BLACK);
				else if (buttons[i][j].getText().equals("O"))
					buttons[i][j].setBackground(Color.WHITE);
				else if (buttons[i][j].getText().equals("."))
					buttons[i][j].setBackground(new Color(0, 100, 0));
			}
			panel.setVisible(true);
		}

		add(panel);

		setTitle("othello1.Othello");
		setSize(800, 800);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
				showGameResult("게임을 포기했습니다..");
			}
		});
		popupMenu.add(giveUpItem);

		toolBar.add(Box.createHorizontalGlue()); // 오른쪽 정렬을 위한 공간
		toolBar.add(menuButton);

		// JToolBar를 프레임의 북쪽에 추가
		add(toolBar, BorderLayout.NORTH);
	}



	private void updateTurnLabel() {
		if (isBlackTurn) {
			turnLabel.setText("흑돌 턴");
		} else {
			turnLabel.setText("백돌 턴");
		}
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

	private void restartGame() {
		// 현재 GUI 창 닫기
		this.dispose();

		// 새로운 LevelSelector 창 열기
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new LevelSelector().setVisible(true);
			}
		});
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
			System.out.println("The game has ended");
			System.out.println(game.determineWinner());
			String winner = game.determineWinner();
			if (winner.equals("X")) {
				showGameResult("축하합니다! 당신이 이겼습니다!");
			} else if (winner.equals("O")) {
				showGameResult("아쉽네요. 컴퓨터가 이겼습니다.");
			} else {
				showGameResult("무승부입니다!");
			}
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
			}
		};
		thread.start();
		if (game.gameOver()) {
			System.out.println("The game has ended. ");
			System.out.println(game.determineWinner());
			String winner = game.determineWinner();
			if (winner.equals("X")) {
				showGameResult("축하합니다! 당신이 이겼습니다!");
			} else if (winner.equals("O")) {
				showGameResult("아쉽네요. 컴퓨터가 이겼습니다.");
			} else {
				showGameResult("무승부입니다!");
			}
		}

	}

	//현재 게임 상태에 따라 UI를 업데이트.
	//각 버튼의 텍스트와 배경색을 설정.
	public void printBoardGUI() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] != null)
					buttons[i][j].setText(board[i][j].toString());
				if (buttons[i][j].getText().equals("X"))
					buttons[i][j].setBackground(Color.BLACK);
				else if (buttons[i][j].getText().equals("O"))
					buttons[i][j].setBackground(Color.WHITE);
			}
		}
	}

	private void showGameResult(String result) {
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
		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));

		JLabel resultLabel = new JLabel(result);
		JLabel countLabel = new JLabel("흑돌: " + blackCount + ", 백돌: " + whiteCount);
		JLabel timeLabel = new JLabel("게임 시간: " + clockComponent.getTimeString());

		resultLabel.setFont(new Font("SanSerif", Font.BOLD, 48));
		resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		countLabel.setFont(new Font("SanSerif", Font.BOLD, 48));
		countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		timeLabel.setFont(new Font("SanSerif", Font.BOLD, 60));
		timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton newGameButton = new JButton("새 게임");
		newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		newGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				new LevelSelector().setVisible(true);
			}
		});

		resultPanel.add(Box.createVerticalGlue());
		resultPanel.add(resultLabel);

		resultPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		resultPanel.add(countLabel);

		resultPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		resultPanel.add(timeLabel);

		resultPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		resultPanel.add(newGameButton);
		resultPanel.add(Box.createVerticalGlue());

		getContentPane().removeAll();
		getContentPane().add(resultPanel);
		getContentPane().revalidate();
		getContentPane().repaint();
	}
}
