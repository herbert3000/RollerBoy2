package game;

public final class SpriteDuplicateChecker {

    private final int capacityIncrement = 20;
    private Sprite sprite[];
    private int nSprites;

    public SpriteDuplicateChecker() {
        sprite = new Sprite[20];
    }

    public boolean contains(Sprite s) {
        for (int i = 0; i < nSprites; i++)
            if (sprite[i] == s)
                return true;

        return false;
    }

    public void addSprite(Sprite s) {
        if (nSprites == sprite.length)
            expandSpriteArray();
        sprite[nSprites] = s;
        nSprites++;
    }

    private void expandSpriteArray() {
        Sprite asprite[] = new Sprite[sprite.length + capacityIncrement];
        System.arraycopy(sprite, 0, asprite, 0, sprite.length);
        sprite = asprite;
    }
}
