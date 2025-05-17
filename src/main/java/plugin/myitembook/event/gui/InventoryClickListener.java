package plugin.myitembook.event.gui;

import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import plugin.myitembook.gui.click.ItemBookClickHandler;
import plugin.myitembook.gui.click.ItemDetailsClickHandler;
import plugin.myitembook.gui.management.GuiSettingStatus;

/**
 * インベントリをクリックした際に発火するイベントのリスナークラス。
 */
public class InventoryClickListener implements Listener {

  private final ItemBookClickHandler itemBookClickHandler;
  private final ItemDetailsClickHandler itemDetailsClickHandler;

  private final Map<UUID, GuiSettingStatus> playerCurrentGuiStatusMap;

  public InventoryClickListener(
      ItemBookClickHandler itemBookClickHandler,
      ItemDetailsClickHandler itemDetailsClickHandler,
      Map<UUID, GuiSettingStatus> playerCurrentGuiStatusMap) {

    this.itemBookClickHandler = itemBookClickHandler;
    this.itemDetailsClickHandler = itemDetailsClickHandler;
    this.playerCurrentGuiStatusMap = playerCurrentGuiStatusMap;
  }

  /**
   * インベントリをクリックした際に発火するイベント。<br> クリックしたインベントリのタイトルが「◆アイテム図鑑◆」の場合、アイテム図鑑の設定状況にcurrentMaterialが含まれているか否かで<br> クリックされた画面を判定し、一覧画面と詳細画面で分岐してメソッドを呼び出す。
   *
   * @param e イベント
   */
  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {

    if (!(e.getWhoClicked() instanceof Player player) ||
        !(e.getView().getTitle().equals("◆アイテム図鑑◆")) ||
        e.getCurrentItem() == null) {
      return;
    }
    e.setCancelled(true);

    int clickedSlot = e.getRawSlot();
    Material clickedMaterial = e.getCurrentItem().getType();

    GuiSettingStatus guiSettingStatus = playerCurrentGuiStatusMap.get(player.getUniqueId());
    Material currentMaterial = guiSettingStatus.getCurrentMaterial();

    if (currentMaterial == null) {
      itemBookClickHandler.handleItemBookClick(clickedSlot, clickedMaterial, player, guiSettingStatus);
    } else {
      itemDetailsClickHandler.handleItemDetailsClick(
          clickedSlot, player, clickedMaterial, currentMaterial, guiSettingStatus);
    }
  }
}
