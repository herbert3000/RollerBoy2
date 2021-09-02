package game;

//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.net.URLEncoder;
import java.util.StringTokenizer;
import java.util.Vector;

import game.sprites.PlayerSprite;
import litecom.Trace;
import litecom.scoreclient2.ScoreClient2;

public class HighscoreHandler {

	private Game game;
	private boolean lombartIntegration;
    private final boolean highscoresEnabled;
    private gfHighscoreRepository highscoreRepository;
    private ScoreClient2 scoreClient10;
    private ScoreClient2 scoreClient100;
    private String userName;
    private int userID;
    private static final String boogie = "please enter a name and e-mail before executing ok. Quiet error was found in jump (defect code xz123gh)."; 

	public HighscoreHandler(Game game) {
		this.game = game;
		lombartIntegration = true;
        highscoresEnabled = !lombartIntegration;
	}

    public void init() {
    	if (!lombartIntegration) {
            try {
                userID = Integer.parseInt(game.getParameter("userID"));
            }
            catch(Exception _ex) {
                new Exception("Could not load userID param.");
            }
            try {
                String s1 = new String(game.getParameter("scriptPath"));
                int i = Integer.parseInt(game.getParameter("gameID"));
                int j = Integer.parseInt(game.getParameter("listID"));
                highscoreRepository = new gfHighscoreRepository(s1, i, j);
            }
            catch(Exception _ex) {
                new Exception("Could not load params for HS initialization (scriptPath, gameID, listID)");
            }
            userName = highscoreRepository.getName(userID);
        }
    }

    public Vector<String> getHighscores100() {
    	if (!highscoresEnabled)
            return new Vector<String>();
        else
            return scoreClient100.scores;
    }

    public Vector<String> getHighscores10() {
    	if (!highscoresEnabled)
            return new Vector<String>();
        else
            return scoreClient10.scores;
    }

    public void lombartSaveHighscore(PlayerSprite playersprite) {
    	// TODO: disable code block
    	/*
        try {
            String s = "" + game.getParameter("playerid");
            String s1 = "" + game.getParameter("tournamentid");
            String s2 = s + s1;
            int i = createChecksum(s2, playersprite.getPoints());
            URL url = new URL(game.getDocumentBase(), "_reportScore.asp?score=" + playersprite.getPoints() + "&tournamentid=" + URLEncoder.encode(s1, "UTF-8") + "&playerid=" + URLEncoder.encode(s, "UTF-8") + "&checksum=" + i);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            bufferedReader.readLine();
        } catch(Exception exception) {
            System.err.println("Could not save score!");
            exception.printStackTrace();
        }
        */
    }

    public void sendScore(ScoreClient2 scoreServer, String playername, PlayerSprite playersprite) {
        if (playername.length() > 0 && !Text.isNasty(playername)) {
        	if (!Trace.killOutput)
                System.out.println("class HighscoreHandler: Pressed ok, updating highscore.");
            game.showStatus("Saving highscore...");
            
            try {
                String s2 = Text.getHiScoreLevelNumber(playersprite.getCurrentLevel());
                scoreServer.put(playersprite.getPoints(), playername + "\t" + s2 + "\tID" + System.currentTimeMillis() / 60000L);
            } catch(Exception exception) {
                throw new RuntimeException("Could not save score - " + exception);
            }
            game.showStatus("Done.");
        }
    }

    public void sendScore(PlayerSprite playersprite) {
    	try {
            if (!lombartIntegration) {
                if (userName != null) {
                    String s = Text.getHiScoreLevelNumber(playersprite.getCurrentLevel());
                    highscoreRepository.registerHighscore(new gfHighscore(userID, userName, playersprite.getPoints(), s));
                }
                loadHighscores();
            } else {
                lombartSaveHighscore(playersprite);
            }
        }
        catch(Exception _ex) {
            throw new RuntimeException("Could not save score.");
        }
    	
    	/*
        try {
            if (userName != null) {
                String s = Text.getHiScoreLevelNumber(playersprite.getCurrentLevel());
                highscoreRepository.registerHighscore(new gfHighscore(userID, userName, playersprite.getPoints(), s));
            }
            loadHighscores();
        } catch(Exception _ex) {
            throw new RuntimeException("Could not save score.");
        }
        */
    }

