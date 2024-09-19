package diceoffate.helpers;

import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.random.Random;


public class DiceManager implements CustomSavable<DiceManager.DiceInfo> {
    public static final DiceManager instance = new DiceManager();
    private static final int CHANCE_ADJUSTMENT_AMOUNT = 10;
    private static final int NORMAL_DROP_RATE = 40;
    private static final int NORMAL_DICE_REWARD_AMOUNT = 1;
    private static final int ELITE_DICE_REWARD_AMOUNT = 1;
    private static final int BOSS_DICE_REWARD_AMOUNT = 2;
    private static final int STARTING_DICE_AMOUNT = 3;
    private DiceInfo info = null;

    public static boolean rollNormalReward() {
        if (instance.info.rng.random(0, 99) < NORMAL_DROP_RATE + instance.info.currentChanceAdjustment) {
            instance.info.currentChanceAdjustment -= CHANCE_ADJUSTMENT_AMOUNT;
            return true;
        } else {
            instance.info.currentChanceAdjustment += CHANCE_ADJUSTMENT_AMOUNT;
            return false;
        }
    }

    public static int getNormalRewardAmount() {
        return NORMAL_DICE_REWARD_AMOUNT;
    }

    public static int getEliteRewardAmount() {
        return ELITE_DICE_REWARD_AMOUNT;
    }

    public static int getBossRewardAmount() {
        return BOSS_DICE_REWARD_AMOUNT;
    }

    public static void addOrRemoveDice(int amount) {
        instance.info.diceCount += amount;
        if (instance.info.diceCount < 0) {
            instance.info.diceCount = 0;
        }
    }

    public static boolean canAfford(int amount) {
        return instance.info.diceCount >= amount;
    }

    public static int getDiceAmount() {
        return instance.info == null ? 0 : instance.info.diceCount;
    }

    public static void startGame() {
        instance.info = new DiceInfo();
    }

    @Override
    public DiceInfo onSave() {
        return info;
    }

    @Override
    public void onLoad(DiceInfo diceInfo) {
        info = diceInfo == null ? new DiceInfo() : diceInfo;
    }

    public static class DiceInfo {
        private final Random rng = new Random(Settings.seed);
        private int diceCount = STARTING_DICE_AMOUNT;
        private int currentChanceAdjustment = 0;
    }
}
