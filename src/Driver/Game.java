package Driver;

import game2D.Character;
import game2D.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * @author David Cairns
 * @author Dyhabo
 */
public class Game extends GameCore {
    // Useful UI constants
    final private Image FULL_HEALTH = new ImageIcon("images/UI/3hp-38.png").getImage();
    final private Image TWO_HEALTH = new ImageIcon("images/UI/2hp-38.png").getImage();
    final private Image ONE_HEALTH = new ImageIcon("images/UI/1hp-38.png").getImage();
    final private Image NO_HEALTH = new ImageIcon("images/UI/0hp-38.png").getImage();
    final float GRAVITY = 0.001f;
    private final Collision collision = new Collision(this);
    private int xOffset = 0;
    private int yOffset = 0;

    // Driver.Game state info
    private boolean debugMode = false;
    private boolean superHealth = false;
    private boolean complete = false;
    private int currentZoneLevel = 1;

    // Player animation resources
    private Animation idle, sprint, cast, death;

    // Enemy animation resources
    private Animation skeleIdle, skeleWalk, skeleDie, skeleAttack;

    // Sprite types
    private Sprite trophy;
    private Projectile fireball;

    private Character player;
    private ArrayList<Character> skeletonList = new ArrayList<>();
    private ArrayList<Character> shadowSkeletonList = new ArrayList<>();
    private Sprite[] sky, clouds, sea;

    // Portal resources
    private Sprite portal;
    private boolean inShadowRealm;
    private int portalUseCount;
    private Sound portalNoise;

    private int potionCount;
    private Sprite[] potion;

    private TileMap tmap = new TileMap();    // Our tile map, note that we load it in init()

    public static void main(String[] args) {
        Game gct = new Game();
        gct.init();
        gct.run(false, 1000, 535); // Start in windowed mode with the given screen height and width
    }

    /**
     * Advances to the next level.
     */
    private void advanceCurrentZoneLevel() {
        if (currentZoneLevel < 3) {
            currentZoneLevel++;
            initialiseGame(currentZoneLevel);
            System.out.println("advanced");
        }
    }

    private int getCurrentZoneLevel() {
        return currentZoneLevel;
    }

    /**
     * Initialise the class, e.g. set up variables, load images, create animations, register event handlers.
     * This method should only be called ONCE from the main method.
     */
    public void init() {
        setTitle("Shadow Realms");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initPotions();
        initSkeleAnims();
        initPlayer();
        initFireball();
        initPurplePortal();
        initTrophy();

        initialiseGame(getCurrentZoneLevel());

        Sound music = new Sound("sounds/magic cliffs.wav", false, false);
        music.setLoop(true);
        music.start();
    }

    /**
     * Initialises trophy sprite with it's animation frame.
     */
    private void initTrophy() {
        Animation trophyFrame = new Animation();
        trophyFrame.addFrame(loadImage("images/trophy-64.png"), 1000);
        trophy = new Sprite(trophyFrame);
    }

    /**
     * Initialises the portal sprite along with the portalNoise sound
     */
    private void initPurplePortal() {
        Animation portalOpen = new Animation();
        portalOpen.loadAnimationFromSheet("images/Portal/Portal-Open-x2.png", 8, 1, 120);
        portal = new Sprite(portalOpen);

        portalNoise = new Sound("sounds/Suck_1.wav", true, true);
        portalNoise.setLoop(true);
    }

    /**
     * Initialises the background sprites to be used in the first (and second) level.
     */
    private void initFirstBackground() {
        sea = instantiateBackground(112, "images/Parallax/sea.png", 431);
        clouds = instantiateBackground(544, "images/Parallax/clouds.png", 195);
        sky = instantiateBackground(112, "images/Parallax/sky.png", 0);

        for (Sprite s : clouds) {
            s.setVelocityX(-0.02f);
        }
    }

    /**
     * Initialises the background sprites.
     *
     * @param imgWidth   int, the width of a single sprite image.
     * @param bgImageLoc String, the file location of the image.
     * @param yPos       int, the Y-Axis position that each sprite should be set to.
     * @return Sprite array.
     */
    private Sprite[] instantiateBackground(int imgWidth, String bgImageLoc, int yPos) {
        Animation bgAnim = new Animation();
        bgAnim.addFrame(loadImage(bgImageLoc), 1000);

        Sprite[] backgroundSprites = new Sprite[(tmap.getPixelWidth() / imgWidth) + 2];

        int tempX = -imgWidth;
        for (int i = 0; i < backgroundSprites.length; i++) {
            backgroundSprites[i] = new Sprite(bgAnim);
            backgroundSprites[i].setPosition(tempX, yPos);
            tempX += imgWidth;
        }
        return backgroundSprites;
    }

