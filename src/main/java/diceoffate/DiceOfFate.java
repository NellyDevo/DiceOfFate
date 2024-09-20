package diceoffate;

import basemod.BaseMod;
import basemod.ModButton;
import basemod.ModLabel;
import basemod.ModPanel;
import basemod.interfaces.*;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rewards.RewardSave;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import diceoffate.helpers.DiceManager;
import diceoffate.helpers.DiceProperties;
import diceoffate.reward.DiceReward;
import diceoffate.toppanel.DiceTopPanelItem;
import diceoffate.util.TexLoader;

import java.io.IOException;
import java.util.Properties;

@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class DiceOfFate implements PostInitializeSubscriber, PostBattleSubscriber, PostDungeonInitializeSubscriber, EditStringsSubscriber {
    public static final String modID = "diceoffate";
    public static UIStrings uiStrings;

    public static String makeID(String idText) {
        return modID + ":" + idText;
    }

    public DiceOfFate() {
        BaseMod.subscribe(this);
        DiceProperties.initialize();
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

        uiStrings = CardCrawlGame.languagePack.getUIString(modID);
        Texture badgeImg = TexLoader.getTexture(makeImagePath("mod_badge.png"));
        ModPanel settingsPanel = new ModPanel();
        BaseMod.registerModBadge(badgeImg, uiStrings.TEXT[0], "Foxy Ellie", uiStrings.TEXT[1], settingsPanel);

        DiceProperties.createButtons(settingsPanel);
    }

    @Override
    public void receivePostBattle(AbstractRoom room) {
        boolean isEliteOrBoss = AbstractDungeon.getCurrRoom().eliteTrigger;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m.type == AbstractMonster.EnemyType.BOSS) {
                room.rewards.add(new DiceReward(DiceProperties.getProperty(DiceProperties.BOSS_REWARD_AMOUNT)));
                return;
            }
        }
        if (room.eliteTrigger) {
            room.rewards.add(new DiceReward(DiceProperties.getProperty(DiceProperties.ELITE_REWARD_AMOUNT)));
        } else if (DiceManager.rollNormalReward()) {
            room.rewards.add(new DiceReward(DiceProperties.getProperty(DiceProperties.NORMAL_REWARD_AMOUNT)));
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
