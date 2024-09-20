package diceoffate.helpers;

import basemod.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class DiceProperties {
    public static final ArrayList<Triple<String, Integer, InputHolder>> PROPERTIES_LIST = new ArrayList<>();
    public static final Triple<String, Integer, InputHolder> BOSS_RELIC_REROLL_COST;
    public static final Triple<String, Integer, InputHolder> CARD_REROLL_COST;
    public static final Triple<String, Integer, InputHolder> POTION_REROLL_COST;
    public static final Triple<String, Integer, InputHolder> RELIC_REROLL_COST;
    public static final Triple<String, Integer, InputHolder> SHOP_REROLL_COST;

    public static final Triple<String, Integer, InputHolder> NORMAL_REWARD_CHANCE;
    public static final Triple<String, Integer, InputHolder> NORMAL_REWARD_ADJUSTMENT;
    public static final Triple<String, Integer, InputHolder> REWARD_CHANCE_MINIMUM;

    public static final Triple<String, Integer, InputHolder> STARTING_DICE;
    public static final Triple<String, Integer, InputHolder> BOSS_REWARD_AMOUNT;
    public static final Triple<String, Integer, InputHolder> ELITE_REWARD_AMOUNT;
    public static final Triple<String, Integer, InputHolder> NORMAL_REWARD_AMOUNT;
    static {
        PROPERTIES_LIST.add(BOSS_RELIC_REROLL_COST = new Triple<>("BossRelicRerollCost", 3, new InputHolder()));
        PROPERTIES_LIST.add(CARD_REROLL_COST = new Triple<>("CardRerollCost", 1, new InputHolder()));
        PROPERTIES_LIST.add(POTION_REROLL_COST = new Triple<>("PotionRerollCost", 1, new InputHolder()));
        PROPERTIES_LIST.add(RELIC_REROLL_COST = new Triple<>("RelicRerollCost", 2, new InputHolder()));
        PROPERTIES_LIST.add(SHOP_REROLL_COST = new Triple<>("ShopRerollCost", 3, new InputHolder()));

        PROPERTIES_LIST.add(NORMAL_REWARD_CHANCE = new Triple<>("NormalRewardChance", 40, new InputHolder()));
        PROPERTIES_LIST.add(NORMAL_REWARD_ADJUSTMENT = new Triple<>("RewardChanceAdjustment", 10, new InputHolder()));
        PROPERTIES_LIST.add(REWARD_CHANCE_MINIMUM = new Triple<>("RewardChanceMinimum", 0, new InputHolder()));

        PROPERTIES_LIST.add(STARTING_DICE = new Triple<>("StartingDice", 3, new InputHolder()));
        PROPERTIES_LIST.add(BOSS_REWARD_AMOUNT = new Triple<>("BossRewardAmount", 2, new InputHolder()));
        PROPERTIES_LIST.add(ELITE_REWARD_AMOUNT = new Triple<>("EliteRewardAmount", 1, new InputHolder()));
        PROPERTIES_LIST.add(NORMAL_REWARD_AMOUNT = new Triple<>("NormalRewardAmount", 1, new InputHolder()));
    }
    private static final float TOP = 725f;
    private static final float LEFT = 434f;
    private static final int BUTTONS_PER_COLUMN = 6;
    private static final float COLUMN_WIDTH = 425f;

    public static UIStrings uiStrings;
    public static SpireConfig properties;
    private static InputProcessor oldInputProcessor;

    public static void initialize() {
        Properties defaults = new Properties();
        for (Triple<String, Integer, InputHolder> property : PROPERTIES_LIST) {
            defaults.setProperty(property.getFirst(), String.valueOf(property.getSecond()));
        }
        try {
            properties = new SpireConfig("Dice of Fate", "DiceOfFate", defaults);
        } catch (IOException e) {
            System.out.println("Dice Of Fate SpireConfig initialization failed:");
            e.printStackTrace();
        }
    }

    public static void createButtons(ModPanel settingsPanel) {
        uiStrings = CardCrawlGame.languagePack.getUIString("diceoffate:options");
        float xPos = LEFT, yPos = TOP;
        int columnCount = 0;
        for (int i = 0; i < PROPERTIES_LIST.size(); ++i) {
            int index = i;
            InputHolder inputHolder = PROPERTIES_LIST.get(i).getThird();
            settingsPanel.addUIElement(new ModLabel(uiStrings.TEXT[i+1], xPos, yPos, settingsPanel, me -> {}));
            ModButton input = new ModButton(xPos - 100f, yPos - 54f, settingsPanel, (me) -> {
                me.parent.waitingOnEvent = true;
                inputHolder.start();
                oldInputProcessor = Gdx.input.getInputProcessor();
                Gdx.input.setInputProcessor(new NumberInput() {
                    @Override
                    public boolean keyUp(int keycode) {
                        if (!super.keyUp(keycode)) {
                            if (keycode == Input.Keys.ENTER) {
                                me.parent.waitingOnEvent = false;
                                Gdx.input.setInputProcessor(oldInputProcessor);
                                try {
                                    properties.setInt(PROPERTIES_LIST.get(index).getFirst(), Integer.parseInt(inputHolder.getValue()));
                                    saveProperties();
                                } catch(NumberFormatException ignored) {}
                                inputHolder.reset();
                            } else {
                                if (keycode >= 144) keycode -= Input.Keys.NUMPAD_0 - Input.Keys.NUM_0;
                                inputHolder.append(Input.Keys.toString(keycode));
                            }
                        }
                        return true;
                    }
                });
            });
            settingsPanel.addUIElement(input);
            settingsPanel.addUIElement(new ModLabel("", xPos - 56f, yPos, settingsPanel, me -> {
                if (me.parent.waitingOnEvent && inputHolder.isActive()) {
                    me.text = inputHolder.getValue() + "_";
                } else {
                    me.text = String.valueOf(properties.getInt(PROPERTIES_LIST.get(index).getFirst()));
                }
            }));
            if (++columnCount >= BUTTONS_PER_COLUMN) {
                yPos = TOP;
                xPos += COLUMN_WIDTH;
                columnCount = 0;
            } else {
                yPos -= 100f;
            }
        }

        settingsPanel.addUIElement(new ModLabel(uiStrings.TEXT[0], 1309f, 475f, settingsPanel, me -> {}));
        ModButton restore = new ModButton(1209f, 421f, settingsPanel, (me) -> {
            for (Triple<String, Integer, InputHolder> property : PROPERTIES_LIST) {
                properties.setInt(property.getFirst(), property.getSecond());
                saveProperties();
            }
        });
        settingsPanel.addUIElement(restore);
    }

    private static void saveProperties() {
        try {properties.save();} catch (IOException e) {
            System.out.println("Failed to save properties:");
            e.printStackTrace();
        }
    }

    public static int getProperty(Triple<String, Integer, InputHolder> key) {
        return properties.getInt(key.getFirst());
    }

    private abstract static class NumberInput extends InputAdapter {
        @Override
        public boolean keyDown(int keycode) {
            return !((keycode >= Input.Keys.NUMPAD_0 && keycode <= Input.Keys.NUMPAD_9)
                    || (keycode >= Input.Keys.NUM_0 && keycode <= Input.Keys.NUM_9)
                    || keycode == Input.Keys.ENTER);
        }
        @Override
        public boolean keyUp(int keycode) {
            return !((keycode >= Input.Keys.NUMPAD_0 && keycode <= Input.Keys.NUMPAD_9)
                    || (keycode >= Input.Keys.NUM_0 && keycode <= Input.Keys.NUM_9)
                    || keycode == Input.Keys.ENTER);
        }
    }

    public static final class Triple<X, Y, Z> {
        private final X first;
        private final Y second;
        private final Z third;

        public Triple(X first, Y second, Z third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public X getFirst() {
            return first;
        }

        public Y getSecond() {
            return second;
        }

        public Z getThird() {
            return third;
        }
    }

    public static final class InputHolder {
        private String input = "";
        private boolean active;

        public String getValue() {
            return input;
        }

        public void start() {
            active = true;
        }

        public void append(String append) {
            input += append;
        }

        public void reset() {
            input = "";
            active = false;
        }

        public boolean isActive() {
            return active;
        }
    }
}
