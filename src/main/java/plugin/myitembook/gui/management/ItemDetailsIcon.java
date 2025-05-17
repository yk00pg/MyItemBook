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
 * アイテムの詳細画面の詳細情報の項目アイコンを扱うenum。
 */
public enum ItemDetailsIcon {
  DISPLAY_NAME(2, Material.PIG_SPAWN_EGG, "【表示名】", ChatColor.LIGHT_PURPLE,
      "の表示名をチャット欄に入力してください。"),
  LORE(3, Material.SHEEP_SPAWN_EGG, "【説明文】", ChatColor.LIGHT_PURPLE,
      "の説明文をチャット欄に入力してください。"),
  CATEGORY(4, Material.CAT_SPAWN_EGG, "【カテゴリ】", ChatColor.LIGHT_PURPLE,
      "のカテゴリ名をチャット欄に入力してください。"),
  DIFFICULTY(5, Material.POLAR_BEAR_SPAWN_EGG, "【入手難易度】", ChatColor.LIGHT_PURPLE,
      "の入手難易度を1〜5などの半角数字でチャット欄に入力してください。"),
  HOW_TO_GET(6, Material.OCELOT_SPAWN_EGG, "【入手方法】", ChatColor.LIGHT_PURPLE,
      "の入手方法をチャット欄に入力してください。"),
  HOW_TO_USE(7, Material.WOLF_SPAWN_EGG, "【使い方】", ChatColor.LIGHT_PURPLE,
      "の使い方をチャット欄に入力してください。"),
  MEMO(8, Material.CAMEL_SPAWN_EGG, "【メモ】", ChatColor.LIGHT_PURPLE,
      "のメモをチャット欄に自由に入力してください。");

  private static final List<String> UNREGISTERED_LORE = List.of(
      ChatColor.DARK_PURPLE + "※この項目はまだ登録されていません。",
      ChatColor.DARK_PURPLE + "　クリックして登録しましょう！");

  @Getter
  private final int slotNum;
  @Getter
  private final Material material;
  @Getter
  private final String detailsItem;
  private final ChatColor chatColor;
  @Getter
  private final String inputInstruction;

  ItemDetailsIcon(
      int slotNum, Material material, String detailsItem, ChatColor chatColor, String inputInstruction) {

    this.slotNum = slotNum;
    this.material = material;
    this.detailsItem = detailsItem;
    this.chatColor = chatColor;
    this.inputInstruction = inputInstruction;
  }

  /**
   * 文字の色と編集項目を取得する。
   *
   * @return 文字の色をつけた編集項目
   */
  public String getDetailsItemAndColor() {
    return chatColor + detailsItem;
  }

  /**
   * 該当の詳細情報が登録済みの場合は白色で差し替えた説明文を、未登録の場合はデフォルトの色と説明文を取得する。
   *
   * @param registered   登録済みの場合
   * @param fallbackLore 登録済みの場合に差し替える説明文
   * @return 文字の色をつけた説明文
   */
  public List<String> getDefaultLore(boolean registered, String fallbackLore) {
    return registered
        ? List.of(ChatColor.WHITE + fallbackLore)
        : UNREGISTERED_LORE;
  }

  /**
   * 素材をアイテムスタックに変換し、表示名と説明文を設定して取得する。
   *
   * @param registered   登録済みの場合
   * @param fallbackLore 登録済みの場合に差し替える説明文
   * @return 表示名と説明文を設定したアイテム（スタック）
   */
  public ItemStack toItemStack(boolean registered, String fallbackLore) {
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.addItemFlags(ItemFlag.values());
      meta.setDisplayName(getDetailsItemAndColor());
      meta.setLore(getDefaultLore(registered, fallbackLore));
    }
    item.setItemMeta(meta);
    return item;
  }

  /**
   * 合致する素材を持った詳細情報の項目アイコンを取得する。
   *
   * @param material 素材
   * @return 詳細情報の項目アイコン
   */
  public static Optional<ItemDetailsIcon> getFilteredItemDetailsIcon(Material material) {
    return Arrays.stream(values())
        .filter(itemDetailsIcon -> itemDetailsIcon.material == material)
        .findFirst();
  }
}