package com.jbs.getout.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jbs.getout.GetOutGame;
import com.jbs.getout.ImageManager;
import com.jbs.getout.Utility;
import com.jbs.getout.states.GameState;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class GameObject {
    public static int GAME_ID_COUNTER = 0;
    public int idNum, GAME_ID;
    public String collideShape;
    public Rectangle rect, rectGrab, rectBody;
    public Vector2 speed, velocity, rectOffset;
    public float rotatePercent, dirtyPercent;

    public Array<GameObject> supportedObjectList, supportedByObjectList, objectClipList, enteringTrashList;
    public Array<Integer> attachList;
    public GameObject attachedObject, attachedTo;
    public int wallState; // -1 - Not A Wall Object, 0 - Wall Object (Off Of Wall), 1 - Wall Object On Wall
    public boolean stackable, onFloor, onShelf, inCloset, isClosetShelf, isDresser, isTrashCan, isDuster, isTrash, noGrab, isWindow;
    public GameObject inTrashCan;
    public int trashCanLidOffset, dusterRadius;
    public TextureRegion textureTrashCanClip;

    public int newDustTimer;
    public Array<DustCloud> dustCloudList;
    public boolean dustCloudDeleteCheck;

    // Constructor //
    public GameObject(int idNum, int x, int y) {
        this.idNum = idNum;
        GAME_ID = GAME_ID_COUNTER++;
        collideShape = "Rectangle";

        int width = ImageManager.objectImageList.get(idNum).getWidth();
        int height = ImageManager.objectImageList.get(idNum).getHeight();
        rect = new Rectangle(x, y, width, height);
        rectGrab = new Rectangle(x, y, width, height);
        rectBody = new Rectangle(1, 1, 1, 1);
        rectOffset = new Vector2(0, 0);
        speed = new Vector2(0, 0);
        velocity = new Vector2(0, 0);
        rotatePercent = -1.0f;
        dirtyPercent = 1.00f;

        supportedObjectList = new Array<GameObject>();
        supportedByObjectList = new Array<GameObject>();
        objectClipList = new Array<GameObject>();
        enteringTrashList = new Array<GameObject>();
        attachList = new Array<Integer>();

        wallState = -1;
        stackable = false;
        onFloor = false;
        onShelf = false;
        inCloset = false;
        isClosetShelf = false;
        isDresser = false;
        isTrashCan = false;
        isDuster = false;
        isTrash = false;
        noGrab = false;
        isWindow = false;
        trashCanLidOffset = 0;

        newDustTimer = -1;
        dustCloudList = new Array<DustCloud>();
        dustCloudDeleteCheck = false;

        loadObject();
    }

    private void loadObject() {

        // 1 - Poop //
        if(idNum == 1) { }

        // 2 - Analog Clock //
        else if(idNum == 2) {
            collideShape = "Ellipse";
        }

        // 3 - Picture Frame //
        else if(idNum == 3) { }

        // 4 - Banana //
        else if(idNum == 4) {
            collideShape = "Ellipse";
        }

        // 5 - Calender //
        else if(idNum == 5) { }

        // 6 - Lamp //
        else if(idNum == 6) {
            stackable = true;
            rect.width *= .35;
            rectOffset.x = .32f;
        }

        // 7 - Plaque //
        else if(idNum == 7) {
            collideShape = "Ellipse";
        }

        // 8 - Digital Clock //
        else if(idNum == 8) {
            stackable = true;
        }

        // 9 - Apple //
        else if(idNum == 9) {
            collideShape = "Ellipse";
        }

        // 10 - Fish //
        else if(idNum == 10) {
            collideShape = "Ellipse";
            attachList.add(7);
        }

        // 11 - Trash Can //
        else if(idNum == 11) {
            isTrashCan = true;
            trashCanLidOffset = 107;
            rect.width *= .66;
            rectOffset.x = .17f;
            dirtyPercent = -1.0f;
        }

        // 12 - Paper Ball //
        else if(idNum == 12) {
            isTrash = true;
        }

        // 13 - Duster //
        else if(idNum == 13) {
            isDuster = true;
            dusterRadius = 44;
            rotatePercent = 0.0f;
            dirtyPercent = -1.0f;
            rect.width *= .44;
            rectOffset.x = .30f;
            rect.height *= .75;
            rectOffset.y = .07f;
        }

        // 14 - Dresser //
        else if(idNum == 14) {
            isDresser = true;
            noGrab = true;
            stackable = true;
            dirtyPercent = -1.0f;
            rectBody = new Rectangle(rect.x, rect.y, ImageManager.objectImageList.get(idNum).getWidth(), ImageManager.objectImageList.get(idNum).getHeight() - 1);
            rect.height = 1;
            rect.y = rect.y + ImageManager.objectImageList.get(idNum).getHeight() - 1;
        }

        // 15 - Shelf //
        else if(idNum == 15) {
            isClosetShelf = true;
            stackable = true;
            noGrab = true;
            dirtyPercent = -1.0f;
        }

        // 16 - Window //
        else if(idNum == 16) {
            isWindow = true;
            noGrab = true;
            dirtyPercent = -1.0f;
        }

        // Set Offset //
        if(rectOffset.x != 0)
            rectOffset.x *= ImageManager.objectImageList.get(idNum).getWidth();
        if(rectOffset.y != 0)
            rectOffset.y *= ImageManager.objectImageList.get(idNum).getHeight();
    }

    public void dustAnimationCheck() {
        if(newDustTimer == -1 && dustCloudList.size < DustCloud.MAX_DUST_CLOUDS) {
            dustCloudList.add(new DustCloud(this));
            newDustTimer = new Random().nextInt(DustCloud.MAX_SPAWN_TIME) + DustCloud.MIN_SPAWN_TIME;

            if(!DustCloud.updateList.contains(this, true))
                DustCloud.updateList.add(this);
        }
    }

    public void move(float deltaTime, int index) {
        double oldSpeedY = speed.y;

        // Movement //
        rect.x += speed.x;
        rect.y += speed.y;

        // Friction //
        speed.x *= .985 * deltaTime;
        speed.y *= .985 * deltaTime;

        // Gravity //
        speed.x += velocity.x * deltaTime;
        speed.y += velocity.y * deltaTime;
        velocity.y -= 1750 * deltaTime;

        // Collision Detection - Hit Wall //
        boolean reverseDirection = false;
        if(rect.x - rectOffset.x < 0) {
            rect.x = rectOffset.x;
            reverseDirection = true;
        }
        else if(rect.x + rect.width + rectOffset.x >= GetOutGame.SCREEN_WIDTH * 2) {
            rect.x = (GetOutGame.SCREEN_WIDTH * 2) - rect.width - rectOffset.x;
            reverseDirection = true;
        }

        // Collision Detection - Hit NPC //
        if(GameState.door.openStateLeft && GameState.npcDoorEventList.size > 0 && GameState.npcDoorEventList.get(0).rectBody != null && GameState.npcDoorEventList.get(0).rectHead != null) {
            NPCEvent npcEvent = GameState.npcDoorEventList.get(0);
            Circle gameObjectCircle = null;
            if(collideShape.equals("Ellipse")) {
                float circleRadius = rect.width / 2;
                if(rect.height < rect.width)
                    circleRadius = rect.height / 2;
                gameObjectCircle = new Circle(rect.x + (rect.width / 2), rect.y + (rect.height / 2), circleRadius);
            }

            // Body Shot //
            if((collideShape.equals("Rectangle")
            && npcEvent.rectBody.overlaps(rect))
            || (collideShape.equals("Ellipse")
            && Intersector.overlaps(gameObjectCircle, npcEvent.rectBody)))
                npcEvent.collideWithObject(index, false);

            // Head Shot //
            else if((collideShape.equals("Rectangle")
            && npcEvent.rectHead.overlaps(rect))
            || (collideShape.equals("Ellipse")
            && Intersector.overlaps(gameObjectCircle, npcEvent.rectHead)))
                npcEvent.collideWithObject(index, true);
        }

        // Collision Detection - Inside Trash Can Wall //
        if(inTrashCan != null && rectGrab.width < inTrashCan.rectGrab.width) {
            if(rect.x - rectOffset.x < inTrashCan.rect.x - inTrashCan.rectOffset.x) {
                rect.x = inTrashCan.rect.x - inTrashCan.rectOffset.x + rectOffset.x;
                reverseDirection = true;
            }
            else if(rect.x + rect.width - rectOffset.x >= inTrashCan.rect.x + inTrashCan.rectGrab.width - inTrashCan.rectOffset.x) {
                rect.x = inTrashCan.rect.x + inTrashCan.rectGrab.width - inTrashCan.rectOffset.x - rect.width - rectOffset.x;
                reverseDirection = true;
            }
        }
        if(reverseDirection) {
            velocity.x *= -1;
            speed.x *= -1;
        }

        // (Up Movement) //
        if(oldSpeedY > 0.0) {

            // Object Clip List Check //
            objectClipList.clear();
            GameState.combinedObjectList.clear();
            GameState.combinedObjectList.addAll(GameState.closetObjectList);
            GameState.combinedObjectList.addAll(GameState.fixedObjectList);
            for(GameObject fixedObject : GameState.combinedObjectList) {
                if(fixedObject.stackable && !objectClipList.contains(fixedObject, true)
                && collisionDetection(fixedObject))
                    objectClipList.add(fixedObject);
            }
        }

        // Collision Detection (Down Movement) //
        else if(oldSpeedY < 0.0) {

            // Collision Detection - Delete Object/Update Clip Texture //
            if(inTrashCan != null) {
                if(rect.y - rectOffset.y + rectGrab.height <= inTrashCan.rect.y - inTrashCan.rectOffset.y + inTrashCan.trashCanLidOffset)
                    GameState.movingObjectDeleteList.add(index);
                else
                    updateTrashCanTextures();
            }

            else if(inTrashCan == null) {

                // Collision Detection - Floor //
                float yMod = 0.0f;
                if(isDresser) { yMod = ImageManager.objectImageList.get(idNum).getHeight(); }
                if(rect.y - yMod <= GameState.FLOOR_Y) {
                    rect.y = GameState.FLOOR_Y + yMod;
                    onFloor = true;
                    freeze(this, index);
                }

                // Collision Detection - Objects //
                else {
                    GameState.combinedObjectList.clear();
                    if(GameState.door.openStateRight)
                        GameState.combinedObjectList.addAll(GameState.closetObjectList);
                    GameState.combinedObjectList.addAll(GameState.fixedObjectList);
                    for(GameObject fixedObject : GameState.combinedObjectList) {
                        if(!objectClipList.contains(fixedObject, true)
                        && !(GameState.mouse.getThrowObject() != null && GameState.mouse.getThrowObject() == fixedObject)
                        && rect.y - oldSpeedY >= fixedObject.rect.y + fixedObject.rect.height
                        && collisionDetection(fixedObject)) {

                            // Stackable Objects //
                            if(fixedObject.stackable) {
                                rect.y = fixedObject.rect.y + fixedObject.rect.height - 1;
                                addSupport(fixedObject);
                                if(fixedObject.isDresser) { onShelf = true; }
                                else if(fixedObject.isClosetShelf) { inCloset = true; }
                                freeze(this, index);
                                break;
                            }

                            // Enter Trash Can //
                            else if(fixedObject.isTrashCan && inTrashCan == null
                            && rect.x + (rect.width / 2.0) >= fixedObject.rect.x - fixedObject.rectOffset.x
                            && rect.x + (rect.width / 2.0) < fixedObject.rect.x + fixedObject.rectGrab.width - fixedObject.rectOffset.x) {
                                inTrashCan = fixedObject;
                                fixedObject.enteringTrashList.add(this);
                                textureTrashCanClip = new TextureRegion(ImageManager.objectImageList.get(idNum), 0, 0, 1, 1);
                                updateTrashCanTextures();
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Move Attached Object //
        if(attachedObject != null) {
            attachedObject.rect.x = rect.x + (rect.width / 2.0f) - (ImageManager.objectImageList.get(attachedObject.idNum).getWidth() / 2.0f);
            attachedObject.rect.y = rect.y + (rect.height / 2.0f) - (ImageManager.objectImageList.get(attachedObject.idNum).getHeight() / 2.0f);
        }
    }

    public boolean collisionDetection(GameObject gameObject) {
        return rect.overlaps(gameObject.rect)
        && (collideShape.equals("Rectangle")
        || (collideShape.equals("Ellipse")
        && rect.x + (rect.width / 2.0) >= gameObject.rect.x
        && rect.x + (rect.width / 2.0) < gameObject.rect.x + gameObject.rect.width))
        && !(gameObject.isClosetShelf && rect.x - rectOffset.x < gameObject.rect.x)
        && !(gameObject.isClosetShelf && rect.x - rectOffset.x + rect.width > gameObject.rect.x + gameObject.rect.width)
        && !(gameObject.isClosetShelf && GameState.door.animationPercentRight == 0.0f);
    }

    public void draw(SpriteBatch spriteBatch, boolean drawOverride) {
        int srcFunc = spriteBatch.getBlendSrcFunc();
        int destFunc = spriteBatch.getBlendDstFunc();

        // Draw Object //
        if(attachedTo == null && (GameState.mouse.getThrowObject() != this || drawOverride)) {
            float yLoc = rect.y - rectOffset.y;
            if(isDresser)
                yLoc -= rectGrab.height;
            if(inTrashCan != null && textureTrashCanClip != null) {
                float xLoc = rect.x - rectOffset.x;
                int offsetY = (int) ((inTrashCan.rect.y - inTrashCan.rectOffset.y + inTrashCan.trashCanLidOffset) - (rect.y - rectOffset.y));
                if(offsetY < 0)
                    offsetY = 0;
                spriteBatch.draw(textureTrashCanClip, xLoc, yLoc + offsetY);

                // Object Dirt Layer //
                if(dirtyPercent > 0.0) {
                    spriteBatch.setShader(ImageManager.shaderProgramBlackAndWhite);
                    ImageManager.shaderProgramBlackAndWhite.setUniformf("target_alpha", dirtyPercent);
                    spriteBatch.draw(textureTrashCanClip, xLoc, yLoc + offsetY);
                }
            }
            else {
                spriteBatch.draw(ImageManager.objectImageList.get(idNum), rect.x - rectOffset.x, yLoc);

                // Object Dirt Layer //
                if(dirtyPercent > 0.0) {
                    spriteBatch.setShader(ImageManager.shaderProgramBlackAndWhite);
                    ImageManager.shaderProgramBlackAndWhite.setUniformf("target_alpha", dirtyPercent);
                    spriteBatch.draw(ImageManager.objectImageList.get(idNum), rect.x - rectOffset.x, yLoc);
                }
            }
            spriteBatch.setShader(null);
            spriteBatch.setBlendFunction(srcFunc, destFunc);
            if(idNum == 8) {
                String timeString = GameState.getTimeString();
                int xMod = 13;
                if(timeString.substring(0, 2).equals("1:"))
                    xMod += 11;
                ImageManager.fontDigitalClock.draw(spriteBatch, timeString, rect.x - rectOffset.x + xMod, yLoc + 30);
            }
        }

        // Attached Object //
        if(attachedObject != null) {
            float x = rect.x - rectOffset.x + (rect.width / 2.0f) - (ImageManager.objectImageList.get(attachedObject.idNum).getWidth() / 2.0f);
            float y = rect.y - rectOffset.y + (rect.height / 2.0f) - (ImageManager.objectImageList.get(attachedObject.idNum).getHeight() / 2.0f);
            spriteBatch.draw(ImageManager.objectImageList.get(attachedObject.idNum), x, y);

            // Object Dirt Layer //
            if(dirtyPercent > 0.0) {
                spriteBatch.setShader(ImageManager.shaderProgramBlackAndWhite);
                ImageManager.shaderProgramBlackAndWhite.setUniformf("target_alpha", dirtyPercent);
                spriteBatch.draw(ImageManager.objectImageList.get(attachedObject.idNum), x, y);
            }

            spriteBatch.setShader(null);
            spriteBatch.setBlendFunction(srcFunc, destFunc);
        }
    }

    public void drawFront(SpriteBatch spriteBatch) {
        spriteBatch.draw(ImageManager.objectFrontImageList.get(idNum), rect.x - rectOffset.x, rect.y - rectOffset.y);
    }

    public void drawSwingAnimation(SpriteBatch spriteBatch) {

        // Debug - Draw Dust Radius Circle //
        //Pixmap pmap = new Pixmap(dusterRadius * 2, dusterRadius * 2, Pixmap.Format.RGBA8888);
        //Circle circle = new Circle(rect.x - rectOffset.x + (ImageManager.objectImageList.get(idNum).getWidth() / 2) - dusterRadius, rect.y - rectOffset.y, dusterRadius);
        //pmap.setColor(Color.RED);
        //pmap.fillCircle(dusterRadius, dusterRadius, dusterRadius - 1);
        //Texture circleTexture = new Texture(pmap);
        //spriteBatch.draw(circleTexture, circle.x, circle.y);

        float swingRotationPercent = (float) Math.sin(Math.toRadians(rotatePercent * 360));
        float swingRotation = swingRotationPercent * 22;
        spriteBatch.draw(ImageManager.objectImageList.get(idNum), rect.x - rectOffset.x, rect.y - rectOffset.y, ImageManager.objectImageList.get(idNum).getWidth() / 2, ImageManager.objectImageList.get(idNum).getHeight() / 1.25f, ImageManager.objectImageList.get(idNum).getWidth(), ImageManager.objectImageList.get(idNum).getHeight(), 1, 1, swingRotation, 0, 0, ImageManager.objectImageList.get(idNum).getWidth(), ImageManager.objectImageList.get(idNum).getHeight(), false, false);
    }

    // Utility Functions //
    public boolean isClean() {
        if(wallState == 1
        || (attachedTo != null && attachedTo.wallState == 1)
        || (isTrashCan && onFloor))
            return true;

        else if(!isTrash && wallState != 0
        && !(isTrashCan && !onFloor)
        && !(attachedTo != null && !attachedTo.isClean())
        && !(attachList.size > 0 && attachedTo == null)
        && onShelfCheck())
            return true;

        return false;
    }

    public boolean onShelfCheck() {
        if(onShelf)
            return true;
        else if(supportedByObjectList.size > 0)
            return supportedByObjectList.get(0).onShelfCheck();

        return false;
    }

    public boolean inClosetCheck() {
        if(inCloset)
            return true;
        else if(supportedByObjectList.size > 0)
            return supportedByObjectList.get(0).inClosetCheck();

        return false;
    }

    public void updateTrashCanTextures() {
        int cropX = 0;
        int cropY = 0;
        int cropWidth = (int) rectGrab.width;

        int trashCanDiff = (int) ((inTrashCan.rect.y - inTrashCan.rectOffset.y + inTrashCan.trashCanLidOffset) - (rect.y - rectOffset.y));
        if(trashCanDiff < 0)
            trashCanDiff = 0;
        int cropHeight = (int) (rectGrab.height - trashCanDiff);
        textureTrashCanClip.setRegion(cropX, cropY, cropWidth, cropHeight);
    }

    public static void freeze(GameObject gameObject, int movingObjectIndex) {
        gameObject.rectGrab.x = gameObject.rect.x - gameObject.rectOffset.x;
        gameObject.rectGrab.y = gameObject.rect.y - gameObject.rectOffset.y;
        if(gameObject.isDresser)
            gameObject.rectGrab.y -= ImageManager.objectImageList.get(gameObject.idNum).getHeight();
        gameObject.speed.set(0.0f, 0.0f);
        gameObject.velocity.set(0.0f, 0.0f);

        // Object Clip List Check //
        GameState.combinedObjectList.clear();
        GameState.combinedObjectList.addAll(GameState.fixedObjectList);
        GameState.combinedObjectList.addAll(GameState.closetObjectList);

        gameObject.objectClipList.clear();
        for(GameObject fixedObject : GameState.combinedObjectList) {
            if(gameObject != fixedObject && fixedObject.stackable
            && gameObject.collideShape.equals("Rectangle")
            && gameObject.rect.overlaps(fixedObject.rect))
                gameObject.objectClipList.add(fixedObject);
        }

        Array<GameObject> targetList;
        if(gameObject.inClosetCheck())
            targetList = GameState.closetObjectList;
        else
            targetList = GameState.fixedObjectList;

        targetList.add(gameObject);
        GameState.movingObjectDeleteList.add(movingObjectIndex);

        // Bring Attached Object To Front Of Display //
        if(gameObject.attachedObject != null) {
            Array<GameObject> attachedObjectList;
            if(GameState.closetObjectList.contains(gameObject.attachedObject, true))
                attachedObjectList = GameState.closetObjectList;
            else
                attachedObjectList = GameState.fixedObjectList;

            Utility.removeGameObjectFromList(gameObject.attachedObject, attachedObjectList);
            targetList.add(gameObject.attachedObject);
        }
    }

    public void setThrowObject() {

        // Supported Object Lists //
        if(supportedObjectList.size > 0) {
            for(GameObject supportedObject : supportedObjectList)
                removeSupport(supportedObject);
            supportedObjectList.clear();
        }

        // Objects Supporting THIS Object //
        if(supportedByObjectList.size > 0) {
            for(GameObject supportingObject : supportedByObjectList) {
                int targetIndex = -1;
                for(int i = 0; i < supportingObject.supportedObjectList.size; i++) {
                    if(supportingObject.supportedObjectList.get(i).GAME_ID == GAME_ID) {
                        targetIndex = i;
                        break;
                    }
                }
                if(targetIndex != -1)
                    supportingObject.supportedObjectList.removeIndex(targetIndex);
            }
            supportedByObjectList.clear();
        }
    }

    public void removeSupport(GameObject supportedObject) {
        GameState.movingObjectList.add(supportedObject);
        Utility.removeGameObjectFromList(supportedObject, GameState.fixedObjectList);

        supportedObject.supportedByObjectList.clear();
        if(supportedObject.supportedObjectList.size > 0) {
            for(GameObject subSupportedObject : supportedObject.supportedObjectList)
                removeSupport(subSupportedObject);
            supportedObject.supportedObjectList.clear();
        }
    }

    public void addSupport(GameObject supportingObject) {
        supportingObject.supportedObjectList.add(this);
        supportedByObjectList.add(supportingObject);
    }

    public void attachTo(GameObject targetObject) {
        targetObject.attachedObject = this;
        attachedTo = targetObject;
        rect.x = targetObject.rect.x + (targetObject.rect.width / 2.0f) - (ImageManager.objectImageList.get(idNum).getWidth() / 2.0f);
        rect.y = targetObject.rect.y + (targetObject.rect.height / 2.0f) - (ImageManager.objectImageList.get(idNum).getHeight() / 2.0f);
    }
}
