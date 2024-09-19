package diceoffate;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardSave;
import com.megacrit.cardcrawl.rewards.chests.BossChest;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.shop.ShopScreen;
import diceoffate.helpers.DiceHooks;
import diceoffate.helpers.DiceManager;
import diceoffate.helpers.listeners.*;
import diceoffate.reward.DiceReward;
import diceoffate.toppanel.DiceTopPanelItem;

@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class DiceOfFate implements PostInitializeSubscriber, PostBattleSubscriber, PostDungeonInitializeSubscriber, EditStringsSubscriber {
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
                room.rewards.add(new DiceReward(DiceManager.getBossRewardAmount()));
                return;
            }
        }
        if (room.eliteTrigger) {
            room.rewards.add(new DiceReward(DiceManager.getEliteRewardAmount()));
        } else if (DiceManager.rollNormalReward()) {
            room.rewards.add(new DiceReward(DiceManager.getNormalRewardAmount()));
        }
    }

    @Override
    public void receivePostDungeonInitialize() {
        if (AbstractDungeon.actNum == 0) {
            DiceManager.startGame();
        }
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(UIStrings.class, makePath("localization/") + "eng/ui_strings.json");
        if (!Settings.language.toString().equalsIgnoreCase("eng")) {
            try {
                BaseMod.loadCustomStringsFile(UIStrings.class, makePath("localization/") + Settings.language.toString().toLowerCase() + "/ui_strings.json");
            } catch (Exception ignored) {}
        }
    }
}
