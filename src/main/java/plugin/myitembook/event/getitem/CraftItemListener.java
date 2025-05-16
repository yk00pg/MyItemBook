package plugin.myitembook.event.getitem;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import plugin.myitembook.item.RegistrationChecker;

/**
 * アイテムをクラフトした際に発火するイベントのリスナークラス。
 */
public class CraftItemListener implements Listener {

  private final RegistrationChecker registrationChecker;

  public CraftItemListener(RegistrationChecker registrationChecker) {
    this.registrationChecker = registrationChecker;
  }

  @EventHandler
  public void onCraftItemEvent(CraftItemEvent e) {
    if (!(e.getWhoClicked() instanceof Player player)) {
      return;
    }

    ItemStack craftedItem = e.getInventory().getResult();
    if (craftedItem == null) {
      return;
    }

    Material material = craftedItem.getType();
    registrationChecker.checkAndHandleItemRegistration(player, material);
  }
}