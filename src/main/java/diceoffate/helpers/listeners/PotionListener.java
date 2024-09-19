package diceoffate.helpers.listeners;

import com.megacrit.cardcrawl.rewards.RewardItem;

public interface PotionListener extends RerollListener  {
    void potionRerolled(RewardItem reward);
}
