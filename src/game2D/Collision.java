package game2D;

import Driver.Game;

import java.io.Serializable;

public class Collision implements Serializable {
    private final Game game;

    public Collision(Game game) {
        this.game = game;
    }

    /**
     * Check whether sprite1 has collided with sprite2.
     *
     * @param sprite1 Sprite.
     * @param sprite2 Sprite.
     */
    public void checkSpriteCollision(Sprite sprite1, Sprite sprite2) {
        float sprite1Y = sprite1.getY();
        float sprite2Y = sprite2.getY();
        int sprite2Height = sprite2.getHeight();
        int sprite1Height = sprite1.getHeight();
        // Check if sprite1 is within the Y-axis range of sprite2.
        if ((sprite1Y >= sprite2Y && sprite1Y <= sprite2Y + sprite2Height
                || sprite1Y + sprite1Height >= sprite2Y && sprite1Y + sprite1Height < sprite2Y + sprite2Height)) {

            // Check right side collision
            float sprite1X = sprite1.getX();
            float sprite2X = sprite2.getX();
            int sprite2Width = sprite2.getWidth();
            if (sprite1X > sprite2X && sprite1X < sprite2X + sprite2Width) {

                if (sprite1 instanceof Character && sprite2 instanceof Character) {
                    if (sprite1 == game.getPlayer() && ((Character) sprite2).getHealth() > 0) {

                        if (System.currentTimeMillis() - game.getPlayer().getLastHitTime() > 800) {
                            game.getPlayer().setHealth(game.getPlayer().getHealth() - 1);
                            game.getPlayer().setVelocityX(-game.getPlayer().getVelocityX());
                            game.getPlayer().setX(sprite2X + sprite2Width + 10);
                            game.playerHitNoise();
                            game.getPlayer().updateLastHitTime();
                        }
                    } else {
                        sprite1.setX(sprite2X + sprite2Width + 1);
                        sprite1.setVelocityX(-sprite1.getVelocityX());
                    }
                } else {
                    game.nonCharacterCollision(sprite1, sprite2);
                }
            } else {// Check left side collision
                int sprite1Width = sprite1.getWidth();
                if (sprite1X + sprite1Width > sprite2X && sprite1X < sprite2X) {

                    if (sprite1 instanceof Character && sprite2 instanceof Character) {
                        if (sprite1 == game.getPlayer() && ((Character) sprite2).getHealth() > 0) {

                            if (System.currentTimeMillis() - game.getPlayer().getLastHitTime() > 800) {
                                game.getPlayer().setHealth(game.getPlayer().getHealth() - 1);
                                game.getPlayer().setVelocityX(-game.getPlayer().getVelocityX());
                                game.getPlayer().setX(sprite2X - game.getPlayer().getWidth() - 10);
                                game.playerHitNoise();
                                game.getPlayer().updateLastHitTime();
                            }
                        } else {
                            sprite1.setX(sprite2X - sprite1Width - 1);
                            sprite1.setVelocityX(-sprite1.getVelocityX());
                        }

                    } else game.nonCharacterCollision(sprite1, sprite2);
                }
            }
        }
    }

    /**
     * Check and handles collisions with a tile map for the given sprite 's'.
     *
     * @param s    The Sprite to check collisions for
     * @param tmap The TileMap to check
     */
    public void checkTileCollision(Sprite s, TileMap tmap) {
        // Find out how wide and how tall a tile is
        float tileWidth = tmap.getTileWidth();
        float tileHeight = tmap.getTileHeight();

        // boolean variables for each corner (T = Top, B = Bottom | L = Left, R = Right)
        boolean collisionTL = false;
        boolean collisionBL = false;
        boolean collisionTR = false;
        boolean collisionBR = false;

        // Take a note of a sprite's current position
        float spriteX = s.getX();
        float spriteY = s.getY();
        int spriteWidth = s.getWidth();

        // Divide the sprite’s x coordinate by the width of a tile to get the tile that the sprite is positioned at.
        int xtile = (int) (spriteX / tileWidth);
        int ytile = (int) (spriteY / tileHeight);

        // What tile character is at the top left of the sprite s?
        char ch = tmap.getTileChar(xtile, ytile);

        if (ch != '.') {
            collisionTL = true;
        }

        // Top right corner collision
        xtile = (int) ((spriteX + spriteWidth) / tileWidth);
        ch = tmap.getTileChar(xtile, ytile);
        if (ch != '.') {
            collisionTR = true;
        }

        // Bottom left corner collision
        xtile = (int) (spriteX / tileWidth);
        ytile = (int) ((spriteY + s.getHeight()) / tileHeight);
        ch = tmap.getTileChar(xtile, ytile);

        // If it's not empty space
        if (ch != '.') {
            collisionBL = true;
        }

        // Bottom right corner collision
        xtile = (int) ((spriteX + spriteWidth) / tileWidth);
        ch = tmap.getTileChar(xtile, ytile);

        if (ch != '.') {
            collisionBR = true;
        }

        if (collisionTL && collisionBL) {

            s.stop();
            if (s instanceof Projectile) {
                s.setX(-5000);
            } else {
                s.setX(spriteX + 3);
            }

        } else if (collisionTR && collisionBR) {

            s.stop();
            if (s instanceof Projectile) {
                s.setX(-5000);
            } else {
                s.setX(spriteX - 3);
            }

        } else if (collisionTL) {

            s.stop();
            if (s instanceof Projectile) {
                s.setX(-5000);
            }
            s.setY(spriteY + 2);

        } else if (collisionBL) {

            s.stop();
            if (s instanceof Projectile) {
                s.setX(-5000);
            }
            s.setY(spriteY - 1);
            if (s == game.getPlayer()) {
                game.getPlayer().setJumping(false);
            }

        } else if (collisionTR) {

            s.stop();
            if (s instanceof Projectile) {
                s.setX(-5000);
            } else {
                s.setY(spriteY + 2);
            }

        } else if (collisionBR) {

            s.stop();
            if (s instanceof Projectile) {
                s.setX(-5000);
            }
            s.setY(spriteY - 1);
            if (s == game.getPlayer()) {
                game.getPlayer().setJumping(false);
            }
        }
    }
}