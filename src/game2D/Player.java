package game2D;

public class Player extends Character{
    private int potionCount;
    private Animation idleAnim;
    private Animation sprintAnim;
    private Animation castAnim;
    private Animation deathAnim;
    private boolean interacting = false;

    /**
     *
     * @param idleAnim
     * @param sprintAnim
     * @param deathAnim
     * @param castAnim
     */
    public Player (Animation idleAnim, Animation sprintAnim, Animation deathAnim, Animation castAnim) {
        super(idleAnim);
        this.idleAnim = idleAnim;
        this.sprintAnim = sprintAnim;
        this.deathAnim = deathAnim;
        this.castAnim = castAnim;
        potionCount = 0;
    }

    public boolean isInteracting() {
        return interacting;
    }

    public void interact() {
        interacting = true;
    }

    public void stopInteraction() {
        interacting = false;
    }

    public int getPotionCount() {
        return potionCount;
    }

    public void setPotionCount(int potionCount) {
        this.potionCount = potionCount;
    }

    public Animation getIdleAnim() {
        return idleAnim;
    }

    public Animation getSprintAnim() {
        return sprintAnim;
    }

    public Animation getCastAnim() {
        return castAnim;
    }

    public Animation getDeathAnim() {
        return deathAnim;
    }
}
