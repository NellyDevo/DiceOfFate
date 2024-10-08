package diceoffate.button;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.rewards.RewardItem;
import diceoffate.DiceOfFate;
import diceoffate.helpers.DiceHooks;
import diceoffate.helpers.DiceManager;
import diceoffate.helpers.DiceProperties;
import diceoffate.helpers.DiceSound;
import diceoffate.helpers.listeners.CardListener;
import diceoffate.helpers.listeners.PotionListener;
import diceoffate.helpers.listeners.RelicListener;
import diceoffate.util.TexLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RewardRerollButton extends RerollButton {
    public static final Map<RewardItem.RewardType, Supplier<Integer>> REWARD_REROLL_COSTS = new HashMap<>();
    public static final Map<RewardItem.RewardType, Consumer<RewardItem>> REWARD_REROLL_EXECUTION = new HashMap<>();
    static {
        REWARD_REROLL_COSTS.put(RewardItem.RewardType.CARD, () -> DiceProperties.getProperty(DiceProperties.CARD_REROLL_COST));
        REWARD_REROLL_EXECUTION.put(RewardItem.RewardType.CARD, RewardRerollButton::rerollCards);
        REWARD_REROLL_COSTS.put(RewardItem.RewardType.POTION, () -> DiceProperties.getProperty(DiceProperties.POTION_REROLL_COST));
        REWARD_REROLL_EXECUTION.put(RewardItem.RewardType.POTION, RewardRerollButton::rerollPotion);
        REWARD_REROLL_COSTS.put(RewardItem.RewardType.RELIC, () -> DiceProperties.getProperty(DiceProperties.RELIC_REROLL_COST));
        REWARD_REROLL_EXECUTION.put(RewardItem.RewardType.RELIC, RewardRerollButton::rerollRelic);
    }
    public static final TextureRegion BUTTON_TEXTURE = new TextureRegion(TexLoader.getTexture(DiceOfFate.makeImagePath("ui/reroll_button.png")));
    private boolean initialized = false;

    @Override
    protected TextureRegion getButton() {
        return BUTTON_TEXTURE;
    }

    public RewardRerollButton() {
        x = (Settings.WIDTH / 2f) + 232f * Settings.scale;
        hb.width = 32;
        hb.height = 32;
    }

    public void update(RewardItem reward) {
        if (!initialized) {
            initialized = true;
            Supplier<Integer> cost = REWARD_REROLL_COSTS.get(reward.type);
            if (cost != null) {
                this.cost = cost.get();
            } else {
                RewardRerollButton.RewardRerollButtonPatches.RerollButtonField.rerollButton.set(reward, null);
            }
        }
        y = reward.y;
        update();
        if (hb.clicked) {
            if (DiceManager.canAfford(cost)) {
                Consumer<RewardItem> consumer = REWARD_REROLL_EXECUTION.get(reward.type);
                if (consumer != null) {
                    consumer.accept(reward);
                    DiceManager.addOrRemoveDice(-cost);
                    rollTimer = rollStart = 0.5f;
                    DiceSound.playDiceSound();
                }
            } else {
                CardCrawlGame.sound.play("UI_CLICK_2", 0.05f);
            }
            reward.hb.clicked = false;
        }
        if (hb.hovered) {
            reward.hb.hovered = false;
        }
    }

    public static void rerollCards(RewardItem reward) {
        reward.cards = AbstractDungeon.getRewardCards();
        DiceHooks.getListeners(CardListener.class).forEach(listener -> {
            listener.cardRerolled(reward);
        });
    }

    public static void rerollPotion(RewardItem reward) {
        reward.potion = PotionHelper.getRandomPotion();
        reward.text = reward.potion.name;
        DiceHooks.getListeners(PotionListener.class).forEach(listener -> {
            listener.potionRerolled(reward);
        });
    }

    public static void rerollRelic(RewardItem reward) {
        reward.relic = AbstractDungeon.returnRandomRelic(AbstractDungeon.returnRandomRelicTier());
        reward.relic.hb = new Hitbox(80f * Settings.scale, 80f * Settings.scale);
        reward.relic.hb.move(-1000f, -1000f);
        reward.text = reward.relic.name;
        DiceHooks.getListeners(RelicListener.class).forEach(listener -> {
            listener.relicRerolled(reward);
        });
    }

    public static class RewardRerollButtonPatches {
        @SpirePatch(
                clz = RewardItem.class,
                method = SpirePatch.CLASS
        )
        public static class RerollButtonField {
            public static SpireField<RewardRerollButton> rerollButton = new SpireField<>(RewardRerollButton::new);
        }
        @SpirePatch(
                clz = RewardItem.class,
                method = "update"
        )
        public static class RerollButtonUpdate {
            @SpirePrefixPatch
            public static void updateButtonPre(RewardItem __instance) {
                RewardRerollButton button = RerollButtonField.rerollButton.get(__instance);
                if (button != null) {
                    button.update(__instance);
                }
            }
        }
        @SpirePatch(
                clz = RewardItem.class,
                method = "render"
        )
        public static class RerollButtonRender {
            @SpirePostfixPatch
            public static void renderButton(RewardItem __instance, SpriteBatch sb) {
                RewardRerollButton button = RerollButtonField.rerollButton.get(__instance);
                if (button != null) {
                    button.render(sb);
                }
            }
        }
    }
}
