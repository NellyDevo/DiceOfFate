package diceoffate.helpers;

import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.random.Random;


public class DiceManager implements CustomSavable<DiceManager.DiceInfo> {
    public static final DiceManager instance = new DiceManager();
    private DiceInfo info = null;

    public static boolean rollNormalReward() {
        if (instance.info.rng.random(0, 99) < DiceProperties.getProperty(DiceProperties.NORMAL_REWARD_CHANCE) + instance.info.currentChanceAdjustment) {
            instance.info.currentChanceAdjustment -= DiceProperties.getProperty(DiceProperties.NORMAL_REWARD_ADJUSTMENT);
            if (instance.info.currentChanceAdjustment + DiceProperties.getProperty(DiceProperties.NORMAL_REWARD_CHANCE) < DiceProperties.getProperty(DiceProperties.REWARD_CHANCE_MINIMUM)) {
                instance.info.currentChanceAdjustment = -(DiceProperties.getProperty(DiceProperties.NORMAL_REWARD_CHANCE) - DiceProperties.getProperty(DiceProperties.REWARD_CHANCE_MINIMUM));
            }
            return true;
        } else {
            instance.info.currentChanceAdjustment += DiceProperties.getProperty(DiceProperties.NORMAL_REWARD_ADJUSTMENT);
            return false;
        }
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
        private int diceCount = DiceProperties.getProperty(DiceProperties.STARTING_DICE);
        private int currentChanceAdjustment = 0;
    }
}
