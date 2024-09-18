package diceoffate.helpers;

import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.random.Random;


public class DiceManager implements CustomSavable<DiceManager.DiceInfo> {
    private static final int CHANCE_ADJUSTMENT_AMOUNT = 10;
    private static final int NORMAL_DROP_RATE = 40;
    private static final int NORMAL_DICE_REWARD_AMOUNT = 1;
    private static final int ELITE_DICE_REWARD_AMOUNT = 1;
    private static final int BOSS_DICE_REWARD_AMOUNT = 2;
    private static final int STARTING_DICE_AMOUNT = 3;
    private DiceInfo info = null;

    public boolean rollNormalReward() {
        if (info.rng.random(0, 99) < NORMAL_DROP_RATE + info.currentChanceAdjustment) {
            info.currentChanceAdjustment -= CHANCE_ADJUSTMENT_AMOUNT;
            return true;
        } else {
            info.currentChanceAdjustment += CHANCE_ADJUSTMENT_AMOUNT;
            return false;
        }
    }

    public int getNormalRewardAmount() {
        return NORMAL_DICE_REWARD_AMOUNT;
    }

    public int getEliteRewardAmount() {
        return ELITE_DICE_REWARD_AMOUNT;
    }

    public int getBossRewardAmount() {
        return BOSS_DICE_REWARD_AMOUNT;
    }

    public void addOrRemoveDice(int amount) {
        info.diceCount += amount;
        if (info.diceCount < 0) {
            info.diceCount = 0;
        }
    }

    public boolean canAfford(int amount) {
        return info.diceCount >= amount;
    }

    public int getDiceAmount() {
        return info == null ? 0 : info.diceCount;
    }

    public void startGame() {
        info = new DiceInfo();
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
