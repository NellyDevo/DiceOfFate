package diceoffate.helpers;

import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.random.Random;


public class DiceManager implements CustomSavable<DiceManager.DiceInfo> {
    public static final DiceManager instance = new DiceManager();
    private static Random rng;
    private DiceInfo info = new DiceInfo();

    public static boolean rollNormalReward() {
        if (getRng().random(0, 99) < DiceProperties.getProperty(DiceProperties.NORMAL_REWARD_CHANCE) + instance.info.currentChanceAdjustment) {
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

    private static Random getRng() {
        if (rng == null) {
            rng = new Random(Settings.seed, instance.info == null ? 0 : instance.info.rngCounter);
        }
        return rng;
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
        rng = new Random(Settings.seed);
    }

    @Override
    public DiceInfo onSave() {
        if (info == null) {info = new DiceInfo();}
        info.rngCounter = rng == null ? 0 : rng.counter;
        return info;
    }

    @Override
    public void onLoad(DiceInfo diceInfo) {
        info = diceInfo == null ? new DiceInfo() : diceInfo;
    }

    public static class DiceInfo {
        private int rngCounter = 0;
        private int diceCount = DiceProperties.getProperty(DiceProperties.STARTING_DICE);
        private int currentChanceAdjustment = 0;
    }
}
