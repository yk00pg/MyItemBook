package plugin.myitembook.gui.management;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plugin.myitembook.Main;
import plugin.myitembook.data.PlayerData;
import plugin.myitembook.item.ItemDetails;

/**
 * アイテム図鑑のアイテムの詳細画面を管理するクラス。
 */
public class ItemDetailsGuiManager {

  private final Main main;
  private final Map<UUID, PlayerData> playerDataMap;

  public ItemDetailsGuiManager(Main main, Map<UUID, PlayerData> playerDataMap) {

    this.main = main;
    this.playerDataMap = playerDataMap;
  }

  /**
   * アイテム図鑑のアイテムの詳細画面を開く。<br> UUIDをキーにして取得したプレイヤーデータマップからアイテムの詳細情報マップを取得する。<br> 9*3のチェスト型インベントリを作成し、現在選択中のアイテムの詳細情報の項目アイコンとメニューアイコンを設置する。<br>
   * アイテム図鑑の設定状況に現在選択中のアイテムを追加し、アイテム図鑑のアイテムの詳細画面を開く。
   *
   * @param player           プレイヤー
   * @param currentMaterial  現在選択中のアイテム
   * @param guiSettingStatus アイテム図鑑の設定状況
   */
  public void openItemDetailsGui(
      Player player, Material currentMaterial, GuiSettingStatus guiSettingStatus) {

    ItemDetails itemDetails =
        playerDataMap.get(player.getUniqueId()).getItemDetailsMap().get(currentMaterial);

    Inventory itemDetailsGui = Bukkit.createInventory(null, 9 * 3, "◆アイテム図鑑◆");

    setMaterialNameIcon(currentMaterial, itemDetailsGui);
    setItemDetailsIcons(itemDetails, itemDetailsGui);

    setMenuIcons(guiSettingStatus, currentMaterial, itemDetailsGui);

    guiSettingStatus.setCurrentMaterial(currentMaterial);
    player.openInventory(itemDetailsGui);
  }

