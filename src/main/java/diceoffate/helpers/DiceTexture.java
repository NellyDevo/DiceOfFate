package diceoffate.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import diceoffate.DiceOfFate;
import diceoffate.util.TexLoader;

import java.util.Random;

public class DiceTexture {
    //sprite sheet raw
    private static final Texture DICE_TEXTURE = TexLoader.getTexture(DiceOfFate.makeImagePath("dice_sheet.png"));
    //sprite sheet related constants
    public static final int IMAGE_COUNT = 6;
    private static final int IMAGE_WIDTH = 32;
    private static final int IMAGE_HEIGHT = 32;
    //sprite sheet deconstructed
    private static final TextureRegion[] DICE_IMAGES = new TextureRegion[IMAGE_COUNT];
    static {
        for (int i = 0; i < IMAGE_COUNT; ++i) {
            DICE_IMAGES[i] = new TextureRegion(DICE_TEXTURE, 0, i * IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);
        }
    }

    public static TextureRegion getDiceImage() {
        return getDiceImage(new Random().nextInt(IMAGE_COUNT));
    }

    public static TextureRegion getDiceImage(int index) {
        while (index < 0) index += IMAGE_COUNT;
        while (index > IMAGE_COUNT) index -= IMAGE_COUNT;
        return DICE_IMAGES[index];
    }

    public static float renderCyclingDice(SpriteBatch sb, int dieOffset, float timer, float interval, float centerX, float centerY) {
        timer += Gdx.graphics.getDeltaTime();
        while (timer > interval) {
            timer -= interval;
            ++dieOffset;
        }
        renderDice(sb, getDiceImage(dieOffset), centerX, centerY, 1f);
        return timer;
    }

    public static float renderRollingDice(SpriteBatch sb, float timer, float startTimer, float centerX, float centerY) {
        float scale = 2f - Math.abs(timer - (startTimer / 2f)) / (startTimer / 2f);
        renderDice(sb, getDiceImage(), centerX, centerY, scale);
        timer -= Gdx.graphics.getDeltaTime();
        return timer;
    }

    public static void renderDice(SpriteBatch sb, TextureRegion image, float centerX, float centerY, float scale) {
        float halfWidth = image.getRegionWidth() / 2f;
        float halfHeight = image.getRegionHeight() / 2f;
        sb.setColor(Color.WHITE);
        sb.draw(image,
                centerX - halfWidth, centerY - halfHeight,              //bottom left coordinates
                halfWidth, halfHeight,                                  //origin coordinates relative to bottom left
                image.getRegionWidth(), image.getRegionHeight(),        //base width and height
                Settings.scale * scale, Settings.scale * scale, 0);     //width scale, height scale, rotation
    }

}
