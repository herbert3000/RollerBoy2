package game;

import java.io.*;
import java.net.*;

public class gfHighscoreRepository {

    private int gameId;
    private int listId;
    private String scriptPath;
    private URL url;
    private URLConnection connection;
    private DataInputStream stream;
    private static final String boogie = "please enter a name and e-mail before executing ok. Quiet error was found in jump (defect code xz123gh).";

    public gfHighscoreRepository(String scriptPath, int gameId, int listId) {
        url = null;
        connection = null;
        stream = null;
        this.scriptPath = new String(scriptPath);
        this.gameId = gameId;
        this.listId = listId;
    }

    public gfHighscore[] GetHighscores(int numScores, int maxAge) {
        try {
            url = new URL(scriptPath + "/GetHighScores.asp?GameID=" + gameId + "&ListID=" + listId + "&NumScores=" + numScores + "&MaxAge=" + maxAge);
        } catch(MalformedURLException _ex) {
            return null;
        }
        try {
            connection = url.openConnection();
            connection.connect();
            stream = new DataInputStream(connection.getInputStream());
        } catch(IOException _ex) {
            return null;
        }
        byte abyte0[] = new byte[0x10000];
        int k = 0;
        do {
            try {
                int l = stream.read(abyte0, k, 0x10000);
                if (l == -1)
                    break;
                k += l;
                continue;
            }
            catch(IOException _ex) { }
            break;
        } while(true);
        try {
            stream.close();
        } catch(IOException _ex) { }
        int i1 = 0;
        int j1 = (new Integer(readLine(abyte0, i1))).intValue();
        i1 = nextLine(abyte0, i1);
        gfHighscore agfhighscore[] = new gfHighscore[j1];
        for (int k1 = 0; k1 < j1; k1++) {
            agfhighscore[k1] = new gfHighscore();
            agfhighscore[k1].userId = (new Integer(readLine(abyte0, i1))).intValue();
            i1 = nextLine(abyte0, i1);
            agfhighscore[k1].userName = readLine(abyte0, i1);
            i1 = nextLine(abyte0, i1);
            agfhighscore[k1].score = (new Integer(readLine(abyte0, i1))).intValue();
            i1 = nextLine(abyte0, i1);
            agfhighscore[k1].gameData = readLine(abyte0, i1);
            i1 = nextLine(abyte0, i1);
        }
        return agfhighscore;
    }

    private String readLine(byte abyte0[], int i) {
        int j;
        int k = j = i;
        for (; abyte0[j] != 10 && abyte0[j] != 13; j++);
        if (j > k) {
            byte abyte1[] = new byte[j - k];
            for (int l = k; l < j; l++)
                abyte1[l - k] = abyte0[l];

            return new String(abyte1);
        } else {
            return new String("");
        }
    }

    private int createChecksum(String s, int i) {
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

    public boolean registerHighscore(gfHighscore gfhighscore) {
        if (gfhighscore.userId < 0)
            return false;
        try {
            url = new URL(scriptPath + "/RegisterHighscore.asp?GameID=" + gameId + "&ListID=" + listId + "&UserID=" + gfhighscore.userId + "&Score=" + gfhighscore.score + "&GameData=" + URLEncodeString(gfhighscore.gameData) + "&Checksum=" + createChecksum(gfhighscore.userName, gfhighscore.score));
        } catch(MalformedURLException _ex) {
            return false;
        }
        try {
            connection = url.openConnection();
            connection.connect();
            stream = new DataInputStream(connection.getInputStream());
        } catch(IOException _ex) {
            return false;
        }
        byte abyte0[] = new byte[10000];
        int i = 0;
        do {
            try {
                int j = stream.read(abyte0, i, 10000);
                if (j == -1)
                    break;
                i += j;
                continue;
            } catch(IOException _ex) { }
            break;
        } while(true);
        try {
            stream.close();
        } catch(IOException _ex) { }
        return true;
    }

    public String getName(int i) {
        if (i < 0)
            return null;
        try {
            url = new URL(scriptPath + "/GetName.asp?ID=" + i);
        } catch(MalformedURLException _ex) {
            return null;
        }
        try {
            connection = url.openConnection();
            connection.connect();
            stream = new DataInputStream(connection.getInputStream());
        } catch(IOException _ex) {
            return null;
        }
        byte abyte0[] = new byte[10000];
        int j = 0;
        do {
            try {
                int k = stream.read(abyte0, j, 10000);
                if (k == -1)
                    break;
                j += k;
                continue;
            } catch(IOException _ex) { }
            break;
        } while(true);
        try {
            stream.close();
        } catch(IOException _ex) { }
        byte abyte1[] = new byte[j];
        for (int l = 0; l < j; l++)
            abyte1[l] = abyte0[l];
        
        return new String(abyte1);
    }

    private int nextLine(byte abyte0[], int i) {
        int j;
        for (j = i; abyte0[j] != 10 && abyte0[j] != 13; j++);
        if (abyte0[j] == 13)
            j++;
        if (abyte0[j] == 10)
            j++;
        return j;
    }

    private String URLEncodeString(String s) {
        String s1 = new String("");
        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) == ' ')
                s1 = s1 + "%20";
            else
                s1 = s1 + s.charAt(i);

        return s1;
    }
}
