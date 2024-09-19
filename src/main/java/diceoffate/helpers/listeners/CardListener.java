package diceoffate.helpers.listeners;

import com.megacrit.cardcrawl.rewards.RewardItem;

public interface CardListener extends RerollListener {
    void cardRerolled(RewardItem reward);
}
