package com.jbs.getout.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jbs.getout.ImageManager;
import com.jbs.getout.states.GameState;

public class Door {
    public static float KNOCK_SIZE = 0.037f;

    public Rectangle rectLeft, rectRight;
    public boolean openStateLeft, openStateRight;
    public float animationPercentRight, animationPercentKnock;

    public Door() {
        rectLeft = new Rectangle(0, 677, 164, 256);    // Left Door Hit Box
        rectRight = new Rectangle(859, 138, 220, 760); // Right Door Hit Box
        openStateLeft = false;
        openStateRight = false;
        animationPercentRight = 0.0f;
        animationPercentKnock = 0.0f;
    }

    public void update() {

        // Left Door Knock Animation //
        if(openStateLeft == false && animationPercentKnock > 0) {
            animationPercentKnock -= .073;
            if(animationPercentKnock <= 0.0)
                animationPercentKnock = 0.0f;
        }

        // Right Door Slide Animation //
        if(animationPercentRight > 0 && animationPercentRight < 1.0) {
            if(!openStateRight)
                animationPercentRight += .017f;
            else
                animationPercentRight -= .017f;

            if(animationPercentRight >= 1.0) {
                animationPercentRight = 1.0f;
                openStateRight = true;
            }
            else if(animationPercentRight <= 0.0) {
                animationPercentRight = 0.0f;
                openStateRight = false;
            }
        }
    }

    public void drawLeft(SpriteBatch spriteBatch) {

        // Open Door //
        if(openStateLeft) {
            if(GameState.npcDoorEventList.size > 0)
                GameState.npcDoorEventList.get(0).draw(spriteBatch);
            spriteBatch.draw(ImageManager.doorOpen, -56, 115);
        }

        // Closed Door //
        else {
            float doorWidth = ImageManager.doorClosed.getWidth();
            float doorHeight = ImageManager.doorClosed.getHeight();
            Vector2 location = new Vector2(-56, 138);
            if(animationPercentKnock > 0) {
                float doorWidthMod = ImageManager.doorClosed.getWidth() * (KNOCK_SIZE * animationPercentKnock);
                float doorHeightMod = ImageManager.doorClosed.getHeight() * (KNOCK_SIZE * animationPercentKnock);
                doorWidth += doorWidthMod;
                doorHeight += doorHeightMod;
                location.x -= (doorWidthMod / 2);
                location.y -= (doorHeightMod / 2);
            }

            spriteBatch.draw(ImageManager.doorClosed, location.x, location.y, doorWidth, doorHeight);
        }
    }

    public void drawRight(SpriteBatch spriteBatch) {
        float doorRightXMod = 0;
        if(animationPercentRight >= 0)
            doorRightXMod = (float) Math.sin(Math.toRadians(animationPercentRight * 90)) * 190;
        spriteBatch.draw(ImageManager.doorRight, 859 + doorRightXMod, 138);
    }

    public void clickLeftDoor() {
        if(GameState.npcDoorEventList.size == 0 || !openStateLeft) {
            openStateLeft = !openStateLeft;
            if(GameState.npcDoorEventList.size > 0 && !GameState.lightSwitch.getIsActive())
                GameState.lightSwitch.setIsActive(true);
        }
    }

    public void clickRightDoor() {

        // Open/Close Right Door //
        if(animationPercentRight == 0.0 || animationPercentRight == 1.0) {
            if(!openStateRight)
                animationPercentRight = 0.01f;
            else
                animationPercentRight = 0.99f;
        }
    }
}
