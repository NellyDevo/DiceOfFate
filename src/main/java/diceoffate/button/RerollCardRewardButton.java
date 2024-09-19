package diceoffate.button;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.buttons.SkipCardButton;
import diceoffate.helpers.DiceManager;
import diceoffate.helpers.DiceTexture;
import javassist.CtBehavior;

public class RerollCardRewardButton extends RerollButton {
    private static final TextureRegion BUTTON = new TextureRegion(ImageMaster.REWARD_SCREEN_TAKE_BUTTON);
    private boolean isHidden = true;
    private static final int W = 512, H = 256;
    public static final float TAKE_Y = Settings.HEIGHT / 2f - 440f * Settings.scale;
    private static final float SHOW_X = Settings.WIDTH / 2f;
    private static final float HIDE_X = Settings.WIDTH / 2f;
    private float target_x = x;
    private final Color textColor = Color.WHITE.cpy();
    private final Color btnColor = Color.WHITE.cpy();
    private float controllerImgTextWidth = 0f;
    private static final float HITBOX_W = 260f * Settings.scale, HITBOX_H = 80f * Settings.scale;

    public RerollCardRewardButton() {
        y = TAKE_Y;
        dieX = 100 * Settings.scale;
        hb.width = HITBOX_W;
        hb.height = HITBOX_H;
        cost = RewardRerollButton.REWARD_REROLL_COSTS.get(RewardItem.RewardType.CARD);
    }

    @Override
    protected TextureRegion getButton() {
        return BUTTON;
    }

    public void hide() {
        if (!isHidden) {
            isHidden = true;
        }
    }

    public void show() {
        isHidden = false;
        textColor.a = 0f;
        btnColor.a = 0f;
        x = HIDE_X;
        target_x = SHOW_X;
    }

    public void update(CardRewardScreen screen) {
        hb.translate(x, y);
        update();
        if (hb.clicked) {
            if (DiceManager.canAfford(cost)) {
                DiceManager.addOrRemoveDice(-cost);
                RewardRerollButton.rerollCards(screen.rItem);
                screen.rewardGroup.clear();
                screen.rewardGroup.addAll(screen.rItem.cards);
                rollTimer = rollStart = 0.5f;
                //todo: roll sound
            } else {
                //todo: fail sound
            }
        }
        if (x != target_x) {
            x = MathUtils.lerp(x, target_x, Gdx.graphics.getDeltaTime() * Settings.UI_LERP_SPEED);
            if (Math.abs(x - target_x) < Settings.UI_SNAP_THRESHOLD) {
                x = target_x;
                hb.move(x, y);
            }
        }

        textColor.a = MathHelper.fadeLerpSnap(textColor.a, 1f);
        btnColor.a = textColor.a;
    }

    @Override
    public void render(SpriteBatch sb) {
        if (isHidden) {
            return;
        }

        renderButton(sb);
        if (FontHelper.getSmartWidth(FontHelper.buttonLabelFont, "Reroll", 9999f, 0f) > 200f * Settings.scale) {
            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, "Reroll", x, y, textColor, 0.8f);
        } else {
            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, "Reroll", x, y, textColor);
        }
        if (rollTimer > 0.0f) {
            rollTimer = DiceTexture.renderRollingDice(sb, rollTimer, rollStart, x + dieX, y + dieY, scale);
        } else {
            idleTimer = DiceTexture.renderCyclingDice(sb, dieOffset, idleTimer, idleInterval / (hb.hovered ? 2f : 1f), x + dieX, y + dieY, scale);
        }
        Color color = DiceManager.canAfford(cost) ? Color.WHITE : Color.RED;
        FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelAmountFont, String.valueOf(cost), hb.x + dieX + 144f * Settings.scale, hb.y + dieY + 39f * Settings.scale, color);
        hb.render(sb);
    } //TODO: localize

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
                controllerImgTextWidth = FontHelper.getSmartWidth(FontHelper.buttonLabelFont, "Reroll", 99999f, 0f) / 2f;
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
                clz = CardRewardScreen.class,
                method = SpirePatch.CLASS
        )
        public static class RerollButtonField {
            public static SpireField<RerollCardRewardButton> rerollButton = new SpireField<>(RerollCardRewardButton::new);
        }
        @SpirePatch(
                clz = CardRewardScreen.class,
                method = "update"
        )
        public static class RerollButtonUpdate {
            @SpireInsertPatch(
                    locator = UpdateLocator.class
            )
            public static void updateButton(CardRewardScreen __instance) {
                RerollCardRewardButton button = CardRewardRerollButtonPatches.RerollButtonField.rerollButton.get(__instance);
                if (button != null) {
                    button.update(__instance);
                }
            }
        }
        @SpirePatch(
                clz = CardRewardScreen.class,
                method = "update"
        )
        private static class UpdateLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(SkipCardButton.class, "update");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
        public static class RerollButtonHide {
            @SpireInsertPatch(
                    locator = HideLocator.class
            )
            public static void hideButton(CardRewardScreen __instance) {
                RerollCardRewardButton button = CardRewardRerollButtonPatches.RerollButtonField.rerollButton.get(__instance);
                if (button != null) {
                    button.hide();
                }
            }
        }
        private static class HideLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(SkipCardButton.class, "hide");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
        @SpirePatch(
                clz = CardRewardScreen.class,
                method = "render"
        )
        public static class RerollButtonRender {
            @SpireInsertPatch(
                    locator = RenderLocator.class
            )
            public static void renderButton(CardRewardScreen __instance, SpriteBatch sb) {
                RerollCardRewardButton button = CardRewardRerollButtonPatches.RerollButtonField.rerollButton.get(__instance);
                if (button != null) {
                    button.render(sb);
                }
            }
        }
        private static class RenderLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(SkipCardButton.class, "render");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
        @SpirePatch2(
                clz = CardRewardScreen.class,
                method = "reopen"
        )
        @SpirePatch2(
                clz = CardRewardScreen.class,
                method = "open"
        )
        public static class RerollButtonShow {
            @SpireInsertPatch(
                    locator = ShowLocator.class
            )
            public static void showButton(CardRewardScreen __instance) {
                RerollCardRewardButton button = CardRewardRerollButtonPatches.RerollButtonField.rerollButton.get(__instance);
                if (button != null) {
                    button.show();
                }
            }
        }
        private static class ShowLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
