package diceoffate.toppanel;

import basemod.TopPanelItem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import diceoffate.DiceOfFate;
import diceoffate.helpers.DiceTexture;

public class DiceTopPanelItem extends TopPanelItem {
    private static final float IDLE_SCALE = 1.5f;
    private static final float HOVER_SCALE = 2f;
    private TextureRegion currentImage;
    private float currentScale = IDLE_SCALE;
    private float targetScale = currentScale;
    private static final float TIP_Y = Settings.HEIGHT - 120f * Settings.scale;
    private static final float TOP_RIGHT_TIP_X = 1550f * Settings.scale;

    public DiceTopPanelItem() {
        super(null, DiceOfFate.modID);
        currentImage = DiceTexture.getDiceImage();
    }

    @Override
    protected void onClick() {}

    @Override
    protected void onHover() {
        if (targetScale != HOVER_SCALE) {
            targetScale = HOVER_SCALE;
            TextureRegion tmp = currentImage;
            do {
                currentImage = DiceTexture.getDiceImage();
            } while (tmp == currentImage);
        }
        TipHelper.renderGenericTip(TOP_RIGHT_TIP_X, TIP_Y, "Dice Of Fate", "Spend Dice to alter fate, changing various rewards."); //TODO localize
    }

    @Override
    protected void onUnhover() {
        targetScale = IDLE_SCALE;
    }

    @Override
    public void render(SpriteBatch sb) {
        DiceTexture.renderDice(sb, currentImage, getHitbox().x + getHitbox().cX, getHitbox().y + getHitbox().cY, currentScale);
        FontHelper.renderFontRightTopAligned(sb, FontHelper.topPanelAmountFont, String.valueOf(DiceOfFate.diceManager.getDiceAmount()), getHitbox().x + 58f * Settings.scale, getHitbox().y + 25f * Settings.scale, Color.WHITE);
        renderHitbox(sb);
    }

    @Override
    public void update() {
        super.update();
        if (targetScale != currentScale) {
            currentScale = MathHelper.scaleLerpSnap(currentScale, targetScale);
        }
    }
}
