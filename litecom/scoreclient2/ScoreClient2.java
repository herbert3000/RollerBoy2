package litecom.scoreclient2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Vector;

import game.HighscoreHandler;
import litecom.ErrorWindow;

public class ScoreClient2 {

    public static String serverName = "nph-scoreServer.cgi";
    public Vector<String> scores;
    private URL server;
    private String prop;
    private int limit;

    public ScoreClient2() { }
 
    public ScoreClient2(String s, String prop, int limit) throws Exception {
        server = new URL(s);
        this.prop = prop;
        this.limit = limit;
    }

    public ScoreClient2(URL server, String prop, int limit) throws Exception {
        this.server = server;
        this.prop = prop;
        this.limit = limit;
    }

	public void put(int score, String name) throws Exception {
        int checkSum = HighscoreHandler.createChecksum(name, score);
        URL url = new URL(server, serverName + "?prop=" + prop + "&action=put&score=" + score + "&data=" + URLEncoder.encode(name, "UTF-8") + "&checksum=" + checkSum);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
        padToStart(bufferedReader);
        String s1 = bufferedReader.readLine();
        if (!s1.startsWith("OK!")) {
            throw new Exception("ScoreServer2: " + s1);
        } else {
            update();
        }
    }

	private void padToStart(BufferedReader bufferedReader) throws Exception {
        String s;
        while ((s = bufferedReader.readLine()) != null) 
            if (s.startsWith("ScoreServer2"))
                break;
    }

	public void update() throws Exception {
        String s = serverName + "?prop=" + prop + "&action=get&unique=" + System.currentTimeMillis() + "&n=" + limit;
        URL url = new URL(server, s);
        URLConnection urlconnection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
        padToStart(bufferedReader);
        Vector<String> vector = new Vector<String>();
        while ((s = bufferedReader.readLine()) != null)  {
            if (s.startsWith("ERROR"))
                throw new Exception("ScoreServer2: " + s);
            vector.addElement(s);
        }
        scores = vector;
    }

    public int minScore() {
        if(scores.size() < limit)
            return 0;
        int i = -1;
        try {
            for (int j = 0; j < scores.size(); j++) {
                StringTokenizer stringtokenizer = new StringTokenizer("" + scores.elementAt(j), "\t");
                int k = Integer.parseInt(stringtokenizer.nextToken().trim());
                if (i == -1)
                    i = k;
                if (k < i)
                    i = k;
            }
        } catch(Exception exception) {
            new ErrorWindow(exception, "SC2: Could not parse scores.");
        }
        return i;
    }
}
