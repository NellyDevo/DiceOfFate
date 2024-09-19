package diceoffate.helpers.listeners;

import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.shop.ShopScreen;

public interface ShopListener extends RerollListener  {
    void shopRerolled(ShopScreen shopScreen, ShopRoom shopRoom);
}
