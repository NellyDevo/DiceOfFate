package diceoffate.button;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import diceoffate.helpers.DiceManager;
import diceoffate.helpers.DiceTexture;

import java.util.Random;

public abstract class RerollButton {
    public float x;
    public float y;
    public float dieX;
    public float dieY;
    public float scale = 0.85f;
    public Hitbox hb = new Hitbox(x, y);
    public float idleTimer = 0.0f;
    public float idleInterval = 1.0f;
    public float rollTimer = 0.0f;
    public float rollStart = 0.0f;
    public int cost;
    protected final int dieOffset = new Random().nextInt(DiceTexture.IMAGE_COUNT);

    protected abstract TextureRegion getButton();

    public void update() {
        resetHitbox();
        hb.update(hb.x, hb.y);
        if (hb.clicked) {
            hb.clicked = false;
        } else if (hb.hovered && InputHelper.justClickedLeft) {
            hb.clickStarted = true;
            InputHelper.justClickedLeft = false;
        } else if (hb.clickStarted) {
            if (hb.hovered) {
                if (InputHelper.justReleasedClickLeft) {
                    InputHelper.justReleasedClickLeft = false;
                    hb.clicked = true;
                }
            } else {
                hb.clickStarted = false;
            }
        }
    }

    public void render(SpriteBatch sb) {
        TextureRegion button = getButton();
        sb.setColor(Color.WHITE);
        float halfWidth = button.getRegionWidth() / 2f;
        float halfHeight = button.getRegionHeight() / 2f;
        sb.draw(button, x - halfWidth, y - halfHeight, halfWidth, halfHeight, button.getRegionWidth(), button.getRegionHeight(), Settings.scale, Settings.scale, 0);
        if (hb.hovered) {
            sb.setBlendFunction(GL30.GL_SRC_ALPHA, GL30.GL_ONE);
            sb.setColor(new Color(1f, 1f, 1f, 0.3f));
            sb.draw(button, x - halfWidth, y - halfHeight, halfWidth, halfHeight, button.getRegionWidth(), button.getRegionHeight(), Settings.scale, Settings.scale, 0);
            sb.setColor(Color.WHITE);
            sb.setBlendFunction(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        }
        if (rollTimer > 0.0f) {
            rollTimer = DiceTexture.renderRollingDice(sb, rollTimer, rollStart, x + dieX, y + dieY, scale);
        } else {
            idleTimer = DiceTexture.renderCyclingDice(sb, dieOffset, idleTimer, idleInterval / (hb.hovered ? 2f : 1f), x + dieX, y + dieY, scale);
        }
        Color color = DiceManager.canAfford(cost) ? Color.WHITE : Color.RED;
        FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelAmountFont, String.valueOf(cost), dieX + hb.cX + 15f * scale * Settings.scale, dieY + hb.cY - 7f * scale * Settings.scale, color);
        hb.render(sb);
    }

    public void resetHitbox() {
        hb.translate(x - (hb.width / 2f), y - (hb.height / 2f));
    }
}
