package com.jbs.getout.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.jbs.getout.GetOutGame;
import com.jbs.getout.ImageManager;
import com.jbs.getout.UI;
import com.jbs.getout.states.GameState;

public class NPCEvent {
    public int idNum;
    public Rectangle rectBody, rectHead;

    public float percentHealth, healthMeterDisplayDifferenceTotal, healthMeterDisplayPercent, healthMeterDisplayDifference, healthMeterIncrementPercent, healthMeterDisplayDir;
    public float percentAnger, angerMeterDisplayDifferenceTotal, angerMeterDisplayPercent, angerMeterDisplayDifference, angerMeterIncrementPercent, angerMeterDisplayDir;
    public boolean displayHealthAngerMeter;

    public float doorKnockTimer, doorKnockWaitTimer, doorEnterTimer, speechBubbleTimer;
    public int doorKnockCount;

    // Constructor //
    public NPCEvent(int idNum) {
        this.idNum = idNum;

        displayHealthAngerMeter = true;
        percentHealth = 1.0f;
        healthMeterDisplayPercent = percentHealth;
        percentAnger = 0.0f;
        angerMeterDisplayPercent = percentAnger;

        doorKnockTimer = 1.0f;
        doorKnockWaitTimer = 0.0f;
        doorEnterTimer = 0.0f;
        doorKnockCount = 0;

        speechBubbleTimer = -1.0f;
    }