  /**
   * 素材名を表示するアイコンを設置する。
   *
   * @param currentMaterial 現在選択中のアイテム
   * @param itemDetailsGui  アイテム図鑑のアイテムの詳細画面
   */
  private void setMaterialNameIcon(Material currentMaterial, Inventory itemDetailsGui) {
    ItemStack materialNameIcon = new ItemStack(currentMaterial);
    ItemMeta materialNameMeta = materialNameIcon.getItemMeta();

    if (materialNameMeta != null) {
      materialNameMeta.addItemFlags(ItemFlag.values());
      materialNameMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "【素材名】");
      materialNameMeta.setLore(
          List.of(ChatColor.WHITE + currentMaterial.name(),
              ChatColor.DARK_GRAY + "※この項目は変更できません"));
    }
    materialNameIcon.setItemMeta(materialNameMeta);
    itemDetailsGui.setItem(0, materialNameIcon);
  }

  /**
   * アイテムの詳細情報の項目アイコンを設置する。<br> アイテムの詳細情報が登録されている場合は登録内容を説明文として設定し、そうでない場合はデフォルトの文章を設定する。
   *
   * @param itemDetails    アイテムの詳細情報
   * @param itemDetailsGui アイテム図鑑のアイテムの詳細画面
   */
  private void setItemDetailsIcons(ItemDetails itemDetails, Inventory itemDetailsGui) {
    boolean hasDisplayName = StringUtils.isNotBlank(itemDetails.getDisplayName());
    itemDetailsGui.setItem(
        ItemDetailsIcon.DISPLAY_NAME.getSlotNum(),
        ItemDetailsIcon.DISPLAY_NAME.toItemStack(hasDisplayName, itemDetails.getDisplayName()));

    boolean hasLore = StringUtils.isNotBlank(itemDetails.getLore());
    itemDetailsGui.setItem(
        ItemDetailsIcon.LORE.getSlotNum(),
        ItemDetailsIcon.LORE.toItemStack(hasLore, itemDetails.getLore()));

    boolean hasCategory = StringUtils.isNotBlank(itemDetails.getCategory());
    itemDetailsGui.setItem(
        ItemDetailsIcon.CATEGORY.getSlotNum(),
        ItemDetailsIcon.CATEGORY.toItemStack(hasCategory, itemDetails.getCategory()));

    boolean hasDifficulty = itemDetails.getDifficulty() > 0;
    itemDetailsGui.setItem(
        ItemDetailsIcon.DIFFICULTY.getSlotNum(),
        ItemDetailsIcon.DIFFICULTY.toItemStack(hasDifficulty, String.valueOf(itemDetails.getDifficulty())));

    boolean hasHowToGet = StringUtils.isNotBlank(itemDetails.getHowToGet());
    itemDetailsGui.setItem(
        ItemDetailsIcon.HOW_TO_GET.getSlotNum(),
        ItemDetailsIcon.HOW_TO_GET.toItemStack(hasHowToGet, itemDetails.getHowToGet()));

    boolean hasHowToUse = StringUtils.isNotBlank(itemDetails.getHowToUse());
    itemDetailsGui.setItem(
        ItemDetailsIcon.HOW_TO_USE.getSlotNum(),
        ItemDetailsIcon.HOW_TO_USE.toItemStack(hasHowToUse, itemDetails.getHowToUse()));

    boolean hasMemo = StringUtils.isNotBlank(itemDetails.getMemo());
    itemDetailsGui.setItem(
        ItemDetailsIcon.MEMO.getSlotNum(),
        ItemDetailsIcon.MEMO.toItemStack(hasMemo, itemDetails.getMemo()));
  }

  /**
   * アイテム図鑑のアイテムの詳細画面にメニューアイコンを設置する。<br> 無効になり得るアイコンについては、状態を確認して表示する説明文を設定する。<br> 3行目（[18]〜[26]スロット）に順番に設置する。
   *
   * @param guiSettingStatus アイテム図鑑の設定状況
   * @param currentMaterial  現在選択中のアイテム
   * @param itemDetailsGui   アイテムの詳細画面
   */
  private void setMenuIcons(
      GuiSettingStatus guiSettingStatus, Material currentMaterial, Inventory itemDetailsGui) {

    List<Material> currentItemList = guiSettingStatus.getCurrentItemList();
    int currentIndex = currentItemList.indexOf(currentMaterial);
    List<ItemStack> menuIconList = new LinkedList<>();

    menuIconList.add(Menu.UNRELEASED.convertToItemStack());  // TITLE
    menuIconList.add(Menu.SAVE.convertToItemStack(false, ""));
    menuIconList.add(Menu.LIST.convertToItemStack(false, ""));
    menuIconList.add(Menu.SORT.convertToItemStack(false, ""));
    menuIconList.add(Menu.UNRELEASED.convertToItemStack());  // FILTER
    menuIconList.add(Menu.UNRELEASED.convertToItemStack());  // SEARCH

    boolean isFirstItem = currentIndex == 0;
    boolean isLastItem = (currentIndex + 1) == currentItemList.size();
    menuIconList.add(Menu.PREV.convertToItemStack(
        isFirstItem, "これが最初のアイテムだよ", "前のアイテム"));
    menuIconList.add(Menu.NEXT.convertToItemStack(
        isLastItem, "これが最後のアイテムだよ", "次のアイテム"));

    menuIconList.add(Menu.BACK.convertToItemStack(false, ""));

    int i = 18;
    for (ItemStack menuIcon : menuIconList) {
      itemDetailsGui.setItem(i, menuIcon);
      i++;
    }
  }

  /**
   * 直前に開いていたアイテムの詳細画面を1.5秒後に再び開く。
   *
   * @param player           クリックしたプレイヤー
   * @param currentMaterial  直前に開いていたアイテム
   * @param guiSettingStatus アイテム図鑑の設定状況
   */
  public void reopenItemDetailsGui(
      Player player, Material currentMaterial, GuiSettingStatus guiSettingStatus) {

    Bukkit.getScheduler().runTaskLater(main, () ->
            openItemDetailsGui(player, currentMaterial, guiSettingStatus),
        30);
  }
}
