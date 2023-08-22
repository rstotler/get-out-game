package com.jbs.getout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;

public class ImageManager {
    public static Texture roomBackground, roomCarpet, doorClosed, doorOpen, doorRight, closet, dustCloud, speechBubble, darkAlphaFull;
    public static Texture maskWindow;
    public static Array<Texture> objectImageList, objectFrontImageList, npcDoorImageList, npcRoomImageList;

    public static Texture sparkleSheet;
    public static Animation<TextureRegion> sparkleAnimation;

    public static BitmapFont fontSpeechBubble, fontDigitalClock, fontToDoList, fontDebug;
    public static ShaderProgram shaderProgramBlackAndWhite, shaderProgramAlpha;

    public ImageManager() {

        // Textures //
        roomBackground = new Texture("images/room/room_background.png");
        roomCarpet = new Texture("images/room/room_carpet.png");
        doorClosed = new Texture("images/room/door_closed.png");
        doorOpen = new Texture("images/room/door_open.png");
        doorRight = new Texture("images/room/door_right.png");
        closet = new Texture("images/room/closet.png");
        dustCloud = new Texture("images/room/dust_cloud.png");
        speechBubble = new Texture("images/room/speech_bubble.png");
        darkAlphaFull = new Texture("images/room/dark_alpha_full.png");
        maskWindow = new Texture(Gdx.files.internal("images/mask/window.png"));

        // GameObjects //
        objectImageList = new Array<Texture>();
        objectFrontImageList = new Array<Texture>();
        int objectCount = Gdx.files.internal("images/object").list().length;
        String stringIdNum;
        for(int i = 0; i < objectCount + 1; i++) {
            stringIdNum = String.valueOf(i);
            if(i < 10)
                stringIdNum = "00" + stringIdNum;
            else if(i < 100)
                stringIdNum = "0" + stringIdNum;

            if(i == 0) {
                objectImageList.add(null);
                objectFrontImageList.add(null);
            }
            else {
                if(Gdx.files.internal("images/object/" + stringIdNum + ".png").exists())
                    objectImageList.add(new Texture("images/object/" + stringIdNum + ".png"));

                if(Gdx.files.internal("images/object/" + stringIdNum + "_front.png").exists())
                    objectFrontImageList.add(new Texture("images/object/" + stringIdNum + "_front.png"));
                else
                    objectFrontImageList.add(null);
            }
        }

        // NPCs //
        npcDoorImageList = new Array<Texture>();
        npcRoomImageList = new Array<Texture>();
        int npcCount = Gdx.files.internal("images/npc").list().length;
        for(int i = 0; i < npcCount + 1; i++) {
            stringIdNum = String.valueOf(i);
            if(i < 10)
                stringIdNum = "0" + stringIdNum;

            if(i == 0) {
                npcDoorImageList.add(null);
                npcRoomImageList.add(null);
            }
            else {
                if(Gdx.files.internal("images/npc/" + stringIdNum + "_door.png").exists())
                    npcDoorImageList.add(new Texture("images/npc/" + stringIdNum + "_door.png"));
                else
                    npcDoorImageList.add(null);

                if(Gdx.files.internal("images/npc/" + stringIdNum + "_room.png").exists())
                    npcRoomImageList.add(new Texture("images/npc/" + stringIdNum + "_room.png"));
                else
                    npcRoomImageList.add(null);
            }
        }

        // Animation //
        sparkleSheet = new Texture(Gdx.files.internal("images/room/sparkle_animation.png"));
        TextureRegion[][] tmp = TextureRegion.split(sparkleSheet, 64, 64);
        TextureRegion[] sparkleFrames = new TextureRegion[15];
        for(int i = 0; i < 15; i++)
            sparkleFrames[i] = tmp[0][i];
        sparkleAnimation = new Animation<TextureRegion>(0.033f, sparkleFrames);

        // Fonts //
        fontSpeechBubble = new BitmapFont(Gdx.files.internal("fonts/wiggly_curves_44.fnt"), Gdx.files.internal("fonts/wiggly_curves_44.png"), false );
        fontSpeechBubble.setColor(Color.BLACK);
        fontDigitalClock = new BitmapFont(Gdx.files.internal("fonts/digital_7_32.fnt"), Gdx.files.internal("fonts/digital_7_32.png"), false );
        fontDigitalClock.setColor(Color.GREEN);
        fontToDoList = new BitmapFont(Gdx.files.internal("fonts/coming_soon_14.fnt"), Gdx.files.internal("fonts/coming_soon_14.png"), false );
        fontToDoList.setColor(Color.BLACK);
        fontDebug = new BitmapFont();
        fontDebug.setColor(Color.RED);

        // Shaders //
        String vertexShader = Gdx.files.internal("shaders/vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("shaders/black_and_white.glsl").readString();
        shaderProgramBlackAndWhite = new ShaderProgram(vertexShader, fragmentShader);
        fragmentShader = Gdx.files.internal("shaders/alpha.glsl").readString();
        shaderProgramAlpha = new ShaderProgram(vertexShader, fragmentShader);
    }

    public static void dispose() {
        roomBackground.dispose();
        roomCarpet.dispose();
        doorClosed.dispose();
        doorOpen.dispose();
        doorRight.dispose();
        closet.dispose();
        dustCloud.dispose();
        speechBubble.dispose();
        darkAlphaFull.dispose();
        maskWindow.dispose();

        for(int i = 0; i < objectImageList.size; i++) {
            if(objectImageList.get(i) != null)
                objectImageList.get(i).dispose();
        }
        for(int i = 0; i < objectFrontImageList.size; i++) {
            if(objectFrontImageList.get(i) != null)
                objectFrontImageList.get(i).dispose();
        }
        for(int i = 0; i < npcDoorImageList.size; i++) {
            if(npcDoorImageList.get(i) != null)
                npcDoorImageList.get(i).dispose();
        }
        for(int i = 0; i < npcRoomImageList.size; i++) {
            if(npcRoomImageList.get(i) != null)
                npcRoomImageList.get(i).dispose();
        }

        sparkleSheet.dispose();

        fontSpeechBubble.dispose();
        fontDigitalClock.dispose();
        fontToDoList.dispose();
        fontDebug.dispose();

        shaderProgramBlackAndWhite.dispose();
        shaderProgramAlpha.dispose();
    }
}
