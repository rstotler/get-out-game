package com.jbs.getout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.jbs.getout.states.GameState;

public class UI {
    public static FrameBuffer frameBuffer;
    public static SpriteBatch maskSpriteBatch;

    private Texture uiBarBottom, uiBarTop, cleanlinessMeter, maskCleanlinessMeter;
    private float cleanlinessDisplayPercent, cleanlinessDisplayDifference, cleanlinessDisplayDifferenceTotal, cleanlinessIncrementPercent;
    private int cleanlinessDisplayDir;

    public static Texture angerBarBottom, angerBarFillTop, angerBarFillBottom, maskAngerBarFillTop, maskAngerBarFillBottom;

    // Constructor //
    public UI() {
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, GetOutGame.SCREEN_WIDTH, GetOutGame.SCREEN_HEIGHT, false);
        maskSpriteBatch = new SpriteBatch();

        uiBarBottom = new Texture(Gdx.files.internal("images/ui/bar_bottom.png"));
        uiBarTop = new Texture(Gdx.files.internal("images/ui/bar_top.png"));
        cleanlinessMeter = new Texture(Gdx.files.internal("images/ui/cleanliness_meter.png"));
        maskCleanlinessMeter = new Texture(Gdx.files.internal("images/mask/cleanliness_meter.png"));

        angerBarBottom = new Texture(Gdx.files.internal("images/ui/anger_bar_bottom.png"));
        angerBarFillTop = new Texture(Gdx.files.internal("images/ui/anger_bar_fill_top.png"));
        angerBarFillBottom = new Texture(Gdx.files.internal("images/ui/anger_bar_fill_bottom.png"));
        maskAngerBarFillTop = new Texture(Gdx.files.internal("images/mask/anger_bar_fill_top.png"));
        maskAngerBarFillBottom = new Texture(Gdx.files.internal("images/mask/anger_bar_fill_bottom.png"));

        cleanlinessDisplayPercent = 0.0f;
    }

    public void update() {

        // Cleanliness Meter //
        if((cleanlinessDisplayDir == 1 && cleanlinessDisplayDifference > 0.00005)
        || (cleanlinessDisplayDir == -1 && cleanlinessDisplayDifference < 0.00005)) {
            cleanlinessDisplayPercent += cleanlinessDisplayDifferenceTotal * cleanlinessIncrementPercent;
            cleanlinessDisplayDifference -= cleanlinessDisplayDifferenceTotal * cleanlinessIncrementPercent;
            cleanlinessIncrementPercent *= .975;
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(uiBarBottom, GameState.camera.position.x - 270, GetOutGame.SCREEN_HEIGHT - uiBarBottom.getHeight());

        spriteBatch.end();
        frameBuffer.begin();
        maskSpriteBatch.setProjectionMatrix(GameState.camera.combined);
        maskSpriteBatch.begin();

        float maskRotation;
        if(cleanlinessDisplayPercent < .5)
            maskRotation = ((.5f - cleanlinessDisplayPercent) / .5f) * -36;
        else
            maskRotation = ((cleanlinessDisplayPercent - .5f) / .5f) * 38;

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        maskSpriteBatch.draw(maskCleanlinessMeter, (GetOutGame.SCREEN_WIDTH / 2.0f) - (maskCleanlinessMeter.getWidth() / 2.0f) + (GameState.camera.position.x - 270), GetOutGame.SCREEN_HEIGHT - 100, maskCleanlinessMeter.getWidth() / 2, maskCleanlinessMeter.getHeight() / 2, maskCleanlinessMeter.getWidth(), maskCleanlinessMeter.getHeight(), 1, 1, maskRotation, 0, 0, maskCleanlinessMeter.getWidth(), maskCleanlinessMeter.getHeight(), false, false);
        maskSpriteBatch.setBlendFunction(GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_ONE_MINUS_DST_COLOR);
        maskSpriteBatch.draw(cleanlinessMeter, (GetOutGame.SCREEN_WIDTH / 2.0f) - (cleanlinessMeter.getWidth() / 2.0f) + (GameState.camera.position.x - 270), GetOutGame.SCREEN_HEIGHT - 263);

        maskSpriteBatch.end();
        frameBuffer.end();
        spriteBatch.setProjectionMatrix(GameState.camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(frameBuffer.getColorBufferTexture(), (GameState.camera.position.x - 270), 0, GetOutGame.SCREEN_WIDTH, GetOutGame.SCREEN_HEIGHT, 0, 0, 1, 1);
        spriteBatch.draw(uiBarTop, GameState.camera.position.x - 270, GetOutGame.SCREEN_HEIGHT - uiBarTop.getHeight());
    }

    // Utility Functions //
    public void setCleanlinessMeter() {
        cleanlinessDisplayDifferenceTotal = GameState.getCleanlinessPercent() - cleanlinessDisplayPercent;
        cleanlinessDisplayDifference = cleanlinessDisplayDifferenceTotal;
        if(cleanlinessDisplayDifference > 0.0)
            cleanlinessDisplayDir = 1;
        else
            cleanlinessDisplayDir = -1;
        cleanlinessIncrementPercent = .025f;
    }

    public void dispose() {
        frameBuffer.dispose();
        maskSpriteBatch.dispose();

        uiBarBottom.dispose();
        uiBarTop.dispose();
        cleanlinessMeter.dispose();
        maskCleanlinessMeter.dispose();

        angerBarBottom.dispose();
        angerBarFillTop.dispose();
        angerBarFillBottom.dispose();
        maskAngerBarFillTop.dispose();
        maskAngerBarFillBottom.dispose();
    }
}
