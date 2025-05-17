package plugin.myitembook.gui.management;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * アイテム図鑑のメニューを扱うenum。
 */
public enum Menu {

  /* 称号付与実装後に解放 : TITLE(Material.BLAZE_SPAWN_EGG, "メラメラ燃える称号確認", "取得済みの称号の確認画面に移動します"), */
  SAVE(Material.ARMADILLO_SPAWN_EGG,
      "くるりと丸まりデータ保存", "図鑑データをYAMLファイルに保存します"),
  LIST(Material.ALLAY_SPAWN_EGG,
      "ルンルン踊って一覧表示", "素材名(ID)順に一覧画面を表示します"),
  SORT(Material.BEE_SPAWN_EGG,
      "ブンブンせっせと登録順に並び替え", "新たに登録された順に一覧画面を表示します"),
  /* フィルター表示実装後に解放 : FILTER(Material.FOX_SPAWN_EGG, "どろんとフィルター表示に変身", "アイテムを絞り込んで一覧画面を表示します"), */
  /* アイテム検索実装後に解放 : SEARCH(Material.AXOLOTL_SPAWN_EGG, "図鑑内検索をにょろにょろお助け", "検索されたアイテムの詳細画面を表示します"), */
  PREV(Material.TURTLE_SPAWN_EGG,
      "のんびりのろのろ前へ戻る", "を開きます"),
  NEXT(Material.RABBIT_SPAWN_EGG,
      "ぴょんぴょん次へ飛び跳ねる", "を開きます"),
  CLOSE(Material.BAT_SPAWN_EGG,
      "パタパタンと図鑑を閉じる", "アイテム図鑑を閉じます"),
  BACK(Material.SALMON_SPAWN_EGG,
      "バシャバシャ一覧画面に戻る", "先ほど開いていた一覧画面に戻ります"),
  UNRELEASED(Material.TADPOLE_SPAWN_EGG,
      "??? (未解放の項目)", "今後のアップデートに乞うご期待！");

  @Getter
  private final Material material;
  private final String menuItem;
  private final String lore;

  Menu(Material material, String menuItem, String lore) {
    this.material = material;
    this.menuItem = menuItem;
    this.lore = lore;
  }

  /**
   * メニューの項目を取得する。<br> メニューが無効の場合はダークグレイで、そうでない場合は金色でメニュー項目を取得する。
   *
   * @param disabled 無効の場合
   * @return 文字の色をつけたメニュー項目
   */
  public String getMenuItem(boolean disabled) {
    return disabled
        ? ChatColor.DARK_GRAY + menuItem
        : ChatColor.GOLD + menuItem;
  }

  /**
   * メニューの説明文を取得する。<br> UNRELEASEDメニューの場合は、ダークグレイで説明文を設定する。<br> その他のメニューの場合は、無効ならダークグレイで差し替えた説明文を、そうでない場合は黄色で説明文を取得する。
   *
   * @param disabled     無効の場合
   * @param fallbackLore 無効の場合に差し替える説明文
   * @return 文字の色をつけた説明文
   */
  public List<String> getLore(
      boolean disabled, String fallbackLore) {

    if (this == Menu.UNRELEASED) {
      return List.of(ChatColor.DARK_GRAY + lore);
    }
    return List.of(disabled
        ? ChatColor.DARK_GRAY + fallbackLore
        : ChatColor.YELLOW + lore);
  }

  /**
   * PREV、NEXTメニュー用の説明文を取得する。<br> メニューが無効ならダークグレイで差し替えた説明文を、そうでない場合は白色で表示している画面に合わせた文言+説明文を取得する。
   *
   * @param disabled     無効の場合
   * @param fallbackLore 無効の場合に差し替える説明文
   * @param stringByGui  表示しているGUIに合わせた文言
   * @return 文字の色をつけた説明文
   */
  public List<String> getLore(
      boolean disabled, String fallbackLore, String stringByGui) {

    return List.of(disabled
        ? ChatColor.DARK_GRAY + fallbackLore
        : ChatColor.YELLOW + stringByGui + lore);
  }

  /**
   * UNRELEASEDメニュー用に素材をアイテムスタックに変換する。
   *
   * @return アイテム（スタック）
   */
  public ItemStack convertToItemStack() {
    return createItem(true, getLore(true, ""));
  }

  /**
   * 素材をアイテムスタックに変換する。
   *
   * @param disabled     無効の場合
   * @param fallbackLore 無効の場合に差し替える説明文
   * @return アイテム（スタック）
   */
  public ItemStack convertToItemStack(boolean disabled, String fallbackLore) {
    return createItem(disabled, getLore(disabled, fallbackLore));
  }

  /**
   * PREV、NEXTメニュー用に素材をアイテムスタックに変換する。
   *
   * @param disabled     無効の場合
   * @param fallbackLore 無効の場合に差し替える説明文
   * @param stringByGui  表示している画面に合わせた文言
   * @return アイテム（スタック）
   */
  public ItemStack convertToItemStack(
      boolean disabled, String fallbackLore, String stringByGui) {

    return createItem(disabled, getLore(disabled, fallbackLore, stringByGui));
  }

  /**
   * 素材をアイテムスタックに変換し、表示名と説明文を設定して取得する。
   *
   * @param disabled 無効の場合
   * @param loreList 説明文
   * @return 表示名と説明文を設定したアイテム（スタック）
   */
  private ItemStack createItem(boolean disabled, List<String> loreList) {
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.addItemFlags(ItemFlag.values());
      meta.setDisplayName(getMenuItem(disabled));
      meta.setLore(loreList);
      item.setItemMeta(meta);
    }
    return item;
  }

  /**
   * 合致する素材を持ったメニューを返す。
   *
   * @param material 素材
   * @return メニュー
   */
  public static Optional<Menu> getFilteredMenu(Material material) {
    return Arrays.stream(values())
        .filter(menu -> menu.material == material)
        .findFirst();
  }

  /**
   * メニューに応じた並び順を返す。
   *
   * @param menu メニュー
   * @return 並び順
   */
  public static OrderType getOrderType(Menu menu) {
    if (menu.equals(Menu.LIST)) {
      return OrderType.ASC_ORDER;
    } else if (menu.equals(Menu.SORT)) {
      return OrderType.REGISTRATION_ORDER;
    }
    return OrderType.ASC_ORDER;
  }
}
