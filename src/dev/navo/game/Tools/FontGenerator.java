package dev.navo.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontGenerator {

    public static BitmapFont fontBold16 = new BitmapFont(Gdx.files.internal("font/16Bold/hangulBold16.fnt"));
    public static BitmapFont fontBold12 = new BitmapFont(Gdx.files.internal("font/12Bold/hangulBold12.fnt"));
    public FontGenerator(){

    }
}
