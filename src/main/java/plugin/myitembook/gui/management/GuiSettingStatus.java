package plugin.myitembook.gui.management;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

/**
 * アイテム図鑑の設定状況を扱うオブジェクト。
 */
@Getter
public class GuiSettingStatus {

  private final int currentPage;
  private final OrderType currentOrderType;
  private final List<Material> currentItemList;
  @Setter
  private Material currentMaterial;

  public GuiSettingStatus(
      int currentPage, OrderType currentOrderType, List<Material> currentItemList, Material currentMaterial) {

    this.currentPage = currentPage;
    this.currentOrderType = currentOrderType;
    this.currentItemList = currentItemList;
    this.currentMaterial = currentMaterial;
  }
}
