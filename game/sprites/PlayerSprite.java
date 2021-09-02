package game.sprites;

import game.*;
import java.awt.Graphics;
import java.awt.Image;
import java.io.*;

public class PlayerSprite extends Sprite {

    public static final int KEY_JUMP = 0;
    public static final int KEY_LEFT = 1;
    public static final int KEY_RIGHT = 2;
    public static final int N_KEYS = 3;
    private static final int widthBBCrop = 10;
    //private float jumpPower;
    private static PlayerSprite reference;
    private boolean keys[];
    //private long jumpStartedAt;
    //private long jumpTime;
    private static Image framesGirl[];
    private static Image framesBoy[];
    private Image frames[];
    private int currentFrame;
    private float health;
    private int lives;
    private int points;
    private int nCoins;
    private float impactTimer;
    private float immortalTimer;
    private int frameCounter;
    private boolean levelCompleted;
    private int currentLevel;
    private float orgX;
    private float orgY;
    private int jumpDir;
    private boolean cheater;
    private CheckpointSprite lastCheckpoint;
    private boolean haventTouchedGroundSinceLastKill;
    private static int playerType;
    //private boolean goToBonusLevel;
    private int coinsCollectedOnThisLevel;
    public static final int PLAYER_ROLLERBOY = 0;
    public static final int PLAYER_RASTAGIRL = 1;
    public static float horizAccelGround = 0.00252F;
    public static float horizAccelAir = 0.003F;
    public static float horizDampingGround = 0.98F;
    public static float horizDampingGroundWhenSkating = 0.987F;
    public static float horizDampingAir = 0.993F;
    private float playerDeadTimer;
    double time;
    //private float collDownTimer;
    int killMultiplier;
    private float standingStillTimer;
    private float springFiredTimer;
    private float inAirTimer;
    private int pingpong[] = { 0, 1, 2, 3, 3, 2, 1, 0 };

    public PlayerSprite() {
        //jumpPower = -0.01F;
        keys = new boolean[3];
        //jumpStartedAt = -1L;
        //jumpTime = 150L;
        health = 3F;
        lives = 2;
        currentLevel = 1;
        haventTouchedGroundSinceLastKill = false;
        playerDeadTimer = -1F;
        time = 0.0D;
        killMultiplier = 1;
        super.width = 22;
        super.height = 30;
        super.useHQmove = true;
        super.canPassSolid = false;
        super.isSolid = false;
        if (reference == null)
            reference = this;
        playerType = SelectCharacterAnimator.playerType;
        if (playerType == PLAYER_RASTAGIRL) {
            if (framesGirl == null)
                framesGirl = getFrames("playeranim2.gif", super.width + widthBBCrop, super.height);
            else
                stepFramesProgress();
            frames = framesGirl;
            return;
        }
        if (framesBoy == null)
            framesBoy = getFrames("playeranim.gif", super.width + widthBBCrop, super.height);
        else
            stepFramesProgress();
        frames = framesBoy;
    }

    public static void clearReference() {
        reference = null;
    }

    public void completeLevel() {
        if (!levelCompleted) {
            levelCompleted = true;
            SoundManager.ref(Game.getReference()).play("levelCompleted");
        }
    }

    @Override
	public boolean isPlayer() {
        return true;
    }

