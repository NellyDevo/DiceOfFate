package diceoffate.button;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import diceoffate.DiceOfFate;
import diceoffate.util.TexLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RewardRerollButton extends RerollButton {
    public static final Map<RewardItem.RewardType, Integer> REWARD_REROLL_COSTS = new HashMap<>();
    public static final Map<RewardItem.RewardType, Consumer<RewardItem>> REWARD_REROLL_EXECUTION = new HashMap<>();
    static {
        REWARD_REROLL_COSTS.put(RewardItem.RewardType.CARD, 1);
        REWARD_REROLL_EXECUTION.put(RewardItem.RewardType.CARD, RewardRerollButton::rerollCards);
        REWARD_REROLL_COSTS.put(RewardItem.RewardType.POTION, 1);
        REWARD_REROLL_EXECUTION.put(RewardItem.RewardType.POTION, RewardRerollButton::rerollPotion);
        REWARD_REROLL_COSTS.put(RewardItem.RewardType.RELIC, 2);
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
            Integer cost = REWARD_REROLL_COSTS.get(reward.type);
            if (cost != null) {
                value = cost;
            } else {
                RewardRerollButton.RewardRerollButtonPatches.RerollButtonField.rerollButton.set(reward, null);
            }
        }
        y = reward.y;
        update();
        if (hb.clicked) {
            reward.hb.clicked = false;
            Consumer<RewardItem> consumer = REWARD_REROLL_EXECUTION.get(reward.type);
            if (consumer != null) {
                consumer.accept(reward);
            }
        }
        if (hb.hovered) {
            reward.hb.hovered = false;
        }
    }

    public static void rerollCards(RewardItem reward) {
        System.out.println("blep");
    }

    public static void rerollPotion(RewardItem reward) {
        System.out.println("blap");
    }

    public static void rerollRelic(RewardItem reward) {
        System.out.println("blop");
    }

    public static class RewardRerollButtonPatches {
        @SpirePatch(
                clz = RewardItem.class,
                method = SpirePatch.CLASS
        )
        public static class RerollButtonField {
            public static SpireField<RewardRerollButton> rerollButton = new SpireField<>(() -> new RewardRerollButton());
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
