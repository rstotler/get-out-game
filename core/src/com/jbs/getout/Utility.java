package com.jbs.getout;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.jbs.getout.gameobjects.GameObject;
import com.jbs.getout.states.GameState;

public class Utility {
    public static void removeGameObjectFromList(GameObject gameObject, Array<GameObject> objectList) {
        int deleteIndex = -1;
        for(int i = 0; i < objectList.size; i++) {
            if(objectList.get(i) == gameObject) {
                deleteIndex = i;
                break;
            }
        }

        if(deleteIndex != -1)
            objectList.removeIndex(deleteIndex);
    }

    public static void drawRect(int x, int y, int width, int height) {
        ShapeRenderer shape = new ShapeRenderer();
        shape.setProjectionMatrix(GameState.camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);

        shape.setColor(Color.RED);
        shape.rect(x, y, width, height);

        shape.end();
        shape.dispose();
    }
}
