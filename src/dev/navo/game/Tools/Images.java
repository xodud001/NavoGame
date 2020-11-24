package dev.navo.game.Tools;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dev.navo.game.NavoGame;

public class Images {
    public static final Texture background = new Texture("data/GameBack.png"); // 배경 이미지 초기화


    private static float[] backgroundOffsets = {0, 0, 0, 0};

    private static Texture[] backgrounds = {
            new Texture("back/Starscape00.png"),
            new Texture("back/Starscape01.png"),
            new Texture("back/Starscape02.png"),
            new Texture("back/Starscape03.png")
    };

    public static void renderBackground(float delta, SpriteBatch batch) {
        float backgroundMaxScrollingSpeed = (float)(NavoGame.V_HEIGHT) / 4;

        //update position of background images
        backgroundOffsets[0] += delta * backgroundMaxScrollingSpeed / 8;
        backgroundOffsets[1] += delta * backgroundMaxScrollingSpeed / 4;
        backgroundOffsets[2] += delta * backgroundMaxScrollingSpeed / 2;
        backgroundOffsets[3] += delta * backgroundMaxScrollingSpeed;

        //draw each background layer
        for (int layer = 0; layer < backgroundOffsets.length; layer++) {
            if (backgroundOffsets[layer] > NavoGame.V_HEIGHT) {
                backgroundOffsets[layer] = 0;
            }
            batch.draw(backgrounds[layer],
                    0,
                    -backgroundOffsets[layer],
                    NavoGame.V_WIDTH, NavoGame.V_HEIGHT);
            batch.draw(backgrounds[layer],
                    0,
                    -backgroundOffsets[layer] + NavoGame.V_HEIGHT,
                    NavoGame.V_WIDTH, NavoGame.V_HEIGHT);
        }
    }
}
