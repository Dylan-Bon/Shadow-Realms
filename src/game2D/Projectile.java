package game2D;

public class Projectile extends Sprite {
    private boolean active = false;
    long lastHitTime = System.currentTimeMillis();

    /**
     * Creates a new Projectile object with the specified Animation.
     *
     * @param anim The animation to use for the sprite of type Animation.
     */
    public Projectile(Animation anim) {
        super(anim);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getLastHitTime() {
        return lastHitTime;
    }

    public void updateLastHitTime() {
        lastHitTime = System.currentTimeMillis();
    }
}