    /**
     * Initialises fireball with its appropriate animation.
     */
    private void initFireball() {
        Animation fireballAnim = new Animation();
        fireballAnim.addFrame(loadImage("images/spells/fire/FB001.png"), 120);
        fireballAnim.addFrame(loadImage("images/spells/fire/FB002.png"), 120);
        fireballAnim.addFrame(loadImage("images/spells/fire/FB003.png"), 120);
        fireballAnim.addFrame(loadImage("images/spells/fire/FB004.png"), 120);
        fireballAnim.addFrame(loadImage("images/spells/fire/FB005.png"), 120);
        fireball = new Projectile(fireballAnim);
        fireball.setAnimationSpeed(0.8f);
    }

    /**
     * Initialises potion[] with three Sprites with their appropriate animation (single frame image).
     */
    private void initPotions() {
        Animation potionFrame = new Animation();
        potionFrame.addFrame(loadImage("images/potion.png"), 1000);
        potion = new Sprite[3];

        for (int i = 0; i < potion.length; i++) {
            potion[i] = new Sprite(potionFrame);
        }
    }

    /**
     * Initialises player Sprite with the idle animation. Also initialises the player animations; sprint, cast, and death.
     */
    private void initPlayer() {
        idle = new Animation();
        idle.loadAnimationFromSheet("images/Mage-Idle3-48.png", 8, 1, 120);
        idle.setAnimationSpeed(1.5f);
        player = new Character(idle); // Initialise the player with an animation

        sprint = new Animation();
        sprint.loadAnimationFromSheet("images/Mage-Sprint-48.png", 7, 1, 120);
        sprint.setAnimationSpeed(1.5f);

        cast = new Animation();
        cast.loadAnimationFromSheet("images/Mage-Cast2-48.png", 4, 1, 200);
        cast.setLoop(true);

        death = new Animation();
        death.loadAnimationFromSheet("images/Mage-Death-48.png", 8, 1, 200);
    }

    /**
     * Initialises the skeleton Animations; skeleIdle, skeleWalk, skeleDie, skeleAttack.
     */
    private void initSkeleAnims() {
        skeleIdle = new Animation();
        skeleIdle.loadAnimationFromSheet("images/Enemy/Skeleton/Skeleton Idle-64.png", 11, 1, 120);
        skeleIdle.setAnimationSpeed(1.0f);

        skeleWalk = new Animation();
        skeleWalk.loadAnimationFromSheet("images/Enemy/Skeleton/Skeleton Walk-64.png", 13, 1, 120);
        skeleWalk.setAnimationSpeed(1.0f);

        skeleDie = new Animation();
        skeleDie.loadAnimationFromSheet("images/Enemy/Skeleton/Skeleton Dead-64.png", 15, 1, 120);
        skeleDie.setAnimationSpeed(1.0f);

        skeleAttack = new Animation();
        skeleAttack.loadAnimationFromSheet("images/Enemy/Skeleton/Skeleton Attack-64.png", 18, 1, 120);
        skeleAttack.setAnimationSpeed(1.0f);
    }

    /**
     * Initialises the game for the current level.
     */
    public void initialiseGame(int currentZoneLevel) {
        resetPlayer();
        if (currentZoneLevel == 1) {
            // Load the tile map and print it out so we can check it is valid
            tmap.loadMap("maps", "tmap2.txt");
            System.out.println(tmap);
            initFirstBackground();
            resetFirstLevel();
        } else if (currentZoneLevel == 2) {
            // Load the tile map and print it out so we can check it is valid
            skeletonList.clear();
            shadowSkeletonList.clear();
            tmap.loadMap("maps", "tmap.txt");
            System.out.println(tmap);
            resetSecondLevel();
        } else if (currentZoneLevel == 3) {
            tmap.loadMap("maps", "map.txt");
            System.out.println(tmap);
            skeletonList.clear();
            shadowSkeletonList.clear();
            ggLevel();
        } else {
            System.out.println("Something went VERY wrong when resetting level.");
        }
    }

    private void ggLevel() {
        complete = true;
        portal.hide();
    }

    /**
     * Resets the first level.
     */
    private void resetFirstLevel() {
        potion[0].setPosition(1820, 399);
        potion[1].setPosition(3185, 207);
        potion[2].setPosition(2261, 463);
        for (Sprite s : potion) {
            s.show();
        }

        portal.setPosition(3062, 417);
        portal.hide();

        if (skeletonList.isEmpty()) {
            skeletonList.add(new Character(skeleIdle));
            skeletonList.get(0).setStartingX(1471);
            skeletonList.get(0).setStartingY(400);

            skeletonList.add(new Character(skeleIdle));
            skeletonList.get(1).setStartingX(1880);
            skeletonList.get(1).setStartingY(208);

            skeletonList.add(new Character(skeleIdle));
            skeletonList.get(2).setStartingX(2280);
            skeletonList.get(2).setStartingY(208);

            skeletonList.add(new Character(skeleIdle));
            skeletonList.get(3).setStartingX(2280);
            skeletonList.get(3).setStartingY(452);

            skeletonList.add(new Character(skeleIdle));
            skeletonList.get(4).setStartingX(2500);
            skeletonList.get(4).setStartingY(452);

            skeletonList.add(new Character(skeleIdle));
            skeletonList.get(5).setStartingX(3200);
            skeletonList.get(5).setStartingY(207);
        }

        initSkeletons(skeletonList);

        trophy.setPosition(132, 303);
        shadowSkeletonList.clear();

        System.out.println("first level reset");
    }

