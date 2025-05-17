package plugin.myitembook.gui.click;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import plugin.myitembook.data.PlayerDataSaver;
import plugin.myitembook.gui.management.GuiSettingStatus;
import plugin.myitembook.gui.management.ItemBookGuiManager;
import plugin.myitembook.gui.management.ItemDetailsGuiManager;
import plugin.myitembook.gui.management.Menu;
import plugin.myitembook.gui.management.OrderType;

/**
 * アイテム図鑑の一覧画面におけるクリックに応じた処理を実行するクラス。
 */
public class ItemBookClickHandler {

  private final ItemBookGuiManager itemBookGuiManager;
  private final ItemDetailsGuiManager itemDetailsGuiManager;
  private final PlayerDataSaver playerDataSaver;

  private final Map<UUID, GuiSettingStatus> playerCurrentGuiStatusMap;

  public ItemBookClickHandler(
      ItemBookGuiManager itemBookGuiManager,
      ItemDetailsGuiManager itemDetailsGuiManager,
      PlayerDataSaver playerDataSaver,
      Map<UUID, GuiSettingStatus> playerCurrentGuiStatusMap) {

    this.itemBookGuiManager = itemBookGuiManager;
    this.itemDetailsGuiManager = itemDetailsGuiManager;
    this.playerDataSaver = playerDataSaver;
    this.playerCurrentGuiStatusMap = playerCurrentGuiStatusMap;
  }

  /**
   * アイテム図鑑の一覧画面におけるクリックを判定して処理を実行する。<br> クリックされたスロットの番号が45未満（登録済みアイテムが配置されたスロット）の場合は、アイテムの詳細画面を開くメソッドを呼び出す。<br>
   * そうでない（メニューが配置されたスロット）場合は、メニュー別に処理するメソッドを呼び出す。
   *
   * @param clickedSlot      クリックされたスロット
   * @param clickedMaterial  クリックされたアイテム
   * @param player           クリックしたプレイヤー
   * @param guiSettingStatus アイテム図鑑の設定状況
   */
  public void handleItemBookClick(
      int clickedSlot, Material clickedMaterial, Player player, GuiSettingStatus guiSettingStatus) {

    if (clickedSlot < 45) {
      openItemDetailsIfPresent(clickedMaterial, player, guiSettingStatus);
    } else {
      handleMenuClick(guiSettingStatus, player, clickedMaterial);
    }
  }

  /**
   * クリックされたアイテムがBEDROCK（未登録）でない場合は、アイテムの詳細画面を開く。
   *
   * @param clickedMaterial  クリックされたアイテム
   * @param player           クリックしたプレイヤー
   * @param guiSettingStatus アイテム図鑑の設定状況
   */
  private void openItemDetailsIfPresent(
      Material clickedMaterial, Player player, GuiSettingStatus guiSettingStatus) {

    if (clickedMaterial.equals(Material.BEDROCK)) {
      return;
    }
    itemDetailsGuiManager.openItemDetailsGui(player, clickedMaterial, guiSettingStatus);
  }

  /**
   * クリックされたアイテムからメニューを判定し、分岐して処理を実行する。
   *
   * @param guiSettingStatus アイテム図鑑の設定状況
   * @param player           クリックしたプレイヤー
   * @param clickedMaterial  クリックされたアイテム
   */
  private void handleMenuClick(
      GuiSettingStatus guiSettingStatus, Player player, Material clickedMaterial) {

    int currentPage = guiSettingStatus.getCurrentPage();
    OrderType currentOrderType = guiSettingStatus.getCurrentOrderType();

    Menu.getFilteredMenu(clickedMaterial).ifPresent(
        menu -> {
          switch (menu) {
            case SAVE -> {
              playerDataSaver.savePlayerItemBookData(player);
              player.closeInventory();
              itemBookGuiManager.reopenItemBookGui(player, currentPage, currentOrderType);
            }
            case LIST, SORT -> openItemBookByOrderType(menu, currentOrderType, player);
            case PREV -> goPrevPageIfPresent(currentPage, player, currentOrderType);
            case NEXT -> goNextPageIfPresent(guiSettingStatus, currentPage, player, currentOrderType);
            case CLOSE -> {
              playerCurrentGuiStatusMap.remove(player.getUniqueId());
              player.closeInventory();
            }
          }
        });
    // TODO: 称号付与実装後にTITLE、フィルター表示実装後にFILTER、アイテム検索実装後にSEARCHの分岐を追加する。
  }

  /**
   * クリックされた並び順が現在の並び順と異なる場合は、クリックされた並び順に並び替えて一覧画面を表示する。
   *
   * @param menu             クリックされたメニュー
   * @param currentOrderType 現在の並び順
   * @param player           クリックしたプレイヤー
   */
  private void openItemBookByOrderType(
      Menu menu, OrderType currentOrderType, Player player) {

    OrderType clickedOrderType = Menu.getOrderType(menu);
    if (!currentOrderType.equals(clickedOrderType)) {
      itemBookGuiManager.openItemBookGui(player, 0, clickedOrderType);
    }
  }

  /**
   * 前のページがある場合は前のページを開く。
   *
   * @param currentPage      現在のページ
   * @param player           クリックしたプレイヤー
   * @param currentOrderType 現在の並び順
   */
  private void goPrevPageIfPresent(
      int currentPage, Player player, OrderType currentOrderType) {

    if (currentPage != 0) {
      itemBookGuiManager.openItemBookGui(player, currentPage - 1, currentOrderType);
    }
  }

  /**
   * 次のページがある場合は次のページを開く。
   *
   * @param guiSettingStatus アイテム図鑑の設定状況
   * @param currentPage      現在のページ
   * @param player           クリックしたプレイヤー
   * @param currentOrderType 現在の並び順
   */
  private void goNextPageIfPresent(
      GuiSettingStatus guiSettingStatus, int currentPage, Player player, OrderType currentOrderType) {

    List<Material> currentItemList = guiSettingStatus.getCurrentItemList();
    int endPage = (int) Math.max(0, Math.ceil((double) currentItemList.size() / 36) - 1);
    if (currentPage != endPage) {
      itemBookGuiManager.openItemBookGui(player, currentPage + 1, currentOrderType);
    }
  }
}
