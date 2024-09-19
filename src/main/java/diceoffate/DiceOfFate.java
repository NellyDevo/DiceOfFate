package diceoffate;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rewards.RewardSave;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import diceoffate.helpers.DiceManager;
import diceoffate.reward.DiceReward;
import diceoffate.toppanel.DiceTopPanelItem;

@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class DiceOfFate implements PostInitializeSubscriber, PostBattleSubscriber, PostDungeonInitializeSubscriber {
    public static final String modID = "diceoffate";

    public static String makeID(String idText) {
        return modID + ":" + idText;
    }

    public DiceOfFate() {
        BaseMod.subscribe(this);
    }

    public static String makePath(String resourcePath) {
        return modID + "Resources/" + resourcePath;
    }

    public static String makeImagePath(String resourcePath) {
        return modID + "Resources/images/" + resourcePath;
    }

    public static void initialize() {
        DiceOfFate thismod = new DiceOfFate();
    }

    @Override
    public void receivePostInitialize() {
        BaseMod.addSaveField(modID, DiceManager.instance);
        BaseMod.addTopPanelItem(new DiceTopPanelItem());
        BaseMod.registerCustomReward(
                DiceReward.RewardType.DICE_OF_FATE_REWARD,
                rewardSave -> new DiceReward(rewardSave.amount),
                customReward -> new RewardSave(customReward.type.toString(), null, ((DiceReward)customReward).amount, 0));
    }

    @Override
    public void receivePostBattle(AbstractRoom room) {
        boolean isEliteOrBoss = AbstractDungeon.getCurrRoom().eliteTrigger;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m.type == AbstractMonster.EnemyType.BOSS) {
                room.rewards.add(new DiceReward(DiceManager.instance.getBossRewardAmount()));
                return;
            }
        }
        if (room.eliteTrigger) {
            room.rewards.add(new DiceReward(DiceManager.instance.getEliteRewardAmount()));
        } else if (DiceManager.instance.rollNormalReward()) {
            room.rewards.add(new DiceReward(DiceManager.instance.getNormalRewardAmount()));
        }
    }

    @Override
    public void receivePostDungeonInitialize() {
        if (AbstractDungeon.actNum == 0) {
            DiceManager.instance.startGame();
        }
    }
}
