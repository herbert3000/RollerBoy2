package game;

import game.sprites.ButtonSprite;
import java.awt.*;
import java.io.*;
import java.util.Hashtable;
import litecom.gfxe.Loader;
import litecom.gfxe.ImageSplitter;
import litecom.Trace;

public abstract class Sprite {

	public float x;
    public float y;
    public float accx;
    public float accy;
    public float dx;
    public float dy;
    public float oldDx;
    public float oldDy;
    public int width;
    public int height;
    public int layer;
    public boolean isSolid;
    public boolean canPassSolid;
    public boolean isCollidable;
    public boolean collLeft;
    public boolean collRight;
    public boolean collUp;
    public boolean collDown;
    public boolean frozen;
    protected boolean useHQmove;
    protected float HQhorizDamping;
    protected float HQhorizAccel;
    protected float HQhorizMaxVel;
    protected float HQhorizPush;
    protected float HQvertDamping;
    protected float HQvertAccel;
    protected float HQvertMaxVel;
    protected float HQvertPush;
    protected final int HQstepsPerSecond = 500;
    protected static World world;
    protected static Level level;
    protected static SpriteManager spriteManager;
    protected boolean jumpKeyDown;
    private long subsampleStepCount;
    private long jumpStartedAt;
    protected Sprite elevator;
    protected int elevatorPos;
    private boolean stamped;
    //private final boolean doTrace = false;
    private boolean killMeNextFrame;
    protected boolean freezable;
    protected static Loader loader;
    protected static boolean isNewTileSet = true;
    private float dyAtJumpStart;
    private CollisionResultSet crsxInstance;
    private CollisionResultSet crsyInstance;
    private float HQstepFraction;
    private float subDx;
    private float subDy;
    //private float subX;
    //private float subY;
    private int steps;
    private double frameTime;
    private static Hashtable<String, Integer> progressPerSpriteClass = new Hashtable<String, Integer>();
    float blah;

    public Sprite() {
        layer = 0;
        isSolid = false;
        canPassSolid = true;
        isCollidable = true;
        frozen = false;
        HQhorizDamping = 0.992F;
        HQhorizAccel = 0.0F;
        HQhorizMaxVel = 123400F;
        HQvertDamping = 0.995F;
        HQvertAccel = 0.003F;
        HQvertMaxVel = 123400F;
        jumpStartedAt = -1L;
        stamped = true;
        killMeNextFrame = false;
        freezable = true;
        crsxInstance = new CollisionResultSet();
        crsyInstance = new CollisionResultSet();
    }

    void moveSubStep(float f, float f1) {
        Level level1 = world.getLevel();
        CollisionResultSet collisionResultSet = null;
        CollisionResultSet collisionResultSet1 = null;
        if (collisionResultSet == null)
            if (f > 0.0F) {
                Object obj = level1.getVertLineCollision(((x + width) - 1.0F) + f, y, height);
                if (obj != null && (!(obj instanceof Tile) || !((Tile)obj).flag2))
                    collisionResultSet = crsxInstance.set(obj, 1, 0, level1.getLastCollidedBlock());
            } else if (f < 0.0F) {
                Object obj1 = level1.getVertLineCollision(x + f, y, height);
                if (obj1 != null && (!(obj1 instanceof Tile) || !((Tile)obj1).flag2))
                    collisionResultSet = crsxInstance.set(obj1, -1, 0, level1.getLastCollidedBlock());
            }
        if (collisionResultSet1 == null)
            if (f1 > 0.0F) {
                Object obj2 = level1.getHorizLineCollision(x, ((y + height) - 1.0F) + f1, width);
                if (obj2 != null && (!(obj2 instanceof Tile) || !((Tile)obj2).flag2 || (((y + height) - 1.0F) + f1) % 32F <= 2.0F))
                    collisionResultSet1 = crsyInstance.set(obj2, 0, 1, level1.getLastCollidedBlock());
            } else if (f1 < 0.0F) {
                Object obj3 = level1.getHorizLineCollision(x, y + f1, width);
                if (obj3 != null && (!(obj3 instanceof Tile) || !((Tile)obj3).flag2))
                    collisionResultSet1 = crsyInstance.set(obj3, 0, -1, level1.getLastCollidedBlock());
            }
        if (canPass(collisionResultSet))
            x += f;
        if (canPass(collisionResultSet1))
            y += f1;
        if (collisionResultSet1 != null)
            handleCollision(collisionResultSet1);
        if (collisionResultSet != null)
            handleCollision(collisionResultSet);
    }

