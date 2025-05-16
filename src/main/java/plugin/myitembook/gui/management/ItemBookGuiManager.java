package plugin.myitembook.gui.management;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import plugin.myitembook.Main;
import plugin.myitembook.data.PlayerData;
import plugin.myitembook.item.ItemDetails;

/**
 * アイテム図鑑GUI（一覧画面）を管理するクラス。
 */
@Getter
public class ItemBookGuiManager {

  private final Main main;
  private final Map<UUID, PlayerData> playerDataMap;
  private final Map<UUID, GuiSettingStatus> playerCurrentGuiStatusMap = new HashMap<>();

  public ItemBookGuiManager(Main main, Map<UUID, PlayerData> playerDataMap) {
    this.main = main;
    this.playerDataMap = playerDataMap;
  }

  /**
   * <p>UUIDをキーにしてプレイヤーの登録済みアイテム一覧を取得し、並び順やアイテムの数を調整する。
   * <p>9*6のチェスト型GUIを作成し、アイテムとメニューアイコンを設置する。
   * UUIDをキーにページ、並び順、アイテム一覧をアイテム図鑑の設定状況マップに格納し、GUIを開く。
   *
   * @param player プレイヤー
   */
  public void openItemBookGui(
      Player player, Integer currentPage, OrderType currentOrderType) {

    UUID uuid = player.getUniqueId();
    Map<Material, ItemDetails> itemDetailsMap = playerDataMap.get(uuid).getItemDetailsMap();

    List<Material> sortedItemList = getAndSortItemList(currentOrderType, itemDetailsMap);
    List<Material> itemList = adjustSizeOfItemList(sortedItemList, currentPage);

    Inventory itemBookGui = Bukkit.createInventory(null, 9 * 6, "◆アイテム図鑑◆");
    setItems(itemList, itemDetailsMap, itemBookGui);
    setMenuIcons(itemBookGui, currentPage, sortedItemList, currentOrderType);

    playerCurrentGuiStatusMap.put(
        uuid,
        new GuiSettingStatus(currentPage, currentOrderType, sortedItemList, null));

    player.openInventory(itemBookGui);
  }

  /**
   * UUIDをキーにプレイヤーの登録済みアイテム一覧を取得し、選択された並び順を基に並び替えた一覧をリスト形式で返す。
   *
   * @return 並び替えた登録済みアイテム一覧
   */
  @NotNull
  private List<Material> getAndSortItemList(
      OrderType currentOrderType, Map<Material, ItemDetails> itemDetailsMap) {

    List<Material> sortedItemList = new ArrayList<>(itemDetailsMap.keySet());

    switch (currentOrderType) {
      case OrderType.REGISTRATION_ORDER:
        sortedItemList.sort(
            Comparator.comparing((Material material) -> {
              ItemDetails itemDetails = itemDetailsMap.get(material);
              return itemDetails != null
                  ? itemDetails.getRegistrationNumber()
                  : Integer.MAX_VALUE;
            }).reversed());
        break;
      case ASC_ORDER:
      default:
        sortedItemList.sort(Comparator.comparing(Material :: name));
    }
    return sortedItemList;
  }

  /**
   * <p>並び替えた登録済みアイテム一覧の数をページ内に収まるように調整し、GUI用アイテム一覧をリスト形式で返す。
   * <p>開こうとしているページからはみ出る場合は、収まる分だけリストに入れる。
   * 開こうとしているページのスロットが余る場合は、BEDROCKで埋める。
   *
   * @param sortedItemList 並び替えた登録済みアイテム一覧
   * @return GUI用アイテム一覧
   */
  @NotNull
  private List<Material> adjustSizeOfItemList(List<Material> sortedItemList, Integer currentPage) {
    int startIndex = currentPage * 36;
    int endIndex = startIndex + 36;

    if (sortedItemList.size() > endIndex) {
      return sortedItemList.subList(startIndex, endIndex);
    }

    List<Material> ajustedItemList = new ArrayList<>(sortedItemList);
    while (ajustedItemList.size() < endIndex) {
      ajustedItemList.add(Material.BEDROCK);
    }
    return ajustedItemList.subList(startIndex, endIndex);
  }

  /**
   * GUI用アイテム一覧を一覧画面に設置する。
   *
   * @param itemList       GUI用アイテム一覧
   * @param itemDetailsMap 登録済みアイテムの詳細情報一覧
   * @param itemBookGui    一覧画面
   */
  private void setItems(
      List<Material> itemList, Map<Material, ItemDetails> itemDetailsMap, Inventory itemBookGui) {

    for (int i = 0; i < itemList.size(); i++) {
      Material material = itemList.get(i);
      ItemStack itemStack = new ItemStack(material);
      setDisplayNameAndLore(itemDetailsMap, itemStack, material);

      itemBookGui.setItem(i, itemStack);
    }
  }

  /**
   * <p>アイテムのメタ情報を設定する。
   * BEDROCK（未登録）と登録済みアイテムの場合に分岐して設定メソッドを呼び出す。
   *
   * @param itemDetailsMap 登録済みアイテムの詳細情報一覧
   * @param itemStack      アイテム（スタック）
   * @param material       アイテム（素材）
   */
  private void setDisplayNameAndLore(
      Map<Material, ItemDetails> itemDetailsMap, ItemStack itemStack, Material material) {

    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta == null) {
      return;
    }
    if (setToBedrock(itemStack, material, itemMeta)) {
      return;
    }

