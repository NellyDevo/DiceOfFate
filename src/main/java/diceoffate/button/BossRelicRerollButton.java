package diceoffate.button;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rewards.chests.BossChest;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.screens.select.BossRelicSelectScreen;
import diceoffate.helpers.DiceHooks;
import diceoffate.helpers.DiceManager;
import diceoffate.helpers.DiceTexture;
import diceoffate.helpers.listeners.BossRelicListener;
import diceoffate.helpers.listeners.CardListener;

public class BossRelicRerollButton extends RerollButton {
    public static final int BOSS_REROLL_COST = 3;
    public static final TextureRegion BUTTON = new TextureRegion(ImageMaster.REWARD_SCREEN_TAKE_BUTTON);
    private static final int W = 512, H = 256;
    public static final float TAKE_Y = Settings.HEIGHT / 2f - 170f * Settings.scale;
    private final Color textColor = Color.WHITE.cpy();
    private final Color btnColor = Color.WHITE.cpy();
    private float controllerImgTextWidth = 0f;
    private static final float HITBOX_W = 260f * Settings.scale, HITBOX_H = 80f * Settings.scale;
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("diceoffate:button");

    @Override
    protected TextureRegion getButton() {
        return BUTTON;
    }

    public BossRelicRerollButton() {
        x = Settings.WIDTH / 2f;
        y = TAKE_Y;
        dieX = 65 * Settings.scale;
        hb.width = HITBOX_W;
        hb.height = HITBOX_H;
        cost = BOSS_REROLL_COST;
    }

    public void update(BossRelicSelectScreen screen) {
        hb.translate(x, y);
        update();
        if (hb.clicked) {
            if (DiceManager.canAfford(cost)) {
                DiceManager.addOrRemoveDice(-cost);
                TreasureRoomBoss bossRoom = (TreasureRoomBoss)AbstractDungeon.getCurrRoom();
                BossChest next = new BossChest();
                bossRoom.chest = next;
                screen.open(next.relics);
                next.isOpen = true;
                rollTimer = rollStart = 0.5f;
                DiceHooks.getListeners(BossRelicListener.class).forEach(listener -> {
                    listener.bossRelicRerolled(bossRoom, next);
                });
                //todo: roll sound
            } else {
                //todo: fail sound
            }
        }

        textColor.a = MathHelper.fadeLerpSnap(textColor.a, 1f);
        btnColor.a = textColor.a;
    }

    @Override
    public void render(SpriteBatch sb) {
        renderButton(sb);
        if (FontHelper.getSmartWidth(FontHelper.buttonLabelFont, uiStrings.TEXT[0], 9999f, 0f) > 200f * Settings.scale) {
            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, uiStrings.TEXT[0], x, y, textColor, 0.8f);
        } else {
            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, uiStrings.TEXT[0], x, y, textColor);
        }
        if (rollTimer > 0.0f) {
            rollTimer = DiceTexture.renderRollingDice(sb, rollTimer, rollStart, x + dieX, y + dieY, scale);
        } else {
            idleTimer = DiceTexture.renderCyclingDice(sb, dieOffset, idleTimer, idleInterval / (hb.hovered ? 2f : 1f), x + dieX, y + dieY, scale);
        }
        Color color = DiceManager.canAfford(cost) ? Color.WHITE : Color.RED;
        FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelAmountFont, String.valueOf(cost), hb.x + dieX + 144f * Settings.scale, hb.y + dieY + 39f * Settings.scale, color);
        hb.render(sb);
    }

    private void renderButton(SpriteBatch sb) {
        sb.setColor(btnColor);
        sb.draw(
                ImageMaster.REWARD_SCREEN_TAKE_BUTTON,
                x - W / 2f,
                y - H / 2f,
                W / 2f,
                H / 2f,
                W,
                H,
                Settings.scale,
                Settings.scale,
                0f,
                0,
                0,
                W,
                H,
                false,
                false);

        if (hb.hovered && !hb.clickStarted) {
            sb.setBlendFunction(GL30.GL_SRC_ALPHA, GL30.GL_ONE);
            sb.setColor(new Color(1f, 1f, 1f, 0.3f));
            sb.draw(
                    ImageMaster.REWARD_SCREEN_TAKE_BUTTON,
                    x - W / 2f,
                    y - H / 2f,
                    W / 2f,
                    H / 2f,
                    W,
                    H,
                    Settings.scale,
                    Settings.scale,
                    0f,
                    0,
                    0,
                    W,
                    H,
                    false,
                    false);
            sb.setBlendFunction(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        }

        if (Settings.isControllerMode) {

            if (controllerImgTextWidth == 0f) {
                controllerImgTextWidth = FontHelper.getSmartWidth(FontHelper.buttonLabelFont, uiStrings.TEXT[0], 99999f, 0f) / 2f;
            }

            sb.setColor(Color.WHITE);
            sb.draw(
                    CInputActionSet.cancel.getKeyImg(),
                    x - 32f - controllerImgTextWidth - 38f * Settings.scale,
                    y - 32f,
                    32f,
                    32f,
                    64,
                    64,
                    Settings.scale,
                    Settings.scale,
                    0f,
                    0,
                    0,
                    64,
                    64,
                    false,
                    false);
        }

        hb.render(sb);
    }

    public static class CardRewardRerollButtonPatches {
        @SpirePatch(
                clz = BossRelicSelectScreen.class,
                method = SpirePatch.CLASS
        )
        public static class RerollButtonField {
            public static SpireField<BossRelicRerollButton> rerollButton = new SpireField<>(BossRelicRerollButton::new);
        }
        @SpirePatch(
                clz = BossRelicSelectScreen.class,
                method = "update"
        )
        public static class RerollButtonUpdate {
            @SpirePrefixPatch
            public static void updateButton(BossRelicSelectScreen __instance) {
                BossRelicRerollButton button = RerollButtonField.rerollButton.get(__instance);
                if (button != null) {
                    button.update(__instance);
                }
            }
        }
        @SpirePatch(
                clz = BossRelicSelectScreen.class,
                method = "render"
        )
        public static class RerollButtonRender {
            @SpirePostfixPatch
            public static void renderButton(BossRelicSelectScreen __instance, SpriteBatch sb) {
                BossRelicRerollButton button = RerollButtonField.rerollButton.get(__instance);
                if (button != null) {
                    button.render(sb);
                }
            }
        }
    }

}
