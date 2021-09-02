package game;

class CollisionResultSet {

    Object obj;
    int nx;
    int ny;
    boolean fresh;
    Block block;

    public CollisionResultSet() {
        fresh = true;
    }

    public CollisionResultSet(Object obj, int nx, int ny, Block block) {
        fresh = true;
        this.obj = obj;
        this.nx = nx;
        this.ny = ny;
        this.block = block;
    }

    public CollisionResultSet set(Object obj, int nx, int ny, Block block) {
        this.obj = obj;
        this.nx = nx;
        this.ny = ny;
        this.block = block;
        return this;
    }
}
