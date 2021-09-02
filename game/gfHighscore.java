package game;

public class gfHighscore {

    public int userId;
    public String userName;
    public int score;
    public String gameData;

    public gfHighscore() {
    	this.userId = 0;
        this.userName = new String("");
        this.score = 0;
        this.gameData = new String("");
    }

    public gfHighscore(int userId, String userName, int score, String gameData) {
        this.userId = userId;
        this.userName = new String(userName);
        this.score = score;
        this.gameData = new String(gameData);
    }
}
