package com.jbs.getout.gameobjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.jbs.getout.ImageManager;

public class LightSwitch {
    private boolean isActive;
    public Rectangle rect;

    private Texture lightswitchOn, lightswitchOff;

    public LightSwitch() {
        lightswitchOn = new Texture("images/room/lightswitch_on.png");
        lightswitchOff = new Texture("images/room/lightswitch_off.png");

        isActive = true;
        rect = new Rectangle(274, 526, lightswitchOn.getWidth(), lightswitchOn.getHeight());
    }

    public void draw(SpriteBatch spriteBatch) {
        if(isActive)
            spriteBatch.draw(lightswitchOn, rect.x, rect.y);
        else
            spriteBatch.draw(lightswitchOff, rect.x, rect.y);
    }

    public void drawAlpha(SpriteBatch spriteBatch) {
        if(!isActive)
            spriteBatch.draw(ImageManager.darkAlphaFull, 0, 0);
    }

    public void dispose() {
        lightswitchOn.dispose();
        lightswitchOff.dispose();
    }

    // Getters & Setters //
    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
}