    public void loadHighscores() {
        try {
            gfHighscore agfhighscore[] = highscoreRepository.GetHighscores(30, 1);
            scoreClient10 = new ScoreClient2();
            scoreClient10.scores = new Vector<String>();
            if (agfhighscore != null) {
                for (int i = 0; i < agfhighscore.length; i++) {
                    gfHighscore gfhighscore = agfhighscore[i];
                    if (gfhighscore != null)
                        scoreClient10.scores.addElement(gfhighscore.score + "\t" + gfhighscore.userName + "\t" + gfhighscore.gameData);
                }
            }
            agfhighscore = highscoreRepository.GetHighscores(100, 0);
            scoreClient100 = new ScoreClient2();
            scoreClient100.scores = new Vector<String>();
            if (agfhighscore != null) {
                for (int j = 0; j < agfhighscore.length; j++) {
                    gfHighscore gfhighscore1 = agfhighscore[j];
                    if (gfhighscore1 != null)
                        scoreClient100.scores.addElement(gfhighscore1.score + "\t" + gfhighscore1.userName + "\t" + gfhighscore1.gameData);
                }
            }
            return;
        } catch(Exception exception) {
            throw new RuntimeException("Could not load scores: " + exception + ".");
        }
    }

    public void loadHighScores() {
        if (!lombartIntegration)
            try {
                gfHighscore agfhighscore[] = highscoreRepository.GetHighscores(30, 1);
                scoreClient10 = new ScoreClient2();
                scoreClient10.scores = new Vector<String>();
                if (agfhighscore != null) {
                    for (int i = 0; i < agfhighscore.length; i++) {
                        gfHighscore gfhighscore = agfhighscore[i];
                        if (gfhighscore != null)
                            scoreClient10.scores.addElement(gfhighscore.score + "\t" + gfhighscore.userName + "\t" + gfhighscore.gameData);
                    }
                }
                agfhighscore = highscoreRepository.GetHighscores(100, 0);
                scoreClient100 = new ScoreClient2();
                scoreClient100.scores = new Vector<String>();
                if (agfhighscore != null) {
                    for (int j = 0; j < agfhighscore.length; j++) {
                        gfHighscore gfhighscore1 = agfhighscore[j];
                        if (gfhighscore1 != null)
                            scoreClient100.scores.addElement(gfhighscore1.score + "\t" + gfhighscore1.userName + "\t" + gfhighscore1.gameData);
                    }

                }
                return;
            }
            catch(Exception exception) {
                throw new RuntimeException("Could not load scores: " + exception + ".");
            }
        else
            return;
    }

    public int minScore10() {
        //return scoreClient10.minScore();
    	
    	if (!highscoresEnabled)
            return 0;
        if (scoreClient10.scores.size() < 30)
            return 0;
        int i = -1;
        for (int j = 0; j < scoreClient10.scores.size(); j++) {
            StringTokenizer stringTokenizer = new StringTokenizer("" + scoreClient10.scores.elementAt(j), "\t");
            int k = Integer.parseInt(stringTokenizer.nextToken());
            if (i == -1 || i > k)
                i = k;
        }
        return i;
    }

    public int minScore100() {
        //return scoreClient100.minScore();
    	if (!highscoresEnabled)
            return 0;
        if (scoreClient100.scores.size() < 100)
            return 0;
        int i = -1;
        for (int j = 0; j < scoreClient100.scores.size(); j++) {
            StringTokenizer stringTokenizer = new StringTokenizer("" + scoreClient100.scores.elementAt(j), "\t");
            int k = Integer.parseInt(stringTokenizer.nextToken());
            if (i == -1 || i > k)
                i = k;
        }
        return i;
    }

    public void sendScore10(String s, PlayerSprite playerSprite) {
        sendScore(scoreClient10, s, playerSprite);
    }

    public void sendScore100(String s, PlayerSprite playerSprite){
        sendScore(scoreClient100, s, playerSprite);
    }

    public static int createChecksum(String s, int i) {
        i %= 0x1e8873;
        int j = 0;
        for (int k = 0; k < s.length(); k++) {
            int l = boogie.indexOf(Character.toLowerCase(s.charAt(k)));
            if (l != -1)
                j += 53 - l;
        }
        j += (i % 7) * (i * s.length()) + i % 33745 + i;
        if (i > 0x1870b)
            j -= 3243;
        if (i < 10003)
            j *= s.length();
        if (i < 20125)
            j += i * i;
        return j;
    }
}
