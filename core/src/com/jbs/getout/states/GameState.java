package com.jbs.getout.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.jbs.getout.GetOutGame;
import com.jbs.getout.ImageManager;
import com.jbs.getout.Mouse;
import com.jbs.getout.UI;
import com.jbs.getout.Utility;
import com.jbs.getout.gameobjects.Door;
import com.jbs.getout.gameobjects.DustCloud;
import com.jbs.getout.gameobjects.GameObject;
import com.jbs.getout.gameobjects.LightSwitch;
import com.jbs.getout.gameobjects.NPCEvent;

import java.util.Random;

// To Do:
// 1 - Fix Dust Clouds Stop Appearing When Dusting Multiple Objects Bug
// 2 - Add Delta Time To Update Functions/Adjust Speeds
// 3 - Attatched Object's Location Not Updating When Moved (Dust Clouds)

public class GameState extends State {
    public static int FLOOR_Y = 60;

    public static Mouse mouse;
    private static ImageManager imageManager;
    public static UI ui;
    public static FrameBuffer frameBuffer;
    public static SpriteBatch maskSpriteBatch;

    public static Rectangle wallRect;
    public static Door door;
    public static GameObject window;
    public static LightSwitch lightSwitch;
    public static Array<GameObject> fixedObjectList, wallObjectList, closetObjectList, movingObjectList, trashCanObjectList, combinedObjectList;
    public static Array<Integer> movingObjectDeleteList;
    public static Array<NPCEvent> npcDoorEventList;

    public static float dayTimer;
    public int dustTimer;

    // Debug //
    //public float sparkleTimer = 0f;