    public boolean isNewTileSet() {
        return isNewTileSet;
    }

    public boolean isPlayer() {
        return false;
    }

    protected final Image[] getFrames(String s, int l, int i1) {
        int j1 = loader == null ? 0 : loader.getProgress();
        Image aimage[] = (new ImageSplitter(Game.getReference(), "gfx/sprites/" + s, l, i1, loader)).getImages();
        if (loader != null) {
            String s1 = getClass().getName();
            progressPerSpriteClass.put(s1, new Integer(loader.getProgress() - j1));
        }
        return aimage;
    }

    private void solidCollision(Object obj, float f, float f1) {
        Sprite sprite = null;
        if (obj instanceof Sprite) {
            sprite = (Sprite)obj;
            if (f1 == -1F) {
                subDy = 0.0F;
                dy = 0.0F;
            }
        } else {
            float f3;
            float f2 = f3 = 0.0F;
            if (f1 != 0.0F) {
                if (useHQmove)
                    subDy = f3 / 500F;
                else
                    subDy = (float)((f3 * frameTime) / steps);
                dy = 0.0F;
            }
            if (f != 0.0F) {
                if (useHQmove)
                    subDx = f2 / 500F;
                else
                    subDx = (float)((f2 * frameTime) / steps);
                dx = 0.0F;
            }
        }
        setCollFlags(sprite, f, f1);
    }

    public void spriteCollision(Sprite sprite, float f, float f1) { }

    private void checkElevatorAttach(Sprite sprite, int i, int j) {
        if (isSolid && !sprite.isSolid && !sprite.canPassSolid) {
            if (sprite.getElevator() == null && !(sprite instanceof TotallyPassable)) {
                byte byte0 = 0;
                if (j == 1)
                    byte0 = 0;
                else if (i == 1)
                    byte0 = 2;
                else if (j == -1)
                    byte0 = 1;
                else if (i == -1)
                    byte0 = 3;
                sprite.setElevator(this, byte0);
                return;
            }
        } else if (!isSolid && sprite.isSolid && !canPassSolid && getElevator() == null && !(this instanceof TotallyPassable)) {
            byte byte1 = 0;
            if (j == 1)
                byte1 = 1;
            else if (i == 1)
                byte1 = 3;
            else if (j == -1)
                byte1 = 0;
            else if (i == -1)
                byte1 = 2;
            setElevator(sprite, byte1);
        }
    }

    protected void addSprite(Sprite sprite) {
        spriteManager.addSprite(sprite);
    }

    public void update(double d) {
        blah = (float)d;
        if (isMovable()) {
            unstamp();
            if (canPassSolid && !isSolid) {
                x += dx * d;
                y += dy * d;
            } else {
                moveSubsampled(d);
            }
            checkDetachElevator();
            stamp();
        }
    }