    private void spawnFirstLevelShadowRealm() {
        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(0).setStartingX(2280);
        shadowSkeletonList.get(0).setStartingY(208);

        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(1).setStartingX(2080);
        shadowSkeletonList.get(1).setStartingY(208);

        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(2).setStartingX(2180);
        shadowSkeletonList.get(2).setStartingY(208);

        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(3).setStartingX(1471);
        shadowSkeletonList.get(3).setStartingY(400);

        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(4).setStartingX(1371);
        shadowSkeletonList.get(4).setStartingY(400);

        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(5).setStartingX(415);
        shadowSkeletonList.get(5).setStartingY(208);

        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(6).setStartingX(500);
        shadowSkeletonList.get(6).setStartingY(208);

        initSkeletons(shadowSkeletonList);
    }

    private void spawnSecondLevelShadowRealm() {
        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(0).setStartingX(3375);
        shadowSkeletonList.get(0).setStartingY(140);

        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(1).setStartingX(3316);
        shadowSkeletonList.get(1).setStartingY(140);

        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(2).setStartingX(1229);
        shadowSkeletonList.get(2).setStartingY(364);

        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(3).setStartingX(1379);
        shadowSkeletonList.get(3).setStartingY(364);

        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(4).setStartingX(1462);
        shadowSkeletonList.get(4).setStartingY(364);

        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(5).setStartingX(1186);
        shadowSkeletonList.get(5).setStartingY(364);

        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(6).setStartingX(1138);
        shadowSkeletonList.get(6).setStartingY(364);

        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(7).setStartingX(486);
        shadowSkeletonList.get(7).setStartingY(428);

        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(8).setStartingX(446);
        shadowSkeletonList.get(8).setStartingY(428);

        shadowSkeletonList.add(new Character(skeleIdle));
        shadowSkeletonList.get(9).setStartingX(2771);
        shadowSkeletonList.get(9).setStartingY(140);

        initSkeletons(shadowSkeletonList);
    }

    /**
     * Resets the second level
     */
    private void resetSecondLevel() {
        potion[0].setPosition(1880, 367);
        potion[1].setPosition(2030, 111);
        potion[2].setPosition(3373, 143);
        for (Sprite s : potion) {
            s.show();
        }

        portal.setPosition(3062, 105);
        portal.hide();

        if (skeletonList.isEmpty()) {
            skeletonList.add(new Character(skeleIdle));
            skeletonList.get(0).setStartingX(1478);
            skeletonList.get(0).setStartingY(367);

            skeletonList.add(new Character(skeleIdle));
            skeletonList.get(1).setStartingX(1382);
            skeletonList.get(1).setStartingY(367);

            skeletonList.add(new Character(skeleIdle));
            skeletonList.get(2).setStartingX(1735);
            skeletonList.get(2).setStartingY(271);

            skeletonList.add(new Character(skeleIdle));
            skeletonList.get(3).setStartingX(1448);
            skeletonList.get(3).setStartingY(207);

            skeletonList.add(new Character(skeleIdle));
            skeletonList.get(4).setStartingX(1885);
            skeletonList.get(4).setStartingY(111);

            skeletonList.add(new Character(skeleIdle));
            skeletonList.get(5).setStartingX(1950);
            skeletonList.get(5).setStartingY(111);

            skeletonList.add(new Character(skeleIdle));
            skeletonList.get(6).setStartingX(2015);
            skeletonList.get(6).setStartingY(111);

            skeletonList.add(new Character(skeleIdle));
            skeletonList.get(7).setStartingX(2094);
            skeletonList.get(7).setStartingY(111);

            skeletonList.add(new Character(skeleIdle));
            skeletonList.get(8).setStartingX(2683);
            skeletonList.get(8).setStartingY(143);

            skeletonList.add(new Character(skeleIdle));
            skeletonList.get(9).setStartingX(2715);
            skeletonList.get(9).setStartingY(143);
        }

        initSkeletons(skeletonList);
        trophy.setPosition(132, 428);
        shadowSkeletonList.clear();
    }