    // Constructor //
    public GameState() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GetOutGame.SCREEN_WIDTH, GetOutGame.SCREEN_HEIGHT);

        mouse = new Mouse();
        imageManager = new ImageManager();
        ui = new UI();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, GetOutGame.SCREEN_WIDTH, GetOutGame.SCREEN_HEIGHT, false);
        maskSpriteBatch = new SpriteBatch();

        wallRect = new Rectangle(247, 173, 586, 800);
        door = new Door();
        lightSwitch = new LightSwitch();

        fixedObjectList = new Array<GameObject>();     // Stationary, Non-Wall Objects
        wallObjectList = new Array<GameObject>();      // Stationary Wall Objects
        closetObjectList = new Array<GameObject>();    // Closet Objects
        movingObjectList = new Array<GameObject>();    // Moving/Thrown Objects
        trashCanObjectList = new Array<GameObject>();  // Trash Can Objects (To Draw Trash Can Front)
        combinedObjectList = new Array<GameObject>();  // Temporary Use
        movingObjectDeleteList = new Array<Integer>(); // Moving Objects -> Stationary Objects
        npcDoorEventList = new Array<NPCEvent>();      // List Of NPCs At Door

        dayTimer = 0.0f;
        dustTimer = 0;

        generateLevel();
    }

    public void generateLevel() {
        mouse.setThrowObject(-1, null);
        fixedObjectList.clear();
        wallObjectList.clear();
        closetObjectList.clear();
        movingObjectList.clear();

        for(int i = 0; i < 1; i++) {

            // Wall Objects //
            addGameObjectToWall(new GameObject(2, 400, 845));  // Clock
            addGameObjectToWall(new GameObject(3, 430, 721));  // Picture
            addGameObjectToWall(new GameObject(5, 275, 675));  // Calendar
            addGameObjectToWall(new GameObject(16, 575, 500)); // Window
            GameObject objectPlaque = new GameObject(7, 320, 560);
            addGameObjectToWall(objectPlaque);

            // Non-Wall Objects //
            //GameObject objectFish = new GameObject(10, 335, 578);
            //objectFish.attachTo(objectPlaque);
            //fixedObjectList.add(objectFish);

            closetObjectList.add(new GameObject(15, 830, 704)); // Closet Shelf (Top)
            closetObjectList.add(new GameObject(15, 830, 512)); // Closet Shelf (Middle)
            closetObjectList.add(new GameObject(15, 830, 128)); // Closet Shelf (Bottom)

            fixedObjectList.add(new GameObject(14, 275, 125)); // Dresser
            movingObjectList.add(new GameObject(8, 360, 540)); // Digital Clock
            movingObjectList.add(new GameObject(6, 480, 540)); // Lamp
            movingObjectList.add(new GameObject(4, 360, 600)); // Banana
            movingObjectList.add(new GameObject(9, 310, 540)); // Apple
            movingObjectList.add(new GameObject(11, 300, 70)); // Trash Can
            movingObjectList.add(new GameObject(13, 400, 70)); // Duster

            // NPC Events //
            npcDoorEventList.add(new NPCEvent(2)); // Dad
        }
    }

    @Override
    public void update(float deltaTime) {
        handleInput();

        mouse.update(deltaTime);
        ui.update();
        door.update();

        dayTimer += .000015;
        if(dayTimer >= 1.0)
            dayTimer = 0.0f;

        // Update Object Dirt Timer //
        if(dustTimer++ > 250) {
            dustTimer = 0;
            combinedObjectList.clear();
            combinedObjectList.addAll(fixedObjectList);
            combinedObjectList.addAll(wallObjectList);
            combinedObjectList.addAll(movingObjectList);
            for(GameObject gameObject : combinedObjectList) {
                if(gameObject.dirtyPercent >= 0.0 && gameObject.dirtyPercent < 1.0)
                    gameObject.dirtyPercent += .05f;
            }
        }

        // Update Moving Objects, Move To/From Lists //
        for(int i = 0; i < movingObjectList.size; i++)
            movingObjectList.get(i).move(deltaTime, i);

        // Dust Clouds //
        DustCloud.updateAll();

        if(movingObjectDeleteList.size > 0) {
            movingObjectDeleteList.sort();
            for(int i = movingObjectDeleteList.size - 1; i >= 0; i--)
                movingObjectList.removeIndex(movingObjectDeleteList.get(i));
            movingObjectDeleteList.clear();
            ui.setCleanlinessMeter();
        }

        // NPC Events //
        if(npcDoorEventList.size > 0) {
            npcDoorEventList.get(0).update();
            if(npcDoorEventList.get(0).doorEnterDir == 2 && npcDoorEventList.get(0).doorEnterTimer <= 0.0)
                npcDoorEventList.removeIndex(0);
        }
    }

    @Override
    protected void handleInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(int x, int y, int index, int button) {
                x *= GetOutGame.WIDTH_RATIO;
                y = (int) (GetOutGame.SCREEN_HEIGHT - (y * GetOutGame.HEIGHT_RATIO));
                mouse.clickRect.setPosition(x + (camera.position.x - 270), y);

                // Click Fixed Object //
                if(mouse.getThrowObject() == null && lightSwitch.getIsActive()
                && mouse.clickFixedObjectCheck(index)) { }

                // Click Light Switch //
                else if(mouse.clickRect.overlaps(lightSwitch.rect) && !(npcDoorEventList.size > 0 && door.openStateLeft))
                    lightSwitch.setIsActive(!lightSwitch.getIsActive());

                // Click Left Door //
                else if(mouse.clickRect.overlaps(door.rectLeft))
                    door.clickLeftDoor();

                // Click Right Door //
                else if(mouse.clickRect.overlaps(door.rectRight))
                    door.clickRightDoor();

                // Start Long-Click //
                else if(mouse.longClickIndex == -1) {
                    mouse.longClickIndex = index;
                    mouse.lastPanX = x;
                }

                return true;
            }

            @Override
            public boolean touchDragged(int x, int y, int index) {
                x *= GetOutGame.WIDTH_RATIO;
                y = (int) (GetOutGame.SCREEN_HEIGHT - (y * GetOutGame.HEIGHT_RATIO));

                // Move Throw Object //
                if(mouse.getThrowIndex() == index && mouse.getThrowObject() != null)
                    mouse.dragThrowObject((int) (x), y);

                // Pan Camera//
                else if(mouse.longClickIndex == index) {
                    float xDiff = (mouse.lastPanX - x) * 2.75f;
                    camera.translate(xDiff, 0);

                    // Edge Check //
                    if(camera.position.x < 270)
                        camera.translate(270 - camera.position.x, 0);
                    else if(camera.position.x > 810)
                        camera.translate(810 - camera.position.x, 0);

                    camera.update();
                    mouse.lastPanX = x;
                }

                mouse.lastPosition.set(x, y);
                return false;
            }

            @Override
            public boolean touchUp(int x, int y, int index, int button) {
                x *= GetOutGame.WIDTH_RATIO;
                y = (int) (GetOutGame.SCREEN_HEIGHT - (y * GetOutGame.HEIGHT_RATIO));
                float velocityX = (x + (camera.position.x - 270)) - (mouse.lastThrowPosition.x + (camera.position.x - 270));
                float velocityY = y - mouse.lastThrowPosition.y;

                if(mouse.getThrowIndex() == index && mouse.getThrowObject() != null) {

                    // Attach Object To Object //
                    if(mouse.getThrowObject().attachList.size > 0 && mouse.attachObjectToObjectCheck())
                        ui.setCleanlinessMeter();

                    // Put Object On Wall //
                    else if(mouse.putObjectOnWallCheck(velocityX, velocityY))
                        ui.setCleanlinessMeter();

                    // Throw GameObject //
                    else
                        mouse.throwHeldObject(velocityX, velocityY);

                    mouse.setThrowObject(-1, null);
                }

                // Release Long-Click //
                else if(mouse.longClickIndex == index)
                    mouse.longClickIndex = -1;

                return true;
            }
        });
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        ScreenUtils.clear(45/255f, 25/255f, 20/255f, 1);
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        // Closet Objects //
        for(GameObject gameObject : closetObjectList)
            gameObject.draw(spriteBatch, false);

        // Background //
        if(window == null)
            spriteBatch.draw(ImageManager.roomBackground, 0, 0);
        else
            drawBackgroundWithWindow(spriteBatch);

        spriteBatch.draw(ImageManager.roomCarpet, 0, 0);
        lightSwitch.draw(spriteBatch);
        door.drawRight(spriteBatch);

        // GameObjects //
        trashCanObjectList.clear();
        for(GameObject gameObject : wallObjectList) {
            gameObject.draw(spriteBatch, false);
            if(gameObject.isTrashCan)
                trashCanObjectList.add(gameObject);
        }
        for(GameObject gameObject : fixedObjectList) {
            gameObject.draw(spriteBatch, false);
            if(gameObject.isTrashCan)
                trashCanObjectList.add(gameObject);
        }

        // Dust Clouds //
        if(DustCloud.updateList.size > 0) {
            for(GameObject gameObject : DustCloud.updateList) {
                for(DustCloud dustCloud : gameObject.dustCloudList)
                    dustCloud.draw(spriteBatch);
            }
        }

        door.drawLeft(spriteBatch);

        for(GameObject gameObject : movingObjectList) {
            gameObject.draw(spriteBatch, false);
            if(gameObject.isTrashCan)
                trashCanObjectList.add(gameObject);
        }
        for(GameObject gameObject : trashCanObjectList)
            gameObject.drawFront(spriteBatch);

        // Mouse (Held) GameObject //
        if(mouse.getThrowObject() != null) {
            if(mouse.getThrowObject().isDuster)
                mouse.getThrowObject().drawSwingAnimation(spriteBatch);
            else
                mouse.getThrowObject().draw(spriteBatch, true);
        }

        // Dark Alpha //
        lightSwitch.drawAlpha(spriteBatch);

        // User Interface //
        ui.draw(spriteBatch);

        // Debug Sparkle //
        //TextureRegion currentFrame = ImageManager.sparkleAnimation.getKeyFrame(sparkleTimer, false);
        //spriteBatch.draw(currentFrame, 150, 150);
        //sparkleTimer += Gdx.graphics.getDeltaTime();

        // Debug Data //
        if(true) {
            ImageManager.fontDebug.draw(spriteBatch, String.valueOf(Gdx.graphics.getFramesPerSecond()), 5, GetOutGame.SCREEN_HEIGHT - 15);
            //ImageManager.fontDebug.draw(spriteBatch, String.valueOf(wallObjectList.size) + ", " + String.valueOf(fixedObjectList.size) + ", " + String.valueOf(movingObjectList.size), 5, GetOutGame.SCREEN_HEIGHT - 15);
            //ImageManager.fontDebug.draw(spriteBatch, String.valueOf(getCleanlinessPercent()), 5, GetOutGame.SCREEN_HEIGHT - 32);
            //ImageManager.fontDebug.draw(spriteBatch, String.valueOf(DustCloud.getSize()), 5, GetOutGame.SCREEN_HEIGHT - 15);
            //int yLoc = GetOutGame.SCREEN_HEIGHT - 15 - 17;
            //combinedObjectList.clear();
            //combinedObjectList.addAll(fixedObjectList);
            //combinedObjectList.addAll(movingObjectList);
            //for(GameObject gameObject : combinedObjectList) {
                //ImageManager.debugFont.draw(spriteBatch, String.valueOf() + ", " + String.valueOf(), 5, yLoc);
                //ImageManager.debugFont.draw(spriteBatch, String.valueOf(gameObject.rect.x) + ", " + String.valueOf(gameObject.rect.y), 5, yLoc);
                //ImageManager.debugFont.draw(spriteBatch, String.valueOf(gameObject.supportedObjectList.size) + ", " + String.valueOf(gameObject.supportedByObjectList.size), 5, yLoc);
                //yLoc -= 17;
            //}
        }

        spriteBatch.end();
    }

    public void drawBackgroundWithWindow(SpriteBatch spriteBatch) {
        spriteBatch.end();

        frameBuffer.begin();
        maskSpriteBatch.setProjectionMatrix(GameState.camera.combined);
        maskSpriteBatch.begin();

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        maskSpriteBatch.draw(ImageManager.maskWindow, window.rect.x + 17, window.rect.y + 12);
        maskSpriteBatch.setBlendFunction(GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_ONE_MINUS_DST_COLOR);
        maskSpriteBatch.draw(ImageManager.roomBackground, 0, 0);

        maskSpriteBatch.end();
        frameBuffer.end();
        spriteBatch.setProjectionMatrix(GameState.camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(frameBuffer.getColorBufferTexture(), (GameState.camera.position.x - 270), 0, GetOutGame.SCREEN_WIDTH, GetOutGame.SCREEN_HEIGHT, 0, 0, 1, 1);
    }

    @Override
    public void dispose() {
        ImageManager.dispose();
        lightSwitch.dispose();
        ui.dispose();
    }

    // Utility Functions //
    public static float getCleanlinessPercent() {
        int objectTotal = fixedObjectList.size + wallObjectList.size + movingObjectList.size;
        int cleanObjectCount = wallObjectList.size;
        for(GameObject gameObject : fixedObjectList) {
            if(gameObject.isClean())
                cleanObjectCount++;
        }

        if(objectTotal > 0) {
//            Gdx.app.log("DebugMessage",String.valueOf(cleanObjectCount) + ", " + String.valueOf(fixedObjectList.size) + ", " + String.valueOf(wallObjectList.size) + ", " + String.valueOf(movingObjectList.size));
            return (cleanObjectCount + 0.0f) / objectTotal;
        }
        else
            return 0.0f;
    }

    public static GameObject getDresser() {
        for(GameObject gameObject : fixedObjectList) {
            if(gameObject.isDresser)
                return gameObject;
        }

        return null;
    }

    public void addGameObjectToWall(GameObject wallObject) {
        wallObject.wallState = 1;
        wallObjectList.add(wallObject);

        if(wallObject.isWindow)
            window = wallObject;
    }

    public static String getTimeString() {
        int currentTotalMinutes = (int) ((16 * 60) * dayTimer);
        int currentHour = 8 + (currentTotalMinutes / 60);
        if(currentHour > 12)
            currentHour -= 12;
        int currentMinutes = currentTotalMinutes % 60;

        String currentHourString = String.valueOf(currentHour);
        String currentMinuteString = String.valueOf(currentMinutes);
        if(currentMinuteString.length() == 1)
            currentMinuteString = "0" + currentMinuteString;

        return currentHourString + ":" + currentMinuteString;
    }
}