    private void moveSubsampled(double d) {
        frameTime = d;
        if (elevator != null) {
            float x2 = x;
            float y2 = y;
            if (elevatorPos == 2) {
                x2 = elevator.x + elevator.width;
                dx = elevator.dx;
                collLeft = true;
            } else if (elevatorPos == 3) {
                x2 = elevator.x - width;
                dx = elevator.dx;
                collRight = true;
            } else if (elevatorPos == 0) {
                y2 = elevator.y + elevator.height;
                dy = elevator.dy;
                collUp = true;
            } else if (elevatorPos == 1) {
                y2 = elevator.y - height;
                dy = elevator.dy;
                collDown = true;
            } if (elevatorPos == 2 || elevatorPos == 3) {
                float f = x2 - x;
                if (f >= 32F)
                    f = 31F;
                if (f <= -32F)
                    f = -31F;
                moveSubStep(f, 0.0F);
            }
            if (elevatorPos == 0) {
                float f = y2 - y;
                if (f >= 32F)
                    f = 31F;
                if (f <= -32F)
                    f = -31F;
                moveSubStep(0.0F, f);
            }
            if (elevatorPos == 1) {
                float f = y2 - y;
                if (f >= 32F)
                    f = 31F;
                if (f <= -32F)
                    f = -31F;
                moveSubStep((float)(elevator.dx * d), f);
            }
        }
        if (useHQmove) {
            float f = (float)(d * 500D);
            steps = (int)f;
            HQstepFraction += f - steps;
            if (HQstepFraction >= 1.0F) {
                HQstepFraction--;
                steps++;
            }
            subDx = dx / 500F;
            subDy = dy / 500F;
        } else {
            steps = (int)(Math.abs(dx) <= Math.abs(dy) ? Math.abs(dy) * 1.42D * d : Math.abs(dx) * 1.42D * d);
            if (steps < 1)
                steps = 1;
            subDx = (float)((dx * d) / steps);
            subDy = (float)((dy * d) / steps);
        }
        if (collDown)
            dyAtJumpStart = dy;
        float f = (float)((dyAtJumpStart * d) / steps);
        for (int i = 0; i < steps; i++) {
            moveSubStep(subDx, subDy);
            if (useHQmove) {
                if (HQhorizPush != 0.0F) {
                    subDx = HQhorizPush;
                    HQhorizPush = 0.0F;
                }
                if (HQvertPush != 0.0F) {
                    subDy = HQvertPush;
                    HQvertPush = 0.0F;
                }
                if ((HQhorizAccel <= 0.0F || !collRight) && (HQhorizAccel >= 0.0F || !collLeft))
                    subDx += HQhorizAccel;
                if (jumpStartedAt != -1L && jumpKeyDown && !collUp) {
                    if (subsampleStepCount - jumpStartedAt < 105L)
                        subDy = f - 0.73F;
                } else {
                    jumpStartedAt = -1L;
                }
                if ((HQvertAccel <= 0.0F || !collDown) && (HQvertAccel >= 0.0F || !collUp))
                    subDy += HQvertAccel;
                subDx *= HQhorizDamping;
                subDy *= HQvertDamping;
                if (Math.abs(subDx) > HQhorizMaxVel)
                    subDx = HQhorizMaxVel;
                if (Math.abs(subDy) > HQvertMaxVel)
                    subDy = HQvertMaxVel;
            }
            subsampleStepCount++;
        }

        if (useHQmove) {
            dx = subDx * 500F;
            dy = subDy * 500F;
            accx = (float)(HQhorizAccel * 500F * 500F * d);
            accy = (float)(HQvertAccel * 500F * 500F * d);
        }
    }

    public void load(DataInputStream dataInputStream) throws IOException {
        float x = dataInputStream.readFloat();
        float y = dataInputStream.readFloat();
        initPos(x, y);
        layer = dataInputStream.readInt();
        freezable = dataInputStream.readBoolean();
    }

    public boolean iCantClipMyslef() {
        return true;
    }

    public Sprite getElevator() {
        return elevator;
    }

    public void setElevator(Sprite sprite, int pos) {
        elevator = sprite;
        elevatorPos = pos;
    }

    private void checkDetachElevator() {
        if (elevator == null)
            return;
        Sprite sprite = elevator;
        boolean flag = false;
        switch(elevatorPos) {
        case 2:
        case 3:
            if (sprite.y > (y + height) - 1.0F)
                flag = true;
            if ((sprite.y + sprite.height) - 1.0F < y)
                flag = true;
            break;

        case 0:
        case 1:
            if (sprite.x > (x + width) - 1.0F)
                flag = true;
            if ((sprite.x + sprite.width) - 1.0F < x)
                flag = true;
            break;
        }
        if (elevator.killMeNextFrame)
            flag = true;
        if (!flag)
            switch(elevatorPos) {
            default:
                break;

            case 2:
                if (sprite.dx < dx)
                    flag = true;
                break;

            case 3:
                if (sprite.dx > dx)
                    flag = true;
                break;

            case 0:
                flag = true;
                break;

            case 1:
                if (jumpStartedAt != -1L) {
                    flag = true;
                    dx = dx + elevator.dx;
                }
                break;
            }
        if (flag) {
            elevator = null;
            elevatorPos = -1;
        }
    }

