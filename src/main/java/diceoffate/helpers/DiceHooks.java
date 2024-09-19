package diceoffate.helpers;

import diceoffate.helpers.listeners.RerollListener;

import java.util.ArrayList;

public class DiceHooks {
    private static final ArrayList<RerollListener> listeners = new ArrayList<>();

    public static void registerListener(RerollListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static <T extends RerollListener> ArrayList<T> getListeners(Class<T> type) {
        ArrayList<T> list = new ArrayList<>();
        for (RerollListener listener : listeners) {
            if (type.isInstance(listener)) {
                list.add(type.cast(listener));
            }
        }
        return list;
    }
}
