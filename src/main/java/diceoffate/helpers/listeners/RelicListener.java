package diceoffate.helpers.listeners;

import com.megacrit.cardcrawl.rewards.RewardItem;

public interface RelicListener extends RerollListener  {
    void relicRerolled(RewardItem reward);
}