    public void save(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeFloat(x);
        dataOutputStream.writeFloat(y);
        dataOutputStream.writeInt(layer);
        dataOutputStream.writeBoolean(freezable);
    }

    public static void setSpriteManager(SpriteManager sm) {
        spriteManager = sm;
    }

    public String getName() {
        String s = getClass().getName();
        s = s.substring(s.lastIndexOf('.') + 1);
        s = s.substring(0, s.length() - "Sprite".length());
        return s;
    }

    public Dimension getSize() {
        return new Dimension(width, height);
    }

    protected final void drawImage(Graphics g, Image image) {
        drawImage(g, image, x, y, image.getWidth(null), image.getHeight(null));
    }

    protected final void drawImage(Graphics g, Image image, float x, float y) {
        drawImage(g, image, x, y, image.getWidth(null), image.getHeight(null));
    }

    protected final void drawImage(Graphics g, Image image, int height, int width) {
        drawImage(g, image, x, y, width, height);
    }

    protected final void drawImage(Graphics g, Image image, float x, float y, int width, int height){
        if (elevator != null)
            switch(elevatorPos) {
            case 0:
                y = elevator.y + elevator.height;
                break;

            case 1:
                y = elevator.y - height;
                break;

            case 2:
                x = elevator.x + elevator.width;
                break;

            case 3:
                x = elevator.x - width;
                break;
            }
        Point point = world.getWorldWindow().getPos();
        int x2 = (int)(x - point.x);
        int y2 = (int)(y - point.y);
        if (x2 + width < 0)
            return;
        if (x2 >= Game.xlen)
            return;
        if (y2 + height < 0)
            return;
        if (y2 >= Game.ylen)
            return;
        g.drawImage(image, x2, y2, width, height, null);
    }

    public static void setLevel(Level level1) {
        if (level == null)
            isNewTileSet = true;
        else if (level.getTileSet() == null || level1.getTileSet() == null)
            isNewTileSet = true;
        else
            isNewTileSet = !level.getTileSet().getName().equals(level1.getTileSet().getName());
        
        level = level1;
    }

    public boolean isFreezable() {
        return freezable;
    }

    public static void setWorld(World world1) {
        world = world1;
    }

    protected void kill() {
        killMeNextFrame = true;
    }

    public void stamp() {
        stamp(-1);
    }

    public void stamp(int l) {
        if (stamped) return;
        
        spriteManager.stampSprite(this);
        stamped = true;
    }

    private void setCollFlags(Sprite sprite, float f, float f1) {
        if (f1 == 1.0F) {
            collDown = true;
            if (sprite != null) {
                sprite.collUp = true;
                return;
            }
        } else if (f == 1.0F) {
            collRight = true;
            if (sprite != null) {
                sprite.collLeft = true;
                return;
            }
        } else if (f1 == -1F) {
            collUp = true;
            if (sprite != null) {
                sprite.collDown = true;
                return;
            }
        } else if (f == -1F) {
            collLeft = true;
            if (sprite != null)
                sprite.collRight = true;
        }
    }

    public boolean isKilled() {
        return killMeNextFrame;
    }

    @Override
	public String toString() {
        String s = getClass().getName();
        s = s.substring(s.lastIndexOf('.') + 1);
        return s + "(" + x + "(" + (int)(x / 32F) + "), " + y + "(" + (int)(y / 32F) + "))";
    }

    public abstract void paint(Graphics g);

    public String getTileSetSuffix() {
        if (level == null)
            return "";
        if (level.getTileSet() == null)
            return "";
        if ("downtown".equals(level.getTileSet().getName()))
            return "-dt";
        if ("ufo".equals(level.getTileSet().getName()))
            return "-ufo";
        else
            return "";
    }

    public boolean isMovable() {
        return true;
    }

