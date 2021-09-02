package game;

import java.util.Enumeration;

final class SpriteEnumerator implements Enumeration<Sprite> {

    private SpriteManager spriteManager;
    private int layerId;
    private int spriteIndex;
    private int index;
    private int numElements;

    SpriteEnumerator(SpriteManager spriteManager) {
        this.spriteManager = spriteManager;
        numElements = spriteManager.getNoSprites();
    }

    @Override
	public Sprite nextElement() {
        Sprite sprite = spriteManager.getSprite(layerId, spriteIndex);
        if (spriteIndex == spriteManager.getNoSprites(layerId) - 1) {
            spriteIndex = 0;
            layerId++;
        } else {
            spriteIndex++;
        }
        index++;
        return sprite;
    }

    public void init() {
        layerId = 0;
        spriteIndex = 0;
        index = 0;
        numElements = spriteManager.getNoSprites();
    }

    @Override
	public boolean hasMoreElements() {
        return index < numElements;
    }
}
