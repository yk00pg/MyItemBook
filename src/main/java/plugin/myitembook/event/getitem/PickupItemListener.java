package plugin.myitembook.event.getitem;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import plugin.myitembook.item.RegistrationChecker;

/**
 * アイテムを拾った際に発火するイベントのリスナークラス。
 */
public class PickupItemListener implements Listener {

  private final RegistrationChecker registrationChecker;

  public PickupItemListener(RegistrationChecker registrationChecker) {
    this.registrationChecker = registrationChecker;
  }

  @EventHandler
  public void onPickupItemEvent(EntityPickupItemEvent e) {
    if (e.getEntity() instanceof Player player) {
      Material material = e.getItem().getItemStack().getType();
      registrationChecker.checkAndHandleItemRegistration(player, material);
    }
  }
}