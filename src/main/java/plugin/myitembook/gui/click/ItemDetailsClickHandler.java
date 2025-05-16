package plugin.myitembook.gui.click;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import plugin.myitembook.data.PlayerDataSaver;
import plugin.myitembook.gui.management.DetailsMenu;
import plugin.myitembook.gui.management.GuiSettingStatus;
import plugin.myitembook.gui.management.ItemBookGuiManager;
import plugin.myitembook.gui.management.ItemDetailsGuiManager;
import plugin.myitembook.gui.management.Menu;
import plugin.myitembook.gui.management.OrderType;

/**
 * アイテム図鑑のアイテム詳細画面におけるクリックに応じた処理をするクラス。
 */
@Getter
public class ItemDetailsClickHandler {

  private final ItemBookGuiManager itemBookGuiManager;
  private final ItemDetailsGuiManager itemDetailsGuiManager;
  private final PlayerDataSaver playerDataSaver;

  private final Map<UUID, EditItem> awaitingInputMap = new HashMap<>();

  public ItemDetailsClickHandler(
      ItemBookGuiManager itemBookGuiManager,
      ItemDetailsGuiManager itemDetailsGuiManager,
      PlayerDataSaver playerDataSaver) {

    this.itemBookGuiManager = itemBookGuiManager;
    this.itemDetailsGuiManager = itemDetailsGuiManager;
    this.playerDataSaver = playerDataSaver;
  }

  /**
   * <p>クリックされたスロットに応じて分岐して処理を実行する。
   * <p>18未満（編集メニューが配置されているスロット）の場合はUUIDをキーに編集項目を入力待ちマップに格納し、チャット入力を待つ。
   * そうでない（メニューが配置されているスロット）場合はメニュー別処理のメソッドを呼び出す。
   *
   * @param clickedSlot     クリックされたスロット
   * @param player          クリックしたプレイヤー
   * @param clickedMaterial クリックされたアイテム
   * @param currentMaterial 現在詳細画面を開いているアイテム
   */
  public void handleItemDetailsClick(
      int clickedSlot, Player player, Material clickedMaterial,
      Material currentMaterial, GuiSettingStatus guiSettingStatus) {

    if (clickedSlot < 18) {
      DetailsMenu.fromMaterial(clickedMaterial).ifPresent(
          detailsMenu -> {
            awaitingInputMap.put(player.getUniqueId(), new EditItem(currentMaterial, detailsMenu));
            player.sendMessage(ChatColor.LIGHT_PURPLE + currentMaterial.name()
                + ChatColor.RESET + detailsMenu.getInputInstruction());
            player.sendMessage("登録中止は「cancel」、登録削除は「delete」と入力してください。");
            player.closeInventory();
          }
      );
    } else {
      handleMenuClick(guiSettingStatus, player, clickedMaterial, currentMaterial);
    }
  }

  /**
   * クリックされたアイテムからどのメニューかを判定し、処理を実行する。
   *
   * @param guiSettingStatus アイテム図鑑の設定状況
   * @param player           クリックしたプレイヤー
   * @param clickedMaterial  クリックされたアイテム
   */
  private void handleMenuClick(
      GuiSettingStatus guiSettingStatus, Player player, Material clickedMaterial, Material currentMaterial) {

    List<Material> currentItemList = guiSettingStatus.getCurrentItemList();
    Menu.fromMaterial(clickedMaterial).ifPresent(
        menuIcons -> {
          switch (menuIcons) {
            case SAVE -> {
              playerDataSaver.savePlayerItemBookData(player);
              player.closeInventory();
              itemDetailsGuiManager.reopenItemDetailsGui(player, currentMaterial, guiSettingStatus);
            }
            case LIST, SORT -> openItemBookByOrderType(menuIcons, player);
            case PREV -> goPrevItemIfPresent(currentItemList, currentMaterial, player, guiSettingStatus);
            case NEXT -> goNextItemIfPresent(currentItemList, currentMaterial, player, guiSettingStatus);
            case BACK -> {
              int currentPage = guiSettingStatus.getCurrentPage();
              OrderType currentOrderType = guiSettingStatus.getCurrentOrderType();
              itemBookGuiManager.openItemBookGui(player, currentPage, currentOrderType);
            }
          }
        });
    // TODO: 称号付与実装後にTITLE、フィルター表示実装後にFILTER、アイテム検索実装後にSEARCHの分岐を追加する。
  }

  /**
   * クリックされた並び順で一覧画面を表示する。
   *
   * @param menu   クリックされたメニュー
   * @param player クリックしたプレイヤー
   */
  private void openItemBookByOrderType(Menu menu, Player player) {
    itemBookGuiManager.openItemBookGui(player, 0, Menu.getOrderType(menu));
  }

  /**
   * 直近で開いていた一覧画面において、現在詳細画面を開いているアイテムのひとつ前に位置していたアイテムの詳細画面を表示する。
   *
   * @param currentItemList 一覧画面のアイテム一覧
   * @param currentMaterial 現在詳細画面を開いているアイテム
   * @param player          クリックしたプレイヤー
   */
  private void goPrevItemIfPresent(
      List<Material> currentItemList, Material currentMaterial,
      Player player, GuiSettingStatus guiSettingStatus) {

    int currentIndex = currentItemList.indexOf(currentMaterial);

    if (currentIndex != 0) {
      itemDetailsGuiManager.openItemDetailsGui(
          player, currentItemList.get(currentIndex - 1), guiSettingStatus);
    }
  }

  /**
   * 直近で開いていた一覧画面において、現在詳細画面を開いているアイテムの次に位置していたアイテムの詳細画面を表示する。
   *
   * @param currentItemList  一覧画面のアイテム一覧
   * @param currentMaterial  現在詳細画面を開いているアイテム
   * @param player           クリックしたプレイヤー
   * @param guiSettingStatus アイテム図鑑の設定状況
   */
  private void goNextItemIfPresent(
      List<Material> currentItemList, Material currentMaterial,
      Player player, GuiSettingStatus guiSettingStatus) {

    int currentIndex = currentItemList.indexOf(currentMaterial);

    if ((currentIndex + 1) != currentItemList.size()) {
      itemDetailsGuiManager.openItemDetailsGui(
          player, currentItemList.get(currentIndex + 1), guiSettingStatus);
    }
  }
}
