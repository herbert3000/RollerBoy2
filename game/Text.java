package game;

public class Text {

    public static String langSuffix = "";
    public static String loadingRollerBoy2 = "Loading RollerBoy 2";
    public static String pleaseWait = "Please wait...";
    public static String criticalProgramFault = "A critical program fault has occured:";
    public static String pleaseConsult = "Please direct your webbrowser to";
    public static String troubleShootingURL = "http://www.rollerboy2.com/trouble.html";
    public static String forHelp = "for help.";
    public static String game = "game";
    public static String magicWoods = "Magic Woods";
    public static String alienSpaceShip = "Alien Space Ship";
    public static String enterLevelName = "Enter level name:";
    public static String enterLevelPassword = "Enter level password:";
    public static String top30 = "TOP 30 Today:";
    public static String top100 = "TOP 100 All Time:";
    public static String rank = "Rank";
    public static String score = "Score";
    public static String name = "Name";
    public static String level = "Level";
    public static String rollerboyIsHaningOut = "RollerBoy is hanging out with his friends...";
    public static String whenSuddenly = "When suddenly...";
    public static String clickToSkipIntro = "Click to skip intro...";
    public static String version = "Version";
    public static String loadingHighscores = "Loading highscores...";
    public static String clickHereToStartNew = "Click here to start a new game";
    public static String clickHereToEnterPassword = "Click here to enter a password for a level";
    public static String oneSecond = "One second...";
    public static String passwordFor = "Password for";
    public static String clickToStart = "Click to start!";
    public static String loading = "Loading";
    public static String loadingSounds = "Loading sounds";
    public static String havingControlProblems = "Problem with the controls? Wait until all sounds have loaded.";
    public static String youFoundAll = "You found all";
    public static String coins = "coins!";
    public static String youFound = "You found";
    public static String coinsOnThisLevel = "coins on this level.";
    public static String gamePaused = "Game paused...";
    public static String choosePlayer = "Choose your player";
    public static String ok = "OK";
    public static String cancel = "Cancel";
    public static String fps = "FPS: ";
    private static String nastyWordList[] = {
        "COCK", "COCKS", "IN ASS", "YOUR ASS", "NIGGA", "NIGGER", "HOE", "SUCKS DICK", "SUCK DICK", "SUCK MY DICK", 
        "SUCKS MY DICK", "WHORE", "SLUT", "FUCK", "FUCKS", "FUCKER", "FUCKERS", "FUCKING", "FUCKINGS", "BITCH", 
        "PUSSY", "CUNT", "PENIS", "PENISES"
    };
    private static char skipChars[] = {
            '!', '?', '.', '-', ',', '_', '@', '\243', '\244', '%', 
            '&', '/', '(', ')', '=', '?', '+', '$', '"'
    };

    static boolean isNasty(String s) {
        s = s.toUpperCase();
        for (int i = 0; i < skipChars.length; i++)
            s = s.replace(skipChars[i], ' ');

        for (int j = 0; j < nastyWordList.length + LevelStepCounts.passwords.length; j++) {
            String s1;
            if (j < nastyWordList.length)
                s1 = nastyWordList[j];
            else
                s1 = LevelStepCounts.passwords[j - nastyWordList.length];
            for (int k = 0; k < s1.length(); k++)
                if (s.indexOf(" " + s1 + " ") != -1)
                    return true;

            if (s.indexOf(s1 + " ") == 0)
                return true;
            if (s.length() == s1.length() && s.equals(s1))
                return true;
            if (s.length() != s1.length() && s.indexOf(" " + s1) == s.length() - (s1.length() + 1))
                return true;
        }
        return false;
    }
 
    static String getVisualizedLevelNumber(int i) {
        if (i == 1)
            return Text.game;
        if (i == 7)
            return Text.magicWoods;
        if (i <= 6)
            return Text.level + " 1-" + i;
        if (i <= 16)
            return Text.level + " 2-" + (i - 6);
        if (i == 17)
            return Text.alienSpaceShip;
        else
            return "3-" + (i - 6 - 10);
    }

    public static String createLevelName(int i) {
        if (i <= 6)
            return "dt" + i;
        if (i <= 16)
            return "hw" + (i - 6);
        else
            return "uf" + (i - 16);
    }

    static int getLevelNumber(String s) {
        if (s.startsWith("dt")) {
            for (; !Character.isDigit(s.charAt(0)); s = s.substring(1));
            return Integer.parseInt(s);
        }
        if (s.startsWith("uf")) {
            for (; !Character.isDigit(s.charAt(0)); s = s.substring(1));
            return Integer.parseInt(s) + 6 + 10;
        }
        for (; !Character.isDigit(s.charAt(0)); s = s.substring(1));
        return Integer.parseInt(s) + 6;
    }

    static String getHiScoreLevelNumber(int i) {
        if (i == 1)
            return "1-1";
        if (i == 7)
            return "2-1";
        if (i <= 6)
            return "1-" + i;
        if (i <= 16)
            return "2-" + (i - 6);
        if (i == 17)
            return "3-1";
        else
            return "3-" + (i - 6 - 10);
    }
}
