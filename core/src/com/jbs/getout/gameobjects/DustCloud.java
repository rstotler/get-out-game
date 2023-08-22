package com.jbs.getout.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jbs.getout.ImageManager;
import com.jbs.getout.states.GameState;

import java.util.Random;

public class DustCloud {
    public static int MOVE_DISTANCE = 50;
    public static int MAX_DUST_CLOUDS = 8;
    public static int MIN_SPAWN_TIME = 10;
    public static int MAX_SPAWN_TIME = 40;
    public static Array<GameObject> updateList = new Array<GameObject>();
    public static Array<Integer> deleteList = new Array<Integer>();

    public Vector2 position;
    public float width, height, movePercent;
    public int moveDir;

    // Constructor //
    public DustCloud(GameObject parentObject) {
        float sizeMod = 1.0f + (new Random().nextFloat() * (1.7f - 1.0f));
        width = ImageManager.dustCloud.getWidth() * sizeMod;
        height = ImageManager.dustCloud.getHeight() * sizeMod;

        float xMin = GameState.mouse.getThrowObject().rect.x - GameState.mouse.getThrowObject().rectOffset.x - (width / 2);
        xMin += (GameState.mouse.getThrowObject().rect.x - GameState.mouse.getThrowObject().rectOffset.x + (ImageManager.objectImageList.get(GameState.mouse.getThrowObject().idNum).getWidth() / 2) - GameState.mouse.getThrowObject().dusterRadius) - (GameState.mouse.getThrowObject().rect.x - GameState.mouse.getThrowObject().rectOffset.x);
        if(xMin < parentObject.rectGrab.x - (width / 2))
            xMin = parentObject.rectGrab.x - (width / 2);
        float xMax = GameState.mouse.getThrowObject().dusterRadius * 2.0f;
        if(GameState.mouse.getThrowObject().rect.x - GameState.mouse.getThrowObject().rectOffset.x < parentObject.rectGrab.x)
            xMax -= parentObject.rectGrab.x - (GameState.mouse.getThrowObject().rect.x - GameState.mouse.getThrowObject().rectOffset.x);
        else if(GameState.mouse.getThrowObject().rect.x - GameState.mouse.getThrowObject().rectOffset.x + GameState.mouse.getThrowObject().rectGrab.width > parentObject.rectGrab.x + parentObject.rectGrab.width)
            xMax -= (GameState.mouse.getThrowObject().rect.x - GameState.mouse.getThrowObject().rectOffset.x + GameState.mouse.getThrowObject().rectGrab.width) - (parentObject.rectGrab.x + parentObject.rectGrab.width);
        if(xMax < 1) { xMax = 1; }

        float yMin = GameState.mouse.getThrowObject().rect.y - GameState.mouse.getThrowObject().rectOffset.y - (height / 2);
        if(yMin < parentObject.rectGrab.y - (height / 2))
            yMin = parentObject.rectGrab.y - (height / 2);
        float yMax = GameState.mouse.getThrowObject().dusterRadius * 1.6f;
        if(GameState.mouse.getThrowObject().rect.y - GameState.mouse.getThrowObject().rectOffset.y < parentObject.rectGrab.y)
            yMax -= parentObject.rectGrab.y - (GameState.mouse.getThrowObject().rect.y - GameState.mouse.getThrowObject().rectOffset.y);
        else if(GameState.mouse.getThrowObject().rect.y - GameState.mouse.getThrowObject().rectOffset.y + (GameState.mouse.getThrowObject().dusterRadius * 2.0f) > parentObject.rectGrab.y + parentObject.rectGrab.height)
            yMax -= (GameState.mouse.getThrowObject().rect.y - GameState.mouse.getThrowObject().rectOffset.y + (GameState.mouse.getThrowObject().dusterRadius * 2.0f)) - (parentObject.rectGrab.y + parentObject.rectGrab.height);
        if(yMax < 1) { yMax = 1; }

        float xLoc = (float) new Random().nextInt((int) xMax) + xMin;
        float yLoc = (float) new Random().nextInt((int) yMax) + yMin;
        position = new Vector2(xLoc, yLoc);

        movePercent = 0.0f;
        moveDir = new Random().nextInt(2);
        if(moveDir == 0) { moveDir = -1; }
    }

    public static void updateAll() {
        if(DustCloud.updateList.size > 0) {
            deleteList.clear();
            for(int i = 0; i < DustCloud.updateList.size; i++) {
                GameObject gameObject = DustCloud.updateList.get(i);

                // Update Dust Clouds //
                for(DustCloud dustCloud : gameObject.dustCloudList) {
                    dustCloud.update();
                    if(dustCloud.movePercent >= 1)
                        gameObject.dustCloudDeleteCheck = true;
                }

                // Delete Dust Clouds //
                if(gameObject.dustCloudDeleteCheck) {
                    gameObject.dustCloudList.removeIndex(0);
                    gameObject.dustCloudDeleteCheck = false;
                    if(gameObject.dustCloudList.size == 0 && DustCloud.updateList.contains(gameObject, true))
                        deleteList.add(i);
                }

                // Tick New Dust Timer //
                if(gameObject.newDustTimer > -1)
                    gameObject.newDustTimer--;
            }

            if(deleteList.size > 0) {
                for(int i = deleteList.size - 1; i >= 0; i--)
                    updateList.removeIndex(i);
            }
        }
    }

    public void update() {
        if(movePercent < 1) {
            movePercent += .0075;
            if(movePercent > 1)
                movePercent = 1;
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.setShader(ImageManager.shaderProgramAlpha);
        ImageManager.shaderProgramAlpha.setUniformf("target_alpha", 1 - movePercent);

        float xMod = (float) (Math.sin(Math.toRadians(movePercent * 90)) * MOVE_DISTANCE * moveDir);
        spriteBatch.draw(ImageManager.dustCloud, position.x + xMod, position.y, width, height);

        spriteBatch.setShader(null);
    }

    public static int getSize() {
        int size = 0;
        for(GameObject gameObject : updateList)
            size += gameObject.dustCloudList.size;

        return size;
    }
}
