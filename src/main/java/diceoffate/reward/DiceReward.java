package diceoffate.reward;

import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.rewards.RewardItem;
import diceoffate.DiceOfFate;
import diceoffate.helpers.DiceTexture;

public class DiceReward extends CustomReward {
    public int amount;

    public DiceReward(int amount) {
        super((Texture)null, amount + " Dice Of Fate", RewardType.DICE_OF_FATE_REWARD);
        this.amount = amount;
        iconRegion = DiceTexture.getDiceImage();
    }

    @Override
    public boolean claimReward() {
        DiceOfFate.diceManager.addOrRemoveDice(amount);
        return true;
    }

    public static class RewardType {
        @SpireEnum public static RewardItem.RewardType DICE_OF_FATE_REWARD;
    }
}
