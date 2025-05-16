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
 * アイテム図鑑GUI（アイテムの詳細画面）を管理するクラス。
 */
public class ItemDetailsGuiManager {

  private final Main main;
  private final Map<UUID, PlayerData> playerDataMap;

  public ItemDetailsGuiManager(Main main, Map<UUID, PlayerData> playerDataMap) {

    this.main = main;
    this.playerDataMap = playerDataMap;
  }

  /**
   * <p>プレイヤーデータからアイテムの詳細情報一覧を取得する。
   * <p>9*3のチェスト型GUIを作成し、現在選択中のアイテムの詳細情報を設定したアイコンを設置する。
   * アイテム図鑑の設定状況に現在選択中のアイテムを追加し、GUIを開く。
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
    setDetailsEditIcons(itemDetails, itemDetailsGui);

    setMenuIcons(guiSettingStatus, currentMaterial, itemDetailsGui);

    guiSettingStatus.setCurrentMaterial(currentMaterial);
    player.openInventory(itemDetailsGui);
  }

  /**
   * 素材名を表示するアイコンを設置する。
   *
   * @param currentMaterial 現在選択中のアイテム
   * @param itemDetailsGui  アイテムの詳細画面
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
   * アイテムの詳細情報を編集するアイコンを設置する。
   *
   * @param itemDetails    アイテムの詳細情報
   * @param itemDetailsGui アイテムの詳細画面
   */
  private void setDetailsEditIcons(ItemDetails itemDetails, Inventory itemDetailsGui) {
    boolean hasDisplayName = StringUtils.isNotBlank(itemDetails.getDisplayName());
    itemDetailsGui.setItem(
        DetailsMenu.DISPLAY_NAME.getSlotNum(),
        DetailsMenu.DISPLAY_NAME.toItemStack(hasDisplayName, itemDetails.getDisplayName()));

    boolean hasLore = StringUtils.isNotBlank(itemDetails.getLore());
    itemDetailsGui.setItem(
        DetailsMenu.LORE.getSlotNum(),
        DetailsMenu.LORE.toItemStack(hasLore, itemDetails.getLore()));

    boolean hasCategory = StringUtils.isNotBlank(itemDetails.getCategory());
    itemDetailsGui.setItem(
        DetailsMenu.CATEGORY.getSlotNum(),
        DetailsMenu.CATEGORY.toItemStack(hasCategory, itemDetails.getCategory()));

    boolean hasDifficulty = itemDetails.getDifficulty() > 0;
    itemDetailsGui.setItem(
        DetailsMenu.DIFFICULTY.getSlotNum(),
        DetailsMenu.DIFFICULTY.toItemStack(hasDifficulty, String.valueOf(itemDetails.getDifficulty())));

    boolean hasHowToGet = StringUtils.isNotBlank(itemDetails.getHowToGet());
    itemDetailsGui.setItem(
        DetailsMenu.HOW_TO_GET.getSlotNum(),
        DetailsMenu.HOW_TO_GET.toItemStack(hasHowToGet, itemDetails.getHowToGet()));

    boolean hasHowToUse = StringUtils.isNotBlank(itemDetails.getHowToUse());
    itemDetailsGui.setItem(
        DetailsMenu.HOW_TO_USE.getSlotNum(),
        DetailsMenu.HOW_TO_USE.toItemStack(hasHowToUse, itemDetails.getHowToUse()));

    boolean hasMemo = StringUtils.isNotBlank(itemDetails.getMemo());
    itemDetailsGui.setItem(
        DetailsMenu.MEMO.getSlotNum(),
        DetailsMenu.MEMO.toItemStack(hasMemo, itemDetails.getMemo()));
  }

  /**
   * メニューアイコンを設置する。
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

    menuIconList.add(Menu.UNRELEASED.toItemStack());  // TITLE
    menuIconList.add(Menu.SAVE.toItemStack(false, ""));
    menuIconList.add(Menu.LIST.toItemStack(false, ""));
    menuIconList.add(Menu.SORT.toItemStack(false, ""));
    menuIconList.add(Menu.UNRELEASED.toItemStack());  // FILTER
    menuIconList.add(Menu.UNRELEASED.toItemStack());  // SEARCH

    boolean isFirstItem = currentIndex == 0;
    boolean isLastItem = (currentIndex + 1) == currentItemList.size();
    menuIconList.add(Menu.PREV.toItemStack(
        isFirstItem, "これが最初のアイテムだよ", "前のアイテム"));
    menuIconList.add(Menu.NEXT.toItemStack(
        isLastItem, "これが最後のアイテムだよ", "次のアイテム"));

    menuIconList.add(Menu.BACK.toItemStack(false, ""));

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

    Bukkit.getScheduler().runTaskLater(main, Runnable ->
            openItemDetailsGui(player, currentMaterial, guiSettingStatus),
        30);
  }
}
