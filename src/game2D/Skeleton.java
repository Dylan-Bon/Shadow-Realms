package game2D;

public class Skeleton extends Character {
    private Animation idleAnim;
    private Animation sprintAnim;
    private Animation attackAnim;
    private Animation deathAnim;

    /**
     * @param skeleAnims
     */
    public Skeleton(Animation[] skeleAnims) {
        super(skeleAnims[0]);
        this.idleAnim = skeleAnims[0];
        this.sprintAnim = skeleAnims[1];
        this.deathAnim = skeleAnims[2];
        this.attackAnim = skeleAnims[3];
    }

    public Animation getIdleAnim() {
        return idleAnim;
    }

    public Animation getSprintAnim() {
        return sprintAnim;
    }

    public Animation getAttackAnim() {
        return attackAnim;
    }

    public Animation getDeathAnim() {
        return deathAnim;
    }
}
