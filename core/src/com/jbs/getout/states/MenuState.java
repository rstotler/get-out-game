package com.jbs.getout.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.jbs.getout.GetOutGame;

public class MenuState extends State {

    // Variables //
    private Texture textureSpeechBubble, textureSpeechBubbleLarge;
    private int speechBubbleX, speechBubbleY, speechBubbleOffset;
    private int timerSpeechBubble, timerSpeechBubbleMax, speechBubbleGrowSize;
    private double speechBubbleGrowPercent;
    private BitmapFont font;

    // Constructor //
    public MenuState() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GetOutGame.SCREEN_WIDTH, GetOutGame.SCREEN_HEIGHT);

        textureSpeechBubble = new Texture("images/room/speech_bubble.png");
        textureSpeechBubbleLarge = new Texture("images/room/speech_bubble_large.png");

        speechBubbleX = (GetOutGame.SCREEN_WIDTH / 2) - (textureSpeechBubbleLarge.getWidth() / 2);
        speechBubbleY = 570;
        speechBubbleOffset = 0;

        timerSpeechBubble = 0;
        timerSpeechBubbleMax = 120;
        speechBubbleGrowSize = 30;

        font = new BitmapFont(Gdx.files.internal("fonts/wiggly_curves_80.fnt"), Gdx.files.internal("fonts/wiggly_curves_80.png"), false );
        font.setColor(Color.BLACK);
    }

    @Override
    protected void handleInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDown (int x, int y, int pointer, int button) {
                GetOutGame.setState(new GameState());
                return true;
            }
        });
    }

    @Override
    public void update(float deltaTime) {
        handleInput();

        timerSpeechBubble++;
        if(timerSpeechBubble >= timerSpeechBubbleMax)
            timerSpeechBubble = 0;

        double speechBubbleTimerPercent = 0;
        if(timerSpeechBubble != 0)
            speechBubbleTimerPercent = (timerSpeechBubble + 0.0) / timerSpeechBubbleMax;
        speechBubbleGrowPercent = Math.sin(Math.toRadians(speechBubbleTimerPercent * 180));
        speechBubbleOffset = (int) (speechBubbleGrowPercent * speechBubbleGrowSize);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        ScreenUtils.clear(95/255f, 210/255f, 245/255f, 1);
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        spriteBatch.draw(textureSpeechBubbleLarge, speechBubbleX - speechBubbleOffset, speechBubbleY - speechBubbleOffset, textureSpeechBubbleLarge.getWidth() + (speechBubbleOffset * 2), textureSpeechBubbleLarge.getHeight() + (speechBubbleOffset * 2));
        font.draw(spriteBatch, "GET OUT", 160, 820);
        font.draw(spriteBatch, "OF MY", 185, 750);
        font.draw(spriteBatch, "ROOM!", 185, 680);

        spriteBatch.draw(textureSpeechBubble, 140, 215, textureSpeechBubble.getWidth() * 1.2f, textureSpeechBubble.getHeight() * 1.2f, 0, 0, textureSpeechBubble.getWidth(), textureSpeechBubble.getHeight(), true, false);
        font.draw(spriteBatch, "PLAY", 190, 340);

        spriteBatch.end();
    }

    @Override
    public void dispose() {
        textureSpeechBubble.dispose();
        textureSpeechBubbleLarge.dispose();
        font.dispose();
    }
}
