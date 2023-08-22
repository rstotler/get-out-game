package com.jbs.getout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jbs.getout.gameobjects.GameObject;
import com.jbs.getout.states.GameState;

public class Mouse {
    public Rectangle clickRect;
    public int offsetX, offsetY;

    // Throw Object Variables //
    private int throwIndex;
    private GameObject throwObject;
    public Vector2 lastPosition, lastThrowPosition, throwDirection, lastThrowDirection;
    public int throwStopMovingTimer, longClickIndex;
    public final int THROW_STOP_MOVING_TIMER_MAX = 2;
    public float lastPanX;

    // Constructor //
    public Mouse() {
        lastPosition = new Vector2(0, 0);
        clickRect = new Rectangle(0, 0, 1, 1);
        offsetX = 0;
        offsetY = 0;

        throwIndex = -1;
        lastThrowPosition = new Vector2(0, 0);
        throwDirection = new Vector2(0, 0);
        lastThrowDirection = new Vector2(0, 0);
        throwStopMovingTimer = 0;

        longClickIndex = -1;
        lastPanX = 0.0f;
    }

    public void update(float deltaTime) {
        if(throwObject != null) {

            // Stop Moving Check //
            if(throwStopMovingTimer != -1) {
                if(throwStopMovingTimer < THROW_STOP_MOVING_TIMER_MAX)
                    throwStopMovingTimer += 1;
                else {
                    int xLoc = (int) (Gdx.input.getX(throwIndex) * GetOutGame.WIDTH_RATIO);
                    int yLoc = (int) (GetOutGame.SCREEN_HEIGHT - (Gdx.input.getY(throwIndex) * GetOutGame.HEIGHT_RATIO));
                    lastThrowPosition.set(xLoc, yLoc);
                    throwStopMovingTimer = -1;
                }
            }

            // Update Rotation //
            if(throwObject.rotatePercent >= 0.0) {
                throwObject.rotatePercent += 3.2f * deltaTime;
                if(throwObject.rotatePercent >= 1.0)
                    throwObject.rotatePercent = 0.0f;
            }

            // Duster Clean Check //
            if(throwObject.isDuster) {
                Circle circle = new Circle(throwObject.rect.x - throwObject.rectOffset.x + (ImageManager.objectImageList.get(throwObject.idNum).getWidth() / 2), throwObject.rect.y - throwObject.rectOffset.y + throwObject.dusterRadius, throwObject.dusterRadius);
                GameState.combinedObjectList.clear();
                GameState.combinedObjectList.addAll(GameState.closetObjectList);
                GameState.combinedObjectList.addAll(GameState.fixedObjectList);
                GameState.combinedObjectList.addAll(GameState.wallObjectList);
                for(GameObject gameObject : GameState.combinedObjectList) {
                    if(gameObject != GameState.mouse.getThrowObject() && Intersector.overlaps(circle, gameObject.rectGrab)) {
                        if(gameObject.dirtyPercent > 0.0) {
                            //gameObject.dirtyPercent -= .005 * deltaTime;
                            if(gameObject.dirtyPercent < 0.0)
                                gameObject.dirtyPercent = 0.0f;

                            // Dust Animation //
                            gameObject.dustAnimationCheck();
                        }
                    }
                }
            }
        }
    }

    // Utility Functions //
    public boolean clickFixedObjectCheck(int index) {
        GameState.combinedObjectList.clear();
        if(GameState.door.openStateRight)
            GameState.combinedObjectList.addAll(GameState.closetObjectList);
        GameState.combinedObjectList.addAll(GameState.fixedObjectList);
        for(int i = GameState.combinedObjectList.size - 1; i >= 0; i--) {
            GameObject gameObject = GameState.combinedObjectList.get(i);
            if(clickRect.overlaps(gameObject.rectGrab)
            && !gameObject.noGrab
            && !(gameObject.inCloset && !GameState.door.openStateRight)
            && !(gameObject.attachedTo != null && gameObject.attachedTo.wallState == 0)) {
                setThrowObject(index, gameObject);
                gameObject.setThrowObject();
                return true;
            }
        }

        // Click Wall Object //
        for(GameObject gameObject : GameState.wallObjectList) {
            if(clickRect.overlaps(gameObject.rectGrab)
            && !gameObject.noGrab) {
                setThrowObject(index, gameObject);
                gameObject.setThrowObject();
                return true;
            }
        }

        return false;
    }

    public void dragThrowObject(int x, int y) {
        lastThrowDirection.set(throwDirection.x, throwDirection.y);
        throwDirection.x = (x - lastPosition.x);
        throwDirection.y = (y - lastPosition.y);

        // Change Direction Check //
        if((throwDirection.x >= 0 && lastThrowDirection.x <= 0) || (throwDirection.x <= 0 && lastThrowDirection.x >= 0)
        || (throwDirection.y >= 0 && lastThrowDirection.y <= 0) || (throwDirection.y <= 0 && lastThrowDirection.y >= 0))
            lastThrowPosition.set(x, y);

        throwObject.rect.setPosition(x - offsetX + (GetOutGame.getState().camera.position.x - 270), y - offsetY);
        throwStopMovingTimer = 0;

        // Move Attached Object //
        if(throwObject.attachedObject != null) {
            throwObject.attachedObject.rect.x = throwObject.rect.x + (throwObject.rect.width / 2.0f) - (ImageManager.objectImageList.get(throwObject.attachedObject.idNum).getWidth() / 2.0f);
            throwObject.attachedObject.rect.y = throwObject.rect.y + (throwObject.rect.height / 2.0f) - (ImageManager.objectImageList.get(throwObject.attachedObject.idNum).getHeight() / 2.0f);
        }
    }