    /**
     * Returns skeletons to full health and sets their current position to their starting position.
     *
     * @param skeletonList ArrayList of Characters.
     */
    private void initSkeletons(ArrayList<Character> skeletonList) {
        for (Character skeleton : skeletonList) {
            skeleton.setHealth(skeleton.getHEALTH_MAX());
            skeleton.setPosition(skeleton.getStartingX(), skeleton.getStartingY());
            skeleton.setAnimation(skeleIdle);
            skeleton.show();
        }
    }

    /**
     * Resets the health, position, and number of picked up potions a player had before dying.
     * If superHealth is enabled, the health will be set to 99, otherwise it will be 3.
     * player Animation is set to idle and it's position is (415, 200).
     * potionCount set to 0.
     */
    private void resetPlayer() {
        if (superHealth)
            enableSuperHealth();
        else
            player.setHealth(player.getHEALTH_MAX());

        player.setAnimation(idle);
        player.setPosition(415, 200);
        player.setVelocityX(0);
        player.setVelocityY(0);
        player.show();

        inShadowRealm = false;
        potionCount = 0;
        portalUseCount = 0;
    }

    /**
     * Draw the current state of the game
     */
    public void draw(Graphics2D g) {
        updateOffsets();

        g.setColor(Color.cyan);
        g.fillRect(0, 0, getWidth(), getHeight());

        drawBackground(g, xOffset);

        if (inShadowRealm) {
            g.setColor(new Color(0, 0, 0, 0.87f)); // Draws a slightly opaque layer over the background images
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // draw Sprites
        drawSprite(player, g, xOffset, yOffset);
        drawSprite(fireball, g, xOffset, yOffset);
        drawSprite(portal, g, xOffset, yOffset);

        for (Sprite pot : potion) {
            drawSprite(pot, g, xOffset, yOffset);
        }

        if (!inShadowRealm) {
            for (Character skeleton : skeletonList) {
                drawSprite(skeleton, g, xOffset, yOffset);
            }
        } else {
            for (Character skeleton : shadowSkeletonList) {
                drawSprite(skeleton, g, xOffset, yOffset);
            }
            drawSprite(trophy, g, xOffset, yOffset);
        }

        tmap.draw(g, xOffset, yOffset); // Apply offsets to tile map and draw  it

        // Show level and status information
        g.setColor(Color.darkGray);
        g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 14));
        if (portal.isVisible() && portalUseCount < 1) {
            g.drawString("Press 'E' to toggle interaction with portal.", getWidth() / 2 - 150, 50);
        } else if (complete) {
            g.drawString("Congrats! You Win. Press 'Q' to restart.", getWidth() / 2 - 140, 50);
        }

        String msg = String.format("Level: %d", currentZoneLevel);
        g.drawString(msg, getWidth() - 80, 50);