    @Override
	public void spriteCollision(Sprite sprite, float f, float f1) {
        if (sprite.isEnemy()) {
            if (impactTimer > 0.3F) {
                impactTimer = 0.0F;
                EnemySprite enemySprite = (EnemySprite)sprite;
                if ((f1 > 0.0F && f == 0.0F || super.dy > Math.abs(super.dx) && super.dy > 10F) && enemySprite.playerPainInflict(f, f1) > 0.0F && enemySprite.canBeJumpedUpon()) {
                    enemySprite.jumpedUpon(this);
                    super.HQhorizPush = -f * 1.2F;
                    super.HQvertPush = -f1 * 1.2F;
                    haventTouchedGroundSinceLastKill = true;
                    return;
                }
                float f2 = enemySprite.playerPainInflict(f, f1);
                if (f2 != 0.0F) {
                    if (immortalTimer <= 0.0F) {
                        immortalTimer = 2.0F;
                        health -= f2;
                        SoundManager.ref(Game.getReference()).play("pain");
                    }
                    super.HQhorizPush = -f * 0.4F;
                    super.HQvertPush = -f1 * 1.2F;
                }
                haventTouchedGroundSinceLastKill = false;
                return;
            }
        } else {
            if ((sprite instanceof SpringSprite) && springFiredTimer > 0.5F) {
                SpringSprite springsprite = (SpringSprite)sprite;
                if (springsprite.dir == 0 && f1 == 1.0F) {
                    super.HQvertPush = -3.5F;
                    springsprite.fireSpring();
                } else if (springsprite.dir == 1 && f1 == -1F) {
                    super.HQvertPush = 3.5F;
                    springsprite.fireSpring();
                } else if (springsprite.dir == 3 && (f1 == 1.0F || f == -1F)) {
                    super.HQhorizPush = 3.5F;
                    super.HQvertPush = -3.5F;
                    springsprite.fireSpring();
                } else if (springsprite.dir == 2 && (f1 == 1.0F || f == 1.0F)) {
                    super.HQhorizPush = -3.5F;
                    super.HQvertPush = -3.5F;
                    springsprite.fireSpring();
                }
                springFiredTimer = 0.0F;
                return;
            }
            if (sprite instanceof EndOfLevelSprite) {
            	/*
                if (!sprite.isFreezable())
                    goToBonusLevel = true;
                else
                    goToBonusLevel = false;
                */
                completeLevel();
                return;
            }
            if (sprite instanceof CheckpointSprite)
                lastCheckpoint = (CheckpointSprite)sprite;
        }
    }

    @Override
	public void load(DataInputStream datainputstream) throws IOException {
        super.load(datainputstream);
    }

    public boolean isImmortal() {
        return immortalTimer > 0.0F;
    }

    public boolean addHealth(float f) {
        if (health == 5F)
            return true;
        health += f;
        if (health > 5F)
            health = 5F;
        return false;
    }

    @Override
	public void save(DataOutputStream dataoutputstream) throws IOException {
        super.save(dataoutputstream);
    }

    public int getLives() {
        return lives;
    }

    public static void setPlayerType(int i) {
        playerType = i;
    }

    public static int getPlayerType() {
        return playerType;
    }

    public void setKey(int i, boolean flag) {
        keys[i] = flag;
    }

    public int getPoints() {
        return points / 9;
    }

    public void restartLevel() {
        playerDeadTimer = -1F;
        super.canPassSolid = false;
        super.isCollidable = true;
        super.dy = 0.0F;
        super.accy = 0.0F;
        if (lastCheckpoint != null) {
            super.x = ((Sprite) (lastCheckpoint)).x;
            super.y = ((Sprite) (lastCheckpoint)).y;
        } else {
            super.x = orgX;
            super.y = orgY;
        }
        health = 3F;
        FallSprite.restoreFallList();
    }

    public int getCoinsCollectedOnThisLevel() {
        return coinsCollectedOnThisLevel;
    }

    public void stepToNextLevel() {
        coinsCollectedOnThisLevel = 0;
        currentLevel++;
        lastCheckpoint = null;
        levelCompleted = false;
    }

    @Override
	public void kill() {
        if (playerDeadTimer == -1F) {
            SoundManager.ref(Game.getReference()).play("gameover");
            super.dx = 0.0F;
            super.accx = 0.0F;
            super.dy = -250F;
            super.accy = 3F;
            playerDeadTimer = 0.0F;
            health = 0.0F;
            super.canPassSolid = true;
            super.isCollidable = false;
        }
    }

    public static PlayerSprite getReference() {
        return reference;
    }

    public void addCoin() {
        coinsCollectedOnThisLevel++;
        nCoins++;
        if (nCoins % 300 == 0 && nCoins != 0) {
            lives++;
            SoundManager.ref(Game.getReference()).play("extraliv");
            GameAnimator.shakeLives();
        }
    }

    @Override
	public void paint(Graphics g) {
        if (playerDeadTimer != -1F)
            super.elevator = null;
        if (immortalTimer <= 0.0F || frameCounter % 2 != 0 || playerDeadTimer != -1F)
            drawImage(g, frames[currentFrame], super.x - 5F, super.y, super.width + widthBBCrop, super.height);
    }

    public void cheater() {
        points = 0;
        cheater = true;
    }

    public void addPoints(int i) {
        if (!cheater)
            points += i * 9;
    }

    @Override
	public Image getReferenceImage() {
        return ImageManager.getImage("sprites/player.gif");
    }

    public boolean isDead() {
        return playerDeadTimer > 2.5D;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int levelIndex) {
        currentLevel = levelIndex;
    }

    public boolean hasCompletedLevel() {
        return levelCompleted;
    }