    public boolean attachObjectToObjectCheck() {
        boolean actionCheck = false;

        for(GameObject wallObject : GameState.wallObjectList) {
            if(wallObject != throwObject
            && throwObject.attachList.contains(wallObject.idNum, true)
            && wallObject.attachedObject == null
            && throwObject.rect.overlaps(wallObject.rect)) {
                throwObject.attachTo(wallObject);
                throwObject.onFloor = false;
                throwObject.onShelf = false;
                actionCheck = true;
                break;
            }
        }

        // Update Grab Rect //
        throwObject.rectGrab.x = throwObject.rect.x - throwObject.rectOffset.x;
        throwObject.rectGrab.y = throwObject.rect.y - throwObject.rectOffset.y;
        if(throwObject.attachedObject != null) {
            throwObject.attachedObject.rectGrab.x = throwObject.attachedObject.rect.x - throwObject.attachedObject.rectOffset.x;
            throwObject.attachedObject.rectGrab.y = throwObject.attachedObject.rect.y - throwObject.attachedObject.rectOffset.y;
        }

        return actionCheck;
    }

    public boolean putObjectOnWallCheck(double velocityX, double velocityY) {
        for(GameObject wallObject : GameState.wallObjectList) {
            if(throwObject != wallObject && throwObject.rect.overlaps(wallObject.rect))
                return false;
        }

        // Put Object On Wall //
        GameObject dresserObject = GameState.getDresser();
        if((velocityX == 0.0 && velocityY == 0.0)
        && (throwObject.wallState == 0 || throwObject.wallState == 1)
        && GameState.wallRect.contains(throwObject.rect)
        && !throwObject.rect.overlaps(GameState.lightSwitch.rect)
        && !(dresserObject != null && throwObject.rect.overlaps(dresserObject.rect))
        && !(dresserObject != null && throwObject.rect.overlaps(dresserObject.rectBody))) {
            if(throwObject.wallState == 0) {
                throwObject.wallState = 1;
                throwObject.onFloor = false;
                throwObject.onShelf = false;

                GameState.wallObjectList.add(throwObject);
                Utility.removeGameObjectFromList(throwObject, GameState.fixedObjectList);
            }

            // Update Grab Rect //
            throwObject.rectGrab.x = throwObject.rect.x - throwObject.rectOffset.x;
            throwObject.rectGrab.y = throwObject.rect.y - throwObject.rectOffset.y;
            if(throwObject.attachedObject != null) {
                throwObject.attachedObject.rectGrab.x = throwObject.attachedObject.rect.x - throwObject.attachedObject.rectOffset.x;
                throwObject.attachedObject.rectGrab.y = throwObject.attachedObject.rect.y - throwObject.attachedObject.rectOffset.y;
            }

            return true;
        }

        return false;
    }

    public void throwHeldObject(float velocityX, float velocityY) {
        float speedMod = 3.5f;
        throwObject.velocity.set(velocityX * speedMod, velocityY * speedMod);

        throwObject.onFloor = false;
        throwObject.onShelf = false;
        throwObject.inCloset = false;

        Array<GameObject> targetList = GameState.fixedObjectList;
        if(throwObject.wallState == 1) {
            throwObject.wallState = 0;
            targetList = GameState.wallObjectList;
        }

        // Object Clip List Check //
        throwObject.objectClipList.clear();
        for(GameObject fixedObject : GameState.fixedObjectList) {
            if(throwObject != fixedObject && fixedObject.stackable
                    && !throwObject.objectClipList.contains(fixedObject, true)
                    && throwObject.rect.overlaps(fixedObject.rect)
                    && (throwObject.collideShape.equals("Rectangle")
                    || (throwObject.collideShape.equals("Ellipse")
                    && throwObject.rect.x + (throwObject.rect.width / 2.0) >= fixedObject.rect.x
                    && throwObject.rect.x + (throwObject.rect.width / 2.0) < fixedObject.rect.x + fixedObject.rect.width)))
                throwObject.objectClipList.add(fixedObject);
        }

        // Add & Remove Object From Object Lists //
        GameState.movingObjectList.add(throwObject);
        Utility.removeGameObjectFromList(throwObject, targetList);
    }

    // Getters & Setters //
    public int getThrowIndex() { return throwIndex; }
    public GameObject getThrowObject() { return throwObject; }
    public void setThrowObject(int throwIndex, GameObject throwObject) {
        this.throwIndex = throwIndex;
        this.throwObject = throwObject;

        if(throwIndex != -1) {
            offsetX = (int) (clickRect.x - throwObject.rect.x);
            offsetY = (int) (clickRect.y - throwObject.rect.y);

            throwObject.speed.set(0, 0);
            throwObject.velocity.set(0, 0);

            lastPosition.set(clickRect.x, clickRect.y);
            lastThrowPosition.set(clickRect.x, clickRect.y);

            // Attached Object //
            if(throwObject.attachedTo != null) {
                throwObject.attachedTo.attachedObject = null;
                throwObject.attachedTo = null;
            }

            // Trash Can //
            if(throwObject.isTrashCan && throwObject.enteringTrashList.size > 0) {
                for(GameObject enteringObject :  throwObject.enteringTrashList)
                    enteringObject.inTrashCan = null;
                throwObject.enteringTrashList.clear();
            }

            // Duster //
            if(throwObject.isDuster)
                throwObject.rotatePercent = .5f;
        }
    }
}
