package plugin.myitembook.item;

import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import plugin.myitembook.data.PlayerData;
import plugin.myitembook.gui.click.EditItem;
import plugin.myitembook.gui.management.DetailsMenu;
import plugin.myitembook.gui.management.GuiSettingStatus;
import plugin.myitembook.gui.management.ItemDetailsGuiManager;

/**
 * アイテムの詳細情報の登録・編集を行うクラス。
 */
@Getter
public class ItemRegistrator {

  private final ItemDetailsGuiManager itemDetailsGuiManager;
  private final Map<UUID, PlayerData> playerDataMap;

  public ItemRegistrator(
      ItemDetailsGuiManager itemDetailsGuiManager, Map<UUID, PlayerData> playerDataMap) {

    this.itemDetailsGuiManager = itemDetailsGuiManager;
    this.playerDataMap = playerDataMap;
  }

  /**
   * 新たに入手したアイテムの詳細情報のデフォルト値を図鑑に登録する。
   *
   * @param material 入手したアイテム（素材）
   * @param player   入手したプレイヤー
   */
  public void registerItemAndItemDetails(Player player, Material material) {
    Map<Material, ItemDetails> itemDetailsMap = playerDataMap.get(player.getUniqueId()).getItemDetailsMap();

    int registrationNumber = itemDetailsMap.size() + 1;
    itemDetailsMap.put(material, new ItemDetails(
        " ",
        " ",
        " ",
        0,
        " ",
        " ",
        " ",
        registrationNumber
    ));
  }

  /**
   * クリックされたアイコンに応じてアイテムの詳細情報を編集してメッセージを送信し、アイテムの詳細画面に戻る。
   *
   * @param editItem         編集項目
   * @param player           チャット入力をしたプレイヤー
   * @param inputContent     入力内容
   * @param currentMaterial  直前に詳細画面を開いていたアイテム
   * @param guiSettingStatus アイテム図鑑の設定状況
   */
  public void editItemBasedOnClick(
      EditItem editItem, Player player, String inputContent,
      Material currentMaterial, GuiSettingStatus guiSettingStatus) {

    Material material = editItem.getMaterial();
    DetailsMenu detailsMenu = editItem.getDetailsMenu();
    ItemDetails itemDetails = playerDataMap.get(player.getUniqueId()).getItemDetailsMap().get(material);

    boolean isCompletedRegistration = true;
    switch (detailsMenu) {
      case DISPLAY_NAME -> itemDetails.updateDisplayName(inputContent);
      case LORE -> itemDetails.updateLore(inputContent);
      case CATEGORY -> itemDetails.updateCategory(inputContent);
      case DIFFICULTY -> isCompletedRegistration = itemDetails.updateDifficulty(inputContent, player);
      case HOW_TO_GET -> itemDetails.updateHowToGet(inputContent);
      case HOW_TO_USE -> itemDetails.updateHowToUse(inputContent);
      case MEMO -> itemDetails.updateMemo(inputContent);
    }

    sendRegistrationMessage(player, inputContent, isCompletedRegistration, material, detailsMenu);
    itemDetailsGuiManager.reopenItemDetailsGui(player, currentMaterial, guiSettingStatus);
  }

  /**
   * 登録が完了した場合、入力内容に応じて登録または削除メッセージを表示する。
   *
   * @param player                  チャット入力をしたプレイヤー
   * @param inputContent            入力内容
   * @param isCompletedRegistration 登録の成否
   * @param material                登録内容を編集したアイテム
   * @param detailsMenu             詳細情報メニュー
   */
  private void sendRegistrationMessage(
      Player player, String inputContent, boolean isCompletedRegistration,
      Material material, DetailsMenu detailsMenu) {

    if (isCompletedRegistration) {
      String action = inputContent.equals("delete") ? "削除" : "登録";
      player.sendMessage(ChatColor.LIGHT_PURPLE + material.name() + ChatColor.RESET + "の"
          + ChatColor.GOLD + detailsMenu.getDetailsItem() + ChatColor.RESET + "を"
          + action + "しました。");
    }
  }
}