    public void update() {

        // Door Open //
        if(GameState.door.openStateLeft) {
            if(doorEnterTimer < 1.0) {
                doorEnterTimer += .05;
                if(doorEnterTimer >= 1.0) {
                    rectBody = new Rectangle(190, 435, 50, 280);
                    rectHead = new Rectangle(240, 690, 80, 85);
                    speechBubbleTimer = 0.0f;
                }
            }

            // Speech Bubble Animation //
            if(speechBubbleTimer >= 0.0 && speechBubbleTimer < 1.0) {
                speechBubbleTimer += .02;
                if(speechBubbleTimer >= 1.0)
                    speechBubbleTimer = 0.0f;
            }

            // Health/Anger Meter //
            if(displayHealthAngerMeter) {

                // Update Meter //
                float oldHealthMeterDisplayDifference = healthMeterDisplayDifference;
                if((healthMeterDisplayDir == 1 && healthMeterDisplayDifference > 0.00005)
                || (healthMeterDisplayDir == -1 && healthMeterDisplayDifference < 0.00005)) {
                    healthMeterDisplayPercent += healthMeterDisplayDifferenceTotal * healthMeterIncrementPercent;
                    healthMeterDisplayDifference -= healthMeterDisplayDifferenceTotal * healthMeterIncrementPercent;
                    healthMeterIncrementPercent *= .975;
                }
                if((angerMeterDisplayDir == 1 && angerMeterDisplayDifference > 0.00005)
                || (angerMeterDisplayDir == -1 && angerMeterDisplayDifference < 0.00005)) {
                    angerMeterDisplayPercent += angerMeterDisplayDifferenceTotal * angerMeterIncrementPercent;
                    angerMeterDisplayDifference -= angerMeterDisplayDifferenceTotal * angerMeterIncrementPercent;
                    angerMeterIncrementPercent *= .975;
                }

                // Quarter-Percent Health Check //
//                if(healthMeterDisplayDifference % .25f > oldHealthMeterDisplayDifference % .25f)
//                    Gdx.app.log("DebugMessage", "HI");
                    Gdx.app.log("DebugMessage", String.valueOf(healthMeterDisplayDifference % .25f));
                    Gdx.app.log("DebugMessage", String.valueOf(oldHealthMeterDisplayDifference % .25f));
            }
        }

        // Door Closed //
        else {
            if(doorKnockCount >= 3) {
                doorKnockWaitTimer += .01;
                if(doorKnockWaitTimer >= 1.0) {
                    doorKnockWaitTimer = 0.0f;
                    doorKnockCount = 0;
                }
            }
            else {
                doorKnockTimer += .075;
                if (doorKnockTimer >= 1.0) {
                    GameState.door.animationPercentKnock = .99f;
                    doorKnockTimer = 0.0f;
                    doorKnockCount++;
                }
            }
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        float npcXMod = (float) Math.sin(Math.toRadians(doorEnterTimer * 90)) * 190;
        float xLoc = 179 - ImageManager.npcDoorImageList.get(idNum).getWidth() + npcXMod;
        spriteBatch.draw(ImageManager.npcDoorImageList.get(idNum), xLoc, 125);

        // Debug - Draw Hit Boxes //
        //if(rectBody != null && rectHead != null) {
        //    spriteBatch.end();
        //    ShapeRenderer shapeRenderer = new ShapeRenderer();
        //    shapeRenderer.setProjectionMatrix(spriteBatch.getProjectionMatrix());
        //    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        //    shapeRenderer.setColor(Color.RED);
        //    shapeRenderer.rect(rectBody.x, rectBody.y, rectBody.width, rectBody.height);
        //    shapeRenderer.rect(rectHead.x, rectHead.y, rectHead.width, rectHead.height);
        //    shapeRenderer.end();
        //    spriteBatch.begin();
        //}

        // Speech Bubble //
        if(speechBubbleTimer >= 0.0) {
            double speechBubbleGrowPercent = Math.sin(Math.toRadians(speechBubbleTimer * 180));
            int speechBubbleOffset = (int) (speechBubbleGrowPercent * 10);
            spriteBatch.draw(ImageManager.speechBubble, 319 - speechBubbleOffset, 675 - speechBubbleOffset, ImageManager.speechBubble.getWidth() + (speechBubbleOffset * 2), ImageManager.speechBubble.getHeight() + (speechBubbleOffset * 2));

            ImageManager.fontSpeechBubble.draw(spriteBatch, "CLEAN", 375, 811);
            ImageManager.fontSpeechBubble.draw(spriteBatch, "YOUR", 385, 773);
            ImageManager.fontSpeechBubble.draw(spriteBatch, "ROOM!", 375, 735);
        }

        // Health/Anger Meter //
        if(displayHealthAngerMeter) {
            spriteBatch.draw(UI.angerBarBottom, 290 + xLoc, 120 + ImageManager.npcDoorImageList.get(idNum).getHeight());

            // Health Meter (Top Fill)
            spriteBatch.end();
            UI.frameBuffer.begin();
            UI.maskSpriteBatch.setProjectionMatrix(GameState.camera.combined);
            UI.maskSpriteBatch.begin();

            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            float angerBarTopDisplayWidth = -UI.angerBarFillTop.getWidth() + (UI.angerBarFillTop.getWidth() * healthMeterDisplayPercent);
            UI.maskSpriteBatch.draw(UI.maskAngerBarFillTop, 289 + xLoc + UI.angerBarFillTop.getWidth() + 1, 122 + ImageManager.npcDoorImageList.get(idNum).getHeight(), angerBarTopDisplayWidth, UI.angerBarFillTop.getHeight());
            UI.maskSpriteBatch.setBlendFunction(GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_ONE_MINUS_DST_COLOR);
            UI.maskSpriteBatch.draw(UI.angerBarFillTop, 290 + xLoc, 122 + ImageManager.npcDoorImageList.get(idNum).getHeight());

            UI.maskSpriteBatch.end();
            UI.frameBuffer.end();
            spriteBatch.setProjectionMatrix(GameState.camera.combined);
            spriteBatch.begin();
            spriteBatch.draw(UI.frameBuffer.getColorBufferTexture(), (GameState.camera.position.x - 270), 0, GetOutGame.SCREEN_WIDTH, GetOutGame.SCREEN_HEIGHT, 0, 0, 1, 1);

            // Health Meter (Bottom Fill)
            spriteBatch.end();
            UI.frameBuffer.begin();
            UI.maskSpriteBatch.setProjectionMatrix(GameState.camera.combined);
            UI.maskSpriteBatch.begin();

            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            float angerBarBottomDisplayWidth = -UI.angerBarFillBottom.getWidth() + (UI.angerBarFillBottom.getWidth() * angerMeterDisplayPercent);
            UI.maskSpriteBatch.draw(UI.maskAngerBarFillBottom, 289 + xLoc + UI.angerBarFillBottom.getWidth() + 1, 120 + ImageManager.npcDoorImageList.get(idNum).getHeight(), angerBarBottomDisplayWidth, UI.angerBarFillBottom.getHeight());
            UI.maskSpriteBatch.setBlendFunction(GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_ONE_MINUS_DST_COLOR);
            UI.maskSpriteBatch.draw(UI.angerBarFillBottom, 290 + xLoc, 120 + ImageManager.npcDoorImageList.get(idNum).getHeight());

            UI.maskSpriteBatch.end();
            UI.frameBuffer.end();
            spriteBatch.setProjectionMatrix(GameState.camera.combined);
            spriteBatch.begin();
            spriteBatch.draw(UI.frameBuffer.getColorBufferTexture(), (GameState.camera.position.x - 270), 0, GetOutGame.SCREEN_WIDTH, GetOutGame.SCREEN_HEIGHT, 0, 0, 1, 1);
        }
    }

    // Utility Functions //
    public void collideWithObject(int objectIndex, boolean headShot) {
        percentHealth -= .15;
        if(percentHealth < 0.0)
            percentHealth = 0.0f;
        setMeter("Health");

        percentAnger += .20;
        if(percentAnger > 1.0)
            percentAnger = 1.0f;
        setMeter("Anger");

        GameState.movingObjectDeleteList.add(objectIndex);
    }

    public void setMeter(String targetMeterString) {
        displayHealthAngerMeter = true;

        if(targetMeterString.equals("Health")) {
            healthMeterDisplayDifferenceTotal = percentHealth - healthMeterDisplayPercent;
            healthMeterDisplayDifference = healthMeterDisplayDifferenceTotal;
            if(healthMeterDisplayDifference > 0.0)
                healthMeterDisplayDir = 1;
            else
                healthMeterDisplayDir = -1;
            healthMeterIncrementPercent = .025f;
        }
        else if(targetMeterString.equals("Anger")) {
            angerMeterDisplayDifferenceTotal = percentAnger - angerMeterDisplayPercent;
            angerMeterDisplayDifference = angerMeterDisplayDifferenceTotal;
            if(angerMeterDisplayDifference > 0.0)
                angerMeterDisplayDir = 1;
            else
                angerMeterDisplayDir = -1;
            angerMeterIncrementPercent = .025f;
        }
    }
}
