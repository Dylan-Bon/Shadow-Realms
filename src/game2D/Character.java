package game2D;

public abstract class Character extends Sprite{
    final int HEALTH_MAX = 3;
    int health;

    private boolean jumping = false;
    private boolean sprinting = false;
    private boolean casting = false;


    long lastHitTime = System.currentTimeMillis();

    /**
     * Creates a new Sprite object with the specified Animation.
     *
     * @param anim The animation to use for the sprite of type Animation.
     */
    public Character(Animation anim) {
        super(anim);
        health = HEALTH_MAX;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHEALTH_MAX() {
        return HEALTH_MAX;
    }

    public long getLastHitTime() {
        return lastHitTime;
    }

    public void updateLastHitTime() {
        lastHitTime = System.currentTimeMillis();
    }

    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }

    public boolean isCasting() {
        return casting;
    }

    public void setCasting(boolean casting) {
        this.casting = casting;
    }


}
