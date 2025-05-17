package plugin.myitembook.data;

import java.io.File;
import java.util.Map;
import lombok.Getter;
import org.bukkit.Material;
import plugin.myitembook.item.ItemDetails;

/**
 * プレイヤーデータ（アイテムの詳細情報マップ、アイテム図鑑データファイル）を扱うオブジェクト。
 */
@Getter
public class PlayerData {

  private final Map<Material, ItemDetails> itemDetailsMap;
  private final File playerFile;

  public PlayerData(Map<Material, ItemDetails> itemDetailsMap, File playerFile) {
    this.itemDetailsMap = itemDetailsMap;
    this.playerFile = playerFile;
  }
}