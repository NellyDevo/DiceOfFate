package diceoffate.helpers.listeners;

import com.megacrit.cardcrawl.rewards.chests.BossChest;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;

public interface BossRelicListener extends RerollListener {
    void bossRelicRerolled(TreasureRoomBoss bossRoom, BossChest next);
}