    setToRegisteredItem(itemDetailsMap, itemStack, material, itemMeta);
  }

  /**
   * BEDROCK（未登録）の場合の表示名と説明文を設定する。
   *
   * @param itemStack アイテム（スタック）
   * @param material  アイテム（素材）
   * @param itemMeta  アイテムのメタ情報
   * @return BEDROCKかどうか
   */
  private boolean setToBedrock(ItemStack itemStack, Material material, ItemMeta itemMeta) {
    if (material == Material.BEDROCK) {
      itemMeta.setDisplayName(ChatColor.GRAY + "" + ChatColor.ITALIC + "??? (謎のアイテム)");
      itemMeta.setLore(List.of(ChatColor.GRAY + "" + ChatColor.ITALIC + "???????????????"));
      itemStack.setItemMeta(itemMeta);
      return true;
    }
    return false;
  }

  /**
   * <p>登録済みアイテムに表示名と説明文を設定する。
   *
   * @param itemDetailsMap アイテムの詳細情報一覧
   * @param itemStack      アイテム（スタック）
   * @param material       アイテム（素材）
   * @param itemMeta       アイテムのメタ情報
   */
  private void setToRegisteredItem(
      Map<Material, ItemDetails> itemDetailsMap, ItemStack itemStack,
      Material material, ItemMeta itemMeta) {

    ItemDetails itemDetails = itemDetailsMap.get(material);
    setDisplayNameForRegisteredItem(material, itemMeta, itemDetails);
    setLoreForRegisteredItem(itemMeta, itemDetails);

    itemMeta.addItemFlags(ItemFlag.values());
    itemStack.setItemMeta(itemMeta);
  }

  /**
   * <p>登録済みアイテムに表示名を設定する。
   * アイテムの詳細情報として登録されている場合は登録内容を、未登録の場合は「素材名（表示名未登録）」を設定する。
   *
   * @param material    アイテム（素材）
   * @param itemMeta    アイテムのメタ情報
   * @param itemDetails アイテムの詳細情報
   */
  private void setDisplayNameForRegisteredItem(
      Material material, ItemMeta itemMeta, ItemDetails itemDetails) {

    String displayName = itemDetails.getDisplayName();
    itemMeta.setDisplayName(
        StringUtils.isNotBlank(displayName)
            ? ChatColor.LIGHT_PURPLE + displayName
            : ChatColor.RED + material.name() + " (表示名未登録)");
  }

  /**
   * <p>登録済みアイテムの説明文を設定する。
   * アイテムの詳細情報として登録されている場合は登録内容を設定し、未登録の場合は何も設定しない。
   *
   * @param itemMeta    アイテムのメタ情報
   * @param itemDetails アイテムの詳細情報
   */
  private void setLoreForRegisteredItem(ItemMeta itemMeta, ItemDetails itemDetails) {
    String lore = itemDetails.getLore();
    if (StringUtils.isNotBlank(lore)) {
      itemMeta.setLore(List.of(ChatColor.WHITE + lore));
    }
  }

  /**
   * 一覧画面の6行目（[45]〜[53]）にメニューアイコンを設置する。
   *
   * @param itemBookGui 一覧画面
   */
  private void setMenuIcons(Inventory itemBookGui, Integer currentPage,
      List<Material> sortedItemList, OrderType currentOrderType) {

    List<ItemStack> menuIconList = new LinkedList<>();
    int endPage = (int) Math.max(0, Math.ceil((double) sortedItemList.size() / 36) - 1);

    menuIconList.add(Menu.UNRELEASED.toItemStack());  // TODO: 称号付与実装後、TITLEに差し替える
    menuIconList.add(Menu.SAVE.toItemStack(false, ""));

    boolean isAscOrder = currentOrderType == OrderType.ASC_ORDER;
    boolean isRegistrationOrder = currentOrderType == OrderType.REGISTRATION_ORDER;
    String fallbackLore = "現在表示している並び順です";
    menuIconList.add(Menu.LIST.toItemStack(isAscOrder, fallbackLore));
    menuIconList.add(Menu.SORT.toItemStack(isRegistrationOrder, fallbackLore));

    menuIconList.add(Menu.UNRELEASED.toItemStack());  // TODO: フィルター表示実装後、FILTERに差し替える
    menuIconList.add(Menu.UNRELEASED.toItemStack());  // TODO: アイテム検索実装後、SEARCHに差し替える

    boolean isFirstPage = currentPage == 0;
    boolean isLastPage = currentPage == endPage;
    menuIconList.add(Menu.PREV.toItemStack(
        isFirstPage, "これが最初のページです", "前のページ"));
    menuIconList.add(Menu.NEXT.toItemStack(
        isLastPage, "これが最後のページです", "次のページ"));

    menuIconList.add(Menu.CLOSE.toItemStack(false, ""));

    int i = 45;
    for (ItemStack menuIcon : menuIconList) {
      itemBookGui.setItem(i, menuIcon);
      i++;
    }
  }

  /**
   * 直前に開いていた一覧画面を1.25秒後に再び開く。
   *
   * @param player           クリックしたプレイヤー
   * @param currentPage      直前に開いていた一覧画面のページ
   * @param currentOrderType 直前に開いていた一覧画面の並び順
   */
  public void reopenItemBookGui(Player player, int currentPage, OrderType currentOrderType) {
    Bukkit.getScheduler().runTaskLater(main, Runnable ->
            openItemBookGui(player, currentPage, currentOrderType),
        25);
  }
}
