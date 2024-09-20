package diceoffate.button;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.shop.Merchant;
import com.megacrit.cardcrawl.shop.ShopScreen;
import diceoffate.DiceOfFate;
import diceoffate.helpers.DiceHooks;
import diceoffate.helpers.DiceManager;
import diceoffate.helpers.DiceProperties;
import diceoffate.helpers.listeners.ShopListener;
import diceoffate.util.TexLoader;

import java.util.ArrayList;

public class ShopRerollButton extends RerollButton {
    public static final TextureRegion BUTTON_TEXTURE = new TextureRegion(TexLoader.getTexture(DiceOfFate.makeImagePath("ui/shop_reroll_button.png")));
    private static final float Y_BASE = (Settings.HEIGHT / 2f) - 150f * Settings.scale;

    @Override
    protected TextureRegion getButton() {
        return BUTTON_TEXTURE;
    }

    public ShopRerollButton() {
        x = (Settings.WIDTH) - 90f * Settings.scale;
        y = Y_BASE;
        scale = 1.5f;
        hb.width = 128;
        hb.height = 128;
        cost = DiceProperties.getProperty(DiceProperties.SHOP_REROLL_COST);
    }

    public void update(ShopScreen shop) {
        update();
        if (hb.clicked) {
            if (DiceManager.canAfford(cost)) {
                rerollShop(shop);
                DiceManager.addOrRemoveDice(-cost);
                rollTimer = rollStart = 0.5f;
                //todo: good click sound
            } else {
                //todo: bad click sound
            }
        }
        Float rugY = ReflectionHacks.getPrivate(shop, ShopScreen.class, "rugY");
        y = Y_BASE + rugY;
        hb.move(x, y);
    }

    public void rerollShop(ShopScreen shopScreen) {
        ShopRoom shopRoom = (ShopRoom)AbstractDungeon.getCurrRoom();
        ArrayList<AbstractCard> replacementColored = new ArrayList<>();
        ArrayList<AbstractCard> replacementColorless = new ArrayList<>();
        AbstractCard replacement;
        for (int i = 0; i < 2; ++i) {
            do {
                replacement = AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.ATTACK, true);
            } while (replacement.color == AbstractCard.CardColor.COLORLESS || groupContains(replacementColored, replacement));
            replacementColored.add(replacement);
        }
        for (int i = 0; i < 2; ++i) {
            do {
                replacement = AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.SKILL, true);
            } while (replacement.color == AbstractCard.CardColor.COLORLESS || groupContains(replacementColored, replacement));
            replacementColored.add(replacement);
        }
        do {
            replacement = AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.POWER, true);
        } while (replacement.color == AbstractCard.CardColor.COLORLESS || groupContains(replacementColored, replacement));
        replacementColored.add(replacement);

        replacementColorless.add(AbstractDungeon.getColorlessCardFromPool(AbstractCard.CardRarity.UNCOMMON));
        replacementColorless.add(AbstractDungeon.getColorlessCardFromPool(AbstractCard.CardRarity.RARE));
        ReflectionHacks.setPrivate(shopRoom.merchant, Merchant.class, "cards1", replacementColored);
        ReflectionHacks.setPrivate(shopRoom.merchant, Merchant.class, "cards2", replacementColorless);
        shopScreen.init(replacementColored, replacementColorless);
        DiceHooks.getListeners(ShopListener.class).forEach(listener -> {
            listener.shopRerolled(shopScreen, shopRoom);
        });
    }

    private boolean groupContains(ArrayList<AbstractCard> group, AbstractCard candidate) {
        for (AbstractCard card : group) {
            if (card.cardID.equals(candidate.cardID)) {
                return true;
            }
        }
        return false;
    }

    public static class ShopRerollButtonPatches {
        @SpirePatch(
                clz = ShopScreen.class,
                method = SpirePatch.CLASS
        )
        public static class RerollButtonField {
            public static SpireField<ShopRerollButton> rerollButton = new SpireField<>(ShopRerollButton::new);
        }
        @SpirePatch(
                clz = ShopScreen.class,
                method = "update"
        )
        public static class RerollButtonUpdate {
            @SpirePrefixPatch
            public static void updateButtonPre(ShopScreen __instance) {
                ShopRerollButton button = RerollButtonField.rerollButton.get(__instance);
                if (button != null) {
                    button.update(__instance);
                }
            }
        }
        @SpirePatch(
                clz = ShopScreen.class,
                method = "render"
        )
        public static class RerollButtonRender {
            @SpirePostfixPatch
            public static void renderButton(ShopScreen __instance, SpriteBatch sb) {
                ShopRerollButton button = RerollButtonField.rerollButton.get(__instance);
                if (button != null) {
                    button.render(sb);
                }
            }
        }
        //@SpirePatch(
        //        clz = ShopScreen.class,
        //        method = "open"
        //)
        //public static class RerollButtonAnimate {
        //    @SpirePostfixPatch
        //    public static void animateButton(ShopScreen __instance) {
        //        ShopRerollButton button = RerollButtonField.rerollButton.get(__instance);
        //        if (button != null) {
        //            button.y = Settings.HEIGHT + 400f * Settings.scale;
        //        }
        //    }
        //}
    }
}