        g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 13));
        drawHealthUI(g);

        if (debugMode) { // Player & game information
            debugEnable(g, xOffset, yOffset);
        }

        if (superHealth) { // Sets health to 99
            g.setColor(Color.red);
            g.drawString("Super Health", getWidth() - 95, 175);
        }
    }

    /**
     * Updates the x and y coordinate offsets used for draw().
     */
    private void updateOffsets() {
        xOffset = 0;
        yOffset = 0;
        if ((int) player.getX() >= getWidth() / 2) {
            xOffset = -(int) player.getX() + getWidth() / 2;
        }
    }

    /**
     * Apply offsets to Sprites and then draw
     *
     * @param sprite Sprite
     * @param g      Graphics2D
     * @param xo     int X offset for drawing
     * @param yo     int Y offset for drawing
     */
    private void drawSprite(Sprite sprite, Graphics2D g, int xo, int yo) {
        sprite.setOffsets(xo, yo);
        sprite.draw(g);
    }

    /**
     * Draws the appropriate health bar for the player depending on value of player.getHealth()
     *
     * @param g Graphics2D
     */
    private void drawHealthUI(Graphics2D g) {
        if (player.getHealth() >= 3) {
            g.drawImage(FULL_HEALTH, 50, 50, null);
        } else if (player.getHealth() == 2) {
            g.drawImage(TWO_HEALTH, 50, 50, null);
        } else if (player.getHealth() == 1) {
            g.drawImage(ONE_HEALTH, 50, 50, null);
        } else {
            g.drawImage(NO_HEALTH, 50, 50, null);
        }
    }

    /**
     * Toggles debug mode on and off.
     * This includes showing collision boxes around all Characters and displays details like fps and player co-ords.
     */
    private void toggleDebugMode() {
        debugMode = !debugMode;
    }

    /**
     * Called when debugMode is true.
     *
     * @param g  Graphics2D passed from Draw method.
     * @param xo int x-offset co-ordinate.
     * @param yo int y-offset co-ordinate.
     */
    private void debugEnable(Graphics2D g, int xo, int yo) {
        g.drawString("FPS: " + getFPS(), getWidth() - 80, 75);
        g.drawString("X: " + player.getX(), getWidth() - 80, 100);
        g.drawString("Y: " + player.getY(), getWidth() - 80, 125);
        g.drawString("Y+Height: " + (player.getY() + player.getHeight()), getWidth() - 140, 150);

        player.drawBoundingBox(g, xo, yo);

        if (!inShadowRealm) {
            for (Character skeleton : skeletonList) {
                skeleton.drawBoundingBox(g, xo, yo);
            }
        } else {
            for (Character skeleton : shadowSkeletonList) {
                skeleton.drawBoundingBox(g, xo, yo);
            }
        }
    }


    /**
     * Draws the background sprites (only works for first level atm)
     *
     * @param g  Graphics2D
     * @param xo int
     */
    private void drawBackground(Graphics2D g, int xo) {
        for (Sprite s : sky) {
            s.setOffsets(xo / 8, 0);
            s.draw(g);
        }

        for (Sprite s : clouds) {
            s.setOffsets(xo / 8, 0);
            s.draw(g);
        }

        for (Sprite s : sea) {
            s.setOffsets(xo / 3, 0);
            s.setVelocityX(-player.getVelocityX() / 10);
            s.draw(g);
        }
    }

    /**
     * Moves clouds that have went off screen to the end of the row of clouds.
     *
     * @param elapsed long
     */
    private void updateClouds(long elapsed) {
        for (Sprite s : clouds) {
            if (s.getX() < -s.getWidth()) {
                float endCloudPosX = 0;
                for (Sprite sp : clouds) {
                    if (sp.getX() > endCloudPosX) {
                        endCloudPosX = sp.getX();
                    }
                }
                s.setX(endCloudPosX + s.getWidth());
            }
            s.update(elapsed);
        }
    }


    /**
     * Update any sprites and check for collisions
     *
     * @param elapsed The elapsed time between this call and the previous call of elapsed
     */
    public void update(long elapsed) {
        updatePlayer(elapsed);
        updateClouds(elapsed);

        // Now update the sprites animation and position
        player.update(elapsed);
        fireball.update(elapsed);
        // Then check for any collisions that may have occurred
        handleMapEdge(player, tmap);

        collision.checkTileCollision(player, tmap);
        collision.checkTileCollision(fireball, tmap);

        updateEnemySkeletons(elapsed);

        checkPotionCollision();
        updatePortal(elapsed);

        if (portalUseCount == 1) {
            if (inShadowRealm && currentZoneLevel == 1) {
                spawnFirstLevelShadowRealm();
                portalUseCount++;
            }
            if (inShadowRealm && currentZoneLevel == 2) {
                spawnSecondLevelShadowRealm();
                portalUseCount++;
            }
        }

        if (inShadowRealm) {
            collision.checkSpriteCollision(trophy, player);
        }

        fallingDeath(player);

        checkPlayerDeath();
        collision.checkTileCollision(player, tmap);
    }


    /**
     * Applies gravity effect to player, sets the animation speed and calls handlePlayerMovement()
     *
     * @param elapsed long
     */
    private void updatePlayer(long elapsed) {
        applyGravity(player, elapsed);

        //player.setAnimationSpeed(1.0f);

        handlePlayerMovement();
    }

    /**
     * Handles velocity & direction changes for the player and determines which direction a fireball should go when cast.
     */
    private void handlePlayerMovement() {
        if (player.isSprinting()) {
            if (player.isDirectionLeft()) {
                player.setVelocityX(-0.18f);
            } else {
                player.setVelocityX(0.18f);
            }
        }

        if (player.isCasting()) {
            if (player.getAnimation().getCurrFrameIndex() == 3) {

                fireball.setDirectionLeft(player.isDirectionLeft());

                int tempX = (int) player.getX();
                if (player.isDirectionLeft()) {
                    tempX -= 25;
                    fireball.setVelocityX(-0.7f);
                } else {
                    tempX += 25;
                    fireball.setVelocityX(0.7f);
                }

                fireball.setPosition(tempX, player.getY() + 20);
                fireball.setActive(true);
            }
        }
    }


    /**
     * Calls handleEnemySkeletons to update enemy skeletons. If the player is in the shadow realm then shadow enemies will also be updated.
     *
     * @param elapsed long
     */
    private void updateEnemySkeletons(long elapsed) {
        if (!inShadowRealm) {
            handleEnemySkeletons(elapsed, skeletonList);
        } else {
            handleEnemySkeletons(elapsed, shadowSkeletonList);
        }
    }

    /**
     * Handles updating the position, actions, collision and despawning of skeleton enemies.
     * Calls checkAgro(Character), checkSpriteCollision(Sprite,Sprite)
     *
     * @param elapsed           long
     * @param enemySkeletonList the list of skeleton enemies to update
     */
    private void handleEnemySkeletons(long elapsed, ArrayList<Character> enemySkeletonList) {
        for (Character skeleton : enemySkeletonList) {
            if (skeleton.getY() > 0) {
                //applyGravity(skeleton, elapsed);
                skeleton.update(elapsed);
                applyGravity(skeleton, elapsed);

                handleMapEdge(skeleton, tmap);
                collision.checkTileCollision(skeleton, tmap);

                if (skeleton.getHealth() > 0) {
                    fallingDeath(skeleton);
                    checkAgro(skeleton);
                    collision.checkSpriteCollision(fireball, skeleton);
                    collision.checkSpriteCollision(player, skeleton);

                    int index = enemySkeletonList.indexOf(skeleton);
                    for (int i = 0; i < enemySkeletonList.size(); i++) {
                        if (i != index && enemySkeletonList.get(i).getHealth() > 0) {
                            collision.checkSpriteCollision(skeleton, enemySkeletonList.get(i));
                        }
                    }
                } else {
                    despawnEnemy(skeleton);
                }
            }
        }
    }

    /**
     * Applies gravity to Characters, no one can defy Gravity >:).
     *
     * @param character Character
     * @param elapsed   long
     */
    private void applyGravity(Character character, long elapsed) {
        character.setVelocityY(character.getVelocityY() + (GRAVITY * elapsed));
    }

    /**
     * Spawns the portal if 3 potions have been collected, updates the portal.
     * If the portal has spawned, the portal noise is played when the player is within range.
     *
     * @param elapsed long
     */
    private void updatePortal(long elapsed) {
        spawnPortal();
        portal.update(elapsed);

        if (portal.isVisible()) {
            int playerX = (int) player.getX();
            int playerY = (int) player.getY();
            int portalX = (int) portal.getX();
            int portalY = (int) portal.getY();

            if (playerX - portalX + portal.getWidth() > -150 && playerX + player.getWidth() - portalX < 150) {
                if (playerY - portalY + portal.getHeight() > -100 && playerY + player.getHeight() - portalY < 100) {

                    if (!portalNoise.isPlaying()) {
                        playPortalNoise();
                    }

                } else if (portalNoise.isPlaying()) {
                    stopPortalNoise();
                }
            } else if (portalNoise.isPlaying()) {
                stopPortalNoise();
            }
            collision.checkSpriteCollision(portal, player);
        }
    }

    /**
     * Spawn portal when all potions have been acquired.
     */
    private void spawnPortal() {
        if (potionCount >= 3) {
            portal.show();
        }
    }

    /**
     * Play the portalNoise Sound. Will do portalNoise.start() if the thread is not yet running,
     * otherwise portalNoise.playClip is utilised.
     */
    private void playPortalNoise() {
        if (portalNoise.isLaunched()) {
            portalNoise.playClip();
        } else {
            portalNoise.start();
        }
    }

    /**
     * Pauses the playback of portalNoise using portalNoise.pauseClip()
     */
    private void stopPortalNoise() {
        if (portalNoise.isLaunched()) {
            portalNoise.pauseClip();
        }
    }

    /**
     * Creates a new Sound instance and plays the sound.
     * This sound is used when the enemy hits the player.
     */
    public void playerHitNoise() {
        Sound enemyHit = new Sound("sounds/Hit_damage_1.wav", false, false);
        enemyHit.setLoop(false);
        enemyHit.start();
    }

    /**
     * Creates a new Sound instance and plays the suond.
     * This sound is used when the player's fireball hits an enemy.
     */
    private void projectileHitNoise() {
        Sound projectileHit = new Sound("sounds/Boss_hit_1.wav", false, false);
        projectileHit.setLoop(false);
        projectileHit.start();
    }

    /**
     * Hides enemy off screen.
     *
     * @param enemy Character
     */
    private void despawnEnemy(Character enemy) {
        if (enemy.getAnimation() == skeleDie && enemy.getAnimation().getCurrFrameIndex() >= (skeleDie.countOfFrames() - 1)) {
            enemy.setY(-6000);
        }
    }

    /**
     * Determines whether a skeleton is aggressive to the player.
     *
     * @param skeleton Character
     */
    private void checkAgro(Character skeleton) {
        //prevent the attack animation from starting again.
        if (skeleton.getAnimation() != skeleDie && (skeleton.getAnimation() != skeleAttack || (skeleton.getAnimation() == skeleAttack && skeleton.getAnimation().hasLooped()))) {

            if (player.getY() - skeleton.getY() >= -150 && player.getY() - skeleton.getY() <= 150) { // if player is in Y agro range

                if (skeleton.getX() - player.getX() <= 300 && skeleton.getX() - player.getX() >= 10) { //if player is in X agro range at left side
                    skeleton.setDirectionLeft(true);

                    if (skeleton.getX() - player.getX() <= 75 && skeleton.getX() - player.getX() >= 10) { //if in attack range
                        skeleton.stop();
                        skeleton.setVelocityX(-0.03f);

                        if (skeleton.getAnimation() != skeleAttack) {
                            skeleAttack.start();
                            skeleton.setAnimation(skeleAttack);
                        }

                    } else { // else not in attack range
                        skeleton.setSprinting(true);
                        skeleton.setAnimation(skeleWalk);
                        skeleton.setVelocityX(-0.1f);
                    }

                } else if (skeleton.getX() - player.getX() <= 0 && skeleton.getX() - player.getX() > -300) { // if player is in X agro range at right side
                    skeleton.setDirectionLeft(false);

                    if (skeleton.getX() - player.getX() <= 0 && skeleton.getX() - player.getX() >= -100) { // if player is in attack range
                        skeleton.stop();
                        skeleton.setVelocityX(0.03f);

                        if (skeleton.getAnimation() != skeleAttack) {
                            skeleAttack.start();
                            skeleton.setAnimation(skeleAttack);
                        }

                    } else { // else not in attack range
                        skeleton.setSprinting(true);
                        skeleton.setAnimation(skeleWalk);
                        skeleton.setVelocityX(0.1f);
                    }

                } else { // else not in agro range
                    skeleton.stop();
                    skeleton.setSprinting(false);
                    if (skeleton.getAnimation() != skeleIdle) {
                        if (skeleton.getAnimation() == skeleAttack) {
                            skeleton.setY(skeleton.getY() + 10);
                        }
                        skeleton.setAnimation(skeleIdle);
                    }
                }
            }
        }
    }

    /**
     * Kills characters if they fall off thed map.
     *
     * @param character Character
     */
    private void fallingDeath(Character character) {
        if (character.getY() + character.getHeight() >= tmap.getPixelHeight() - 5) {
            character.setHealth(0);
            if (character != player) {
                checkEnemyDeath(character);
            }
        }
    }

    /**
     * Checks the health of player.
     * If health dips below 1 then this code will play the death animation and reset the level.
     */
    private void checkPlayerDeath() {
        if (player.getHealth() < 1) {
            //if players animation is not set do death animation, then set it.
            if (player.getAnimation() != death) {
                player.setY(player.getY() - 35); //bump the y co-ord up by 35 do compensate for the larger animation.
                death.start();
                player.setAnimation(death);
            }

            player.stop();
            player.setSprinting(false);
            player.setJumping(false);

            if (death.getCurrFrameIndex() == (death.countOfFrames() - 1)) {
                initialiseGame(getCurrentZoneLevel());
            }
        }
    }

    /**
     * If enemy health is 1, the animation of the enemy is set to the death animation.
     *
     * @param enemy Skeleton Sprite.
     */
    private void checkEnemyDeath(Character enemy) {
        if (enemy.getHealth() < 1) {
            enemy.stop();
            skeleDie.start();
            enemy.setAnimation(skeleDie);
        }
    }

    /**
     * Checks and handles collisions with the edge of the map.
     *
     * @param s    The Sprite to check collisions for
     * @param tmap The tile map to check
     */
    public void handleMapEdge(Sprite s, TileMap tmap) {
        // Check if sprite has fallen off bottom of screen.
        if (s.getY() + s.getHeight() > tmap.getPixelHeight()) {
            // Put the player back on the map 1 pixel above the bottom
            s.setY(tmap.getPixelHeight() - s.getHeight() - 1);
            s.setVelocityY(0);
        }
        //check if sprite is going too far off screen.
        if (s.getX() < 3 || s.getX() == 3) {
            s.setX(3);
            s.setVelocityX(0);
        } else if (s.getX() >= (tmap.getPixelWidth() - 50)) {
            s.setX(tmap.getPixelWidth() - 50);
            s.setVelocityX(0);
        }
    }

    /**
     * Override of mouseReleased event defined in GameCore.
     * This is a CHEAT. The mouse is not used for typical gameplay, however if you do choose to use the mouse then this method
     * provides a 'teleport' ability which moves the player sprite to the location that the mouse has clicked (on release) on.
     * This method is used over mouseClicked() as the latter proved to be unstable.
     *
     * @param e MouseEvent
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY() - player.getHeight();

        if ((int) player.getX() >= getWidth() / 2 && mouseX <= getWidth() / 2) {
            mouseX = (int) player.getX() - (getWidth() / 2 - mouseX);
        } else if ((int) player.getX() >= getWidth() / 2 && mouseX > getWidth() / 2) {
            mouseX = (int) player.getX() + (mouseX - getWidth() / 2);
        }
        player.setPosition(mouseX, mouseY);
        System.out.println("CHEAT ACTIVATED. Teleporting to: " + mouseX + "," + mouseY);
    }

    /**
     * Override of the keyPressed event defined in GameCore to catch our own events.
     * Uses enhanced switch statement. Required Java 13.
     *
     * @param e The event that has been generated.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (player.getHealth() > 0) {
            switch (key) {
                case KeyEvent.VK_UP, KeyEvent.VK_W -> {
                    if (!player.isJumping()) {
                        player.setVelocityY(-0.5f);
                        player.setJumping(true);
                    }
                }
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
                    player.setSprinting(true);
                    player.setDirectionLeft(false);
                    player.setAnimation(sprint);
                }
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> {
                    player.setSprinting(true);
                    player.setDirectionLeft(true);
                    player.setAnimation(sprint);
                }
                case KeyEvent.VK_SPACE -> {
                    if (!player.isCasting()) {
                        player.setY(player.getY() - 14);
                        cast.start();
                    }
                    player.setCasting(true);
                    player.setAnimation(cast);
                }
                case KeyEvent.VK_Q -> {
                    if (complete) {
                        currentZoneLevel = 1;
                        initialiseGame(currentZoneLevel);
                        complete = false;
                    }
                }
            }
        }
    }

    /**
     * Override of the keyReleased event defined in GameCore to catch our own events.
     * Uses enhanced switch statement. Required Java 13.
     *
     * @param e the event that has been generated.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (player.getHealth() > 0) {
            switch (key) {
                case KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_A, KeyEvent.VK_D -> {
                    player.setSprinting(false);
                    player.setVelocityX(0);
                    player.setAnimation(idle);
                }
                case KeyEvent.VK_SPACE -> stopCasting();
                case KeyEvent.VK_E -> player.interact();
                case KeyEvent.VK_0 -> toggleDebugMode();
                case KeyEvent.VK_9 -> toggleSuperHealth();
            }
        }
    }

    /**
     * Stops the player from casting a spell.
     */
    private void stopCasting() {
        player.setCasting(false);
        player.setY(player.getY() + 14);

        if (!player.isSprinting()) {
            player.setAnimation(idle);
        } else {
            player.setAnimation(sprint);
        }
    }

    /**
     * Checks collision for fireball projectile, portal, trophy and potions.
     *
     * @param s1 Spirte (Projectile, Portal, Trophy, Player (if checking potions)
     * @param s2 Sprite (Player, Potion)
     */
    public void nonCharacterCollision(Sprite s1, Sprite s2) {
        if (s1 instanceof Projectile && s2 instanceof Character) {
            checkProjectileCharCollision((Projectile) s1, (Character) s2);
        } else if (s1 == portal && s2 == player) {
            handlePortalCollision();

        } else if (s1 == trophy && s2 == player) {
            advanceCurrentZoneLevel();
        } else if (s1 == player) {
            for (Sprite pot : potion) {
                if (s2 == pot) {
                    acquirePotion(s2);
                }
            }
        }
    }

    /**
     * Calls checkSpriteCollision using player and each potion
     */
    private void checkPotionCollision() {
        for (Sprite s : potion) {
            if (s.isVisible()) {
                collision.checkSpriteCollision(player, s);
            }
        }
    }

    /**
     * If the potion is visible then increase potionCount by 1 and hide the potion.
     *
     * @param s2 Sprite potion
     */
    private void acquirePotion(Sprite s2) {
        potionCount++;
        s2.hide();
    }

    /**
     * Check if a Projectile collides with a Character, if it does then let the Character take damage.
     *
     * @param s1 the Projectile
     * @param s2 the Character
     */
    private void checkProjectileCharCollision(Projectile s1, Character s2) {
        if (System.currentTimeMillis() - s2.getLastHitTime() > 700) {
            projectileHitNoise();
            s1.stop();
            s1.setX(-5000); // Doesn't work in skeleton attack anim, something to do with hit times.. Ideally should call this after a certain amount of time.
            s2.setHealth(s2.getHealth() - 1);
            s2.updateLastHitTime();

            checkEnemyDeath(s2);
        }
    }

    /**
     * Moves the player in and out of the shadow realm.
     */
    private void handlePortalCollision() {
        if (player.isInteracting()) {
            inShadowRealm = !inShadowRealm;
            player.stopInteraction();
            portalUseCount++;
        }
    }

    /**
     * Toggles superhealth mode on and off. Superhealth sets health to 99, does not ignore death from falling off map.
     */
    private void toggleSuperHealth() {
        superHealth = !superHealth;
        if (superHealth) {
            enableSuperHealth();
        } else {
            player.setHealth(player.getHEALTH_MAX());
        }
    }

    /**
     * Sets player's health to 99.
     */
    private void enableSuperHealth() {
        player.setHealth(99);
    }

    /**
     * @return the player Sprite.
     */
    public Character getPlayer() {
        return player;
    }
}