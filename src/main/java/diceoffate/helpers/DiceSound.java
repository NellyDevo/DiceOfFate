package diceoffate.helpers;

import basemod.BaseMod;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import diceoffate.DiceOfFate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiceSound {
    public static final int DICE_SOUND_COUNT = 7;
    public static final List<String> DICE_SOUNDS = new ArrayList<>();
    private static final Random soundRng = new Random();

    public static void initialize() {
        for (int i = 0; i < DICE_SOUND_COUNT; ++i) {
            String key = "DICE + i";
            DICE_SOUNDS.add(key);
            BaseMod.addAudio(key, DiceOfFate.makePath("sounds/dice_" + i + ".ogg"));
        }
    }

    public static void playDiceSound() {
        CardCrawlGame.sound.play(DICE_SOUNDS.get(soundRng.nextInt(DICE_SOUNDS.size())), 0.05f);
    }

}
