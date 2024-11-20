package othello2;

public class GameRecord {
    int level;
    boolean isWin;
    int playerScore;
    int computerScore;

    public GameRecord(int level, boolean isWin, int playerScore, int computerScore) {
        this.level = level;
        this.isWin = isWin;
        this.playerScore = playerScore;
        this.computerScore = computerScore;
    }

    @Override
    public String toString() {
        return String.format("난이도: %d, 결과: %s, 점수: %d-%d", 
                             level, isWin ? "승리" : "패배", playerScore, computerScore);
    }
}