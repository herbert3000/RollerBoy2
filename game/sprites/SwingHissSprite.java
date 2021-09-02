package game.sprites;

import game.*;
import java.awt.*;
import java.io.*;

public class SwingHissSprite extends Sprite {

    private float orgX;
    private float orgY;
    private float origoX;
    private float origoY;
    //private int currentFrame;
    private boolean firstTime;
    //private LinkSprite link;
    private static String loadedImageName;
    float s;
    float s2;
    float period;
    float amp;
    //private float time;

    public SwingHissSprite() {
        firstTime = true;
        s = 0.0F;
        s2 = 0.0F;
        period = 2.0F;
        amp = 4F;
        String s1 = "swinghiss" + getTileSetSuffix();
        if (!s1.equals(loadedImageName)) {
            loadedImageName = s1;
            getReferenceImage();
        }
        super.width = getReferenceImage().getWidth(null);
        super.height = getReferenceImage().getHeight(null);
        super.isSolid = true;
        super.canPassSolid = false;
    }

    @Override
	public void paint(Graphics g) {
        drawImage(g, getReferenceImage());
        g.setColor(Color.black);
        Point point = Sprite.world.getWorldWindow().getPos();
        int i = (int)((super.x + 15F) - point.x);
        int j = (int)(super.y - 53F - point.y);
        int k = (int)((origoX + super.width / 2) - point.x);
        int l = (int)((origoY + 16F) - point.y);
        g.drawLine(i, j, k, l);
        i = (int)((super.x + 78F) - point.x);
        j = (int)(super.y - 53F - point.y);
        k = (int)((origoX + super.width / 2) - point.x);
        l = (int)((origoY + 16F) - point.y);
        g.drawLine(i, j, k, l);
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/" + loadedImageName + "1.gif");
    }

    @Override
	public void load(DataInputStream datainputstream) throws IOException {
        super.load(datainputstream);
        s = datainputstream.readFloat();
        period = datainputstream.readFloat();
        amp = datainputstream.readFloat();
        s2 = datainputstream.readFloat();
    }

    @Override
	public Sprite getCopy() {
        SwingHissSprite swinghisssprite = new SwingHissSprite();
        swinghisssprite.freezable = super.freezable;
        swinghisssprite.s = s;
        swinghisssprite.s2 = s2;
        swinghisssprite.period = period;
        swinghisssprite.amp = amp;
        return swinghisssprite;
    }

    @Override
	public void initPos(float x, float y) {
        orgX = x;
        orgY = y;
        super.initPos(x, y);
    }

    @Override
	public void save(DataOutputStream dataoutputstream) throws IOException {
        super.save(dataoutputstream);
        dataoutputstream.writeFloat(s);
        dataoutputstream.writeFloat(period);
        dataoutputstream.writeFloat(amp);
        dataoutputstream.writeFloat(s2);
    }

    @Override
	public void updateSpriteLogic(double d) {
        if (firstTime) {
            firstTime = false;
            addSprite(/*link = */new LinkSprite(this, ImageManager.getImage("sprites/" + loadedImageName + "2.gif"), 0.0F, -53F));
        }
        //time += d;
        origoX = orgX;
        origoY = orgY - amp * 32F;
        float f = (float)(origoX + Math.sin(s) * amp * 32D);
        float f1 = (float)(origoY + Math.cos(s) * amp * 32D);
        s = (float)Math.sin(s2) * 0.65F;
        s2 += (6.2831853071795862D / period) * d;
        super.accx = (float)((f - super.x) / d - super.dx);
        super.accy = (float)((f1 - super.y) / d - super.dy);
    }
}
