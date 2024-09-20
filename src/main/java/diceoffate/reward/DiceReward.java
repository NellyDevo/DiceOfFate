package diceoffate.reward;

import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import diceoffate.helpers.DiceManager;
import diceoffate.helpers.DiceTexture;

public class DiceReward extends CustomReward {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("diceoffate:reward");
    public int amount;

    public DiceReward(int amount) {
        super((Texture)null, amount + (amount == 1 ? uiStrings.TEXT[0] : uiStrings.TEXT[1]), RewardType.DICE_OF_FATE_REWARD);
        this.amount = amount;
        iconRegion = DiceTexture.getDiceImage();
    }

    @Override
    public boolean claimReward() {
        DiceManager.addOrRemoveDice(amount);
        return true;
    }

    public static class RewardType {
        @SpireEnum public static RewardItem.RewardType DICE_OF_FATE_REWARD;
    }
}