    public void printDebugLayerStuff() {
        if (dx != 0.0F || dy != 0.0F)
            DebugLayer.add(DebugLayerEntry.Type.TYPE_ARROW, Color.white, x + width / 2, y + height / 2, x + width / 2 + dx / 5F, y + height / 2 + dy / 5F);
        if (collLeft)
            DebugLayer.add(DebugLayerEntry.Type.TYPE_LINE, Color.blue, x, y, x, (y + height) - 1.0F);
        if (collRight)
            DebugLayer.add(DebugLayerEntry.Type.TYPE_LINE, Color.blue, (x + width) - 1.0F, y, (x + width) - 1.0F, (y + height) - 1.0F);
        if (collUp)
            DebugLayer.add(DebugLayerEntry.Type.TYPE_LINE, Color.blue, x, y, (x + width) - 1.0F, y);
        if (collDown)
            DebugLayer.add(DebugLayerEntry.Type.TYPE_LINE, Color.blue, x, (y + height) - 1.0F, (x + width) - 1.0F, (y + height) - 1.0F);
        if (elevator != null) {
            Sprite sprite = elevator;
            DebugLayer.add(DebugLayerEntry.Type.TYPE_ARROW, Color.orange, x + width / 2, y + height / 2, sprite.x + sprite.width / 2, sprite.y + sprite.height / 2);
        }
    }

    public void buttonCheck() {
        if (elevator != null && elevatorPos == 1 && (elevator instanceof ButtonSprite))
            ((ButtonSprite)elevator).somethingOnButton();
    }

    public abstract Image getReferenceImage();

    public boolean isEnemy() {
        return false;
    }

    public abstract Sprite getCopy();

    public void initPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    private boolean canPass(CollisionResultSet collisionResultSet) {
        if (collisionResultSet == null)
            return true;
        return collisionResultSet.obj instanceof TotallyPassable;
    }

    protected void initJump() {
        if (jumpStartedAt == -1L) {
            SoundManager.ref(Game.getReference()).play("player-jump");
            jumpStartedAt = subsampleStepCount;
            dyAtJumpStart = dy;
        }
    }

    public void unstamp() {
        unstamp(-1);
    }

    public void setFreeze(boolean frozen) {
        this.frozen = frozen;
    }

    public void unstamp(int i) {
        if (!stamped)
            return;
        
        spriteManager.unstampSprite(this);
        stamped = false;
    }

    public static void setLoader(Loader loader1) {
        loader = loader1;
    }

    public void checkColl(String s) {
        Level level = world.getLevel();
        Object obj = level.getHorizLineCollision(x, y, width);
        if (obj != null)
            throw new RuntimeException(s + obj);
        obj = level.getHorizLineCollision(x, (y + height) - 1.0F, width);
        if (obj != null)
            throw new RuntimeException(s + obj);
        obj = level.getVertLineCollision(x, y, height);
        if (obj != null)
            throw new RuntimeException(s + obj);
        obj = level.getVertLineCollision((x + width) - 1.0F, y, height);
        if (obj != null)
            throw new RuntimeException(s + obj);
        else
            return;
    }

    protected final void flushFrames(Image aimage[]) {
        if (aimage == null)
            return;
        Trace.out(this, "flushing images");
        for (int l = 0; l < aimage.length; l++)
            if (aimage[l] != null) {
                aimage[l].flush();
                aimage[l] = null;
            }
    }

    public abstract void updateSpriteLogic(double d);

    protected void stepFramesProgress() {
        if (loader != null) {
            Integer i = progressPerSpriteClass.get(getClass().getName());
            if (i != null)
                loader.progress(i.intValue());
        }
    }

    public void setFreezable(boolean freezable) {
        this.freezable = freezable;
    }

    protected void handleCollision(CollisionResultSet c) {
        if (c.obj instanceof Tile) {
            if (!canPassSolid) {
                solidCollision(c.obj, c.nx, c.ny);
                return;
            }
        } else if (c.obj instanceof Sprite) {
            Sprite sprite = (Sprite)c.obj;
            if (sprite.isCollidable) {
                checkElevatorAttach(sprite, c.nx, c.ny);
                if (!canPassSolid && sprite.isSolid)
                    solidCollision(sprite, c.nx, c.ny);
                spriteCollision(sprite, c.nx, c.ny);
                sprite.spriteCollision(this, -c.nx, -c.ny);
                return;
            }
        } else {
            throw new RuntimeException("Unknown collision object!!");
        }
    }
}
