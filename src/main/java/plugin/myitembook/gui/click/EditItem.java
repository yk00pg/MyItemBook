package plugin.myitembook.gui.click;

import lombok.Getter;
import org.bukkit.Material;
import plugin.myitembook.gui.management.ItemDetailsIcon;

/**
 * アイテムの詳細情報の編集項目を扱うオブジェクト。
 */
@Getter
public class EditItem {

  private final Material material;
  private final ItemDetailsIcon itemDetailsIcon;

  public EditItem(Material material, ItemDetailsIcon itemDetailsIcon) {
    this.material = material;
    this.itemDetailsIcon = itemDetailsIcon;
  }
}
