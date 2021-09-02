package game;

public abstract class EnemySprite extends Sprite {

    public boolean canBeJumpedUpon() {
        return true;
    }

    public abstract void jumpedUpon(Sprite sprite);

    @Override
	public final boolean isEnemy() {
        return true;
    }

    public abstract float playerPainInflict(float f, float f1);

    static {
        if (Sprite.loader != null)
            Sprite.loader.progress();
    }
}
