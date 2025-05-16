package plugin.myitembook.gui.click;

import lombok.Getter;
import org.bukkit.Material;
import plugin.myitembook.gui.management.DetailsMenu;

/**
 * アイテムの詳細情報の編集項目を扱うオブジェクト。
 */
@Getter
public class EditItem {

  private final Material material;
  private final DetailsMenu detailsMenu;

  public EditItem(Material material, DetailsMenu detailsMenu) {
    this.material = material;
    this.detailsMenu = detailsMenu;
  }
}
