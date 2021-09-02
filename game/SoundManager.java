package game;

import game.sprites.PlayerSprite;
//import java.applet.Applet;
import java.applet.AudioClip;
import java.util.Hashtable;
import java.util.Vector;
import litecom.Trace;

public class SoundManager implements Runnable {

    public static boolean soundEnabled;
    private Game game;
    private Hashtable<String, AudioClip> sounds;
    private Vector<String> loadQue;
    private int soundListIndex;
    private static SoundManager ref;

    public SoundManager() {
        sounds = new Hashtable<String, AudioClip>();
        loadQue = new Vector<String>();
        soundListIndex = 0;
    }

    public SoundManager(Game game) {
        sounds = new Hashtable<String, AudioClip>();
        loadQue = new Vector<String>();
        soundListIndex = 0;
        if (game.getParameter("sound") != null)
            soundEnabled = true;
        else
            soundEnabled = false;
        this.game = game;
        if (soundEnabled) {
            Thread thread = new Thread(this);
            thread.setPriority(1);
            thread.start();
        }
    }

    private boolean queContains(String s) {
        for (int i = 0; i < loadQue.size(); i++) {
            String s1 = loadQue.elementAt(i).toString();
            if (s1.equals(s))
                return true;
        }
        return false;
    }

    public void play(float f, float f1, String soundName) {
        if (soundEnabled) {
            PlayerSprite playersprite = PlayerSprite.getReference();
            float f2 = ((Sprite) (playersprite)).x - f;
            float f3 = ((Sprite) (playersprite)).y - f1;
            if (f2 * f2 + f3 * f3 < Game.xlen * Game.xlen) {
                play(soundName);
            }
        }
    }

    public void play(String soundName) {
        if (!soundEnabled) return;
        
        AudioClip audioclip = sounds.get(soundName);
        if (audioclip == null) {
            if (!queContains(soundName)) {
                Trace.out(this, "adding sound to que: " + soundName);
                loadQue.addElement(soundName);
                synchronized(this) {
                    notifyAll();
                }
                return;
            }
        } else {
            Trace.out(this, "playing clip: " + soundName);
            audioclip.play();
        }
    }

    public float getProgress() {
        if (!soundEnabled)
            return 1.0F;
        else
            return (float)soundListIndex / (float)(SoundList.soundList.length - 1);
    }

    public Game getGame() {
        return game;
    }

    @Override
	public void run() {
        do {
            do {
                while (loadQue.size() > 0) {
                    String s = loadQue.elementAt(loadQue.size() - 1);
                    Trace.out(this, "loading sound: " + s);
                    AudioClip audioclip = game.getAudioClip(Game.documentBase, "sounds/" + s + ".au");
                    audioclip.play();
                    audioclip.stop();
                    sounds.put(s, audioclip);
                    Trace.out(this, "sound loaded");
                    loadQue.removeElement(s);
                }
                synchronized(this) {
                    try {
                        if (soundListIndex < SoundList.soundList.length)
                            wait(100L);
                        else
                            wait();
                    } catch(InterruptedException _ex) { }
                }
            } while (soundListIndex >= SoundList.soundList.length || loadQue.size() != 0);
            String s1 = SoundList.soundList[soundListIndex];
            soundListIndex++;
            s1 = s1.substring(0, s1.lastIndexOf('.'));
            Trace.out(this, "Precaching: " + s1);
            loadQue.addElement(s1);
        } while(true);
    }

    public void enableSound() {
        if (!soundEnabled) {
            soundEnabled = true;
            Thread thread = new Thread(this);
            thread.setPriority(1);
            thread.start();
        }
    }

    public static SoundManager ref(Game game) {
        if (ref == null)
            ref = new SoundManager(game);
        else if (ref.getGame() != game)
            ref = new SoundManager(game);
        return ref;
    }
}