    @Override
	public Sprite getCopy() {
        PlayerSprite playersprite = new PlayerSprite();
        playersprite.freezable = super.freezable;
        return playersprite;
    }

    @Override
	public void initPos(float x, float y) {
        orgX = x;
        orgY = y;
        super.initPos(x, y);
    }

    public int getKillMultiplier() {
        if (!haventTouchedGroundSinceLastKill)
            killMultiplier = 1;
        int i = killMultiplier;
        killMultiplier++;
        if (killMultiplier > 10)
            killMultiplier = 10;
        return i;
    }

    public void addCoins(int amount) {
        for (int i = 0; i < amount; i++)
            addCoin();
    }

    public int getNoCoins() {
        return nCoins;
    }

    @Override
	public void updateSpriteLogic(double d) {
        if (super.elevator != null && super.elevatorPos == 1)
            haventTouchedGroundSinceLastKill = false;
        if (playerDeadTimer != -1F)
            super.accy = (float)(300D * d);
        if (super.collDown) {
            //collDownTimer = 0.0F;
            super.HQhorizDamping = horizDampingGround;
            haventTouchedGroundSinceLastKill = false;
        } else {
            super.HQhorizDamping = horizDampingAir;
        }
        //collDownTimer += d;
        if (super.y + super.height > Sprite.world.getLevel().getHeight() * 32)
            health = 0.0F;
        if (levelCompleted) {
            for (int i = 0; i < keys.length; i++)
                keys[i] = false;
        }
        super.jumpKeyDown = keys[0];
        if (keys[0] && super.collDown && !super.collUp)
            initJump();
        if (jumpDir == -1 && !super.collDown)
            if (keys[2])
                jumpDir = 3;
            else if (keys[1])
                jumpDir = 2;
            else if (super.dx > 0.0F)
                jumpDir = 3;
            else
                jumpDir = 2;
        if (super.collDown) {
            if (inAirTimer > 0.1)
                SoundManager.ref(Game.getReference()).play("player-land");
            jumpDir = -1;
        }
        if(keys[1]) {
            super.HQhorizDamping = horizDampingGroundWhenSkating;
            if (super.collDown)
                super.HQhorizAccel = -horizAccelGround;
            else
                super.HQhorizAccel = -horizAccelAir;
        } else if(keys[2]) {
            super.HQhorizDamping = horizDampingGroundWhenSkating;
            if (super.collDown)
                super.HQhorizAccel = horizAccelGround;
            else
                super.HQhorizAccel = horizAccelAir;
        } else {
            super.HQhorizAccel = 0.0F;
        }
        if (playerDeadTimer != -1F)
            currentFrame = 1;
        else if (super.collDown) {
            if (!keys[1] && !keys[2]) {
                if (standingStillTimer > 3F) {
                    if ((int)(standingStillTimer * 3F) % 2 == 0)
                        currentFrame = 20;
                    else
                        currentFrame = 0;
                } else if(standingStillTimer > 1.0F)
                    currentFrame = 0;
                else if (Math.abs(super.dx) < 2.5F) {
                    if (super.dx > 0.0F)
                        currentFrame = 2;
                    else
                        currentFrame = 19;
                } else if (super.dx > 0.0F)
                    currentFrame = 8;
                else
                    currentFrame = 13;
            } else {
                standingStillTimer = 0.0F;
                if (super.HQhorizAccel > 0.0F && super.dx < 0.0F)
                    currentFrame = 13;
                else if (super.HQhorizAccel < 0.0F && super.dx > 0.0F)
                    currentFrame = 8;
                else if (keys[2])
                    currentFrame = 3 + pingpong[(int)((time * 18D) % pingpong.length)];
                else
                    currentFrame = 18 - pingpong[(int)((time * 16D) % pingpong.length)];
            }
            inAirTimer = 0.0F;
        } else if (inAirTimer > 0.05){
            if (jumpDir == 3) {
                if (super.dy > 0.0F)
                    currentFrame = 10;
                else
                    currentFrame = 9;
            } else if (super.dy > 0.0F)
                currentFrame = 11;
            else
                currentFrame = 12;
        }
        inAirTimer += d;
        standingStillTimer += d;
        springFiredTimer += d;
        immortalTimer -= d;
        impactTimer += d;
        time += d;
        if (playerDeadTimer >= 0.0F)
            playerDeadTimer += d;
        frameCounter++;
        if (health <= 0.0F)
            kill();
    }

    public float getHealth() {
        return health;
    }

    public void addLife(int i) {
        lives += i;
    }
}
