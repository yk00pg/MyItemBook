package plugin.myitembook.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugin.myitembook.item.ItemDetails;

/**
 * プレイヤーのアイテム図鑑データをYAMLファイルに書き出して保存するクラス。
 */
public class PlayerDataSaver {

  private final Map<UUID, PlayerData> playerDataMap;

  public PlayerDataSaver(Map<UUID, PlayerData> playerDataMap) {
    this.playerDataMap = playerDataMap;
  }

  /**
   * UUIDをキーにしてプレイヤーデータマップからアイテム図鑑データファイルを取得し、新しいアイテム図鑑データを書き出して保存する。
   *
   * @param player プレイヤー
   */
  public void savePlayerItemBookData(Player player) {
    PlayerData playerData = playerDataMap.get(player.getUniqueId());
    File playerFile = playerData.getPlayerFile();
    FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

    List<Map<String, Object>> itemBookInfoList = createItemBookInfoList(playerData);
    playerConfig.set(YamlFields.ROOT, itemBookInfoList);

    try {
      playerConfig.save(playerFile);
      player.sendMessage("アイテム図鑑データを保存しました。");
      Bukkit.getLogger().info("アイテム図鑑データを保存しました。");
    } catch (IOException ex) {
      player.sendMessage("アイテム図鑑データの保存に失敗しました: " + ex.getMessage());
      Bukkit.getLogger().severe("アイテム図鑑データの保存に失敗しました: " + ex.getMessage());
      Bukkit.getLogger().info("アイテム図鑑データ: " + itemBookInfoList);
    }
  }

  /**
   * アイテムの詳細情報マップを基に、アイテム図鑑データをマップリスト形式で作り替える。
   *
   * @return 作り替えたアイテム図鑑データマップリスト
   */
  @NotNull
  private List<Map<String, Object>> createItemBookInfoList(PlayerData playerData) {
    Map<Material, ItemDetails> itemDetailsMap = playerData.getItemDetailsMap();

    List<Map<String, Object>> itemBookInfoList = new ArrayList<>();
    for (Material material : itemDetailsMap.keySet()) {
      ItemDetails itemDetails = itemDetailsMap.get(material);
      if (itemDetails == null) {
        continue;
      }
      itemBookInfoList.add(createItemBookInfoMap(material, itemDetails));
    }
    return itemBookInfoList;
  }

  /**
   * YAMLファイルのフィールド名とアイテムの詳細情報を紐付けてアイテム図鑑データマップを作成する。
   *
   * @param material    素材
   * @param itemDetails アイテムの詳細情報
   * @return アイテム図鑑データマップ
   */
  @NotNull
  private Map<String, Object> createItemBookInfoMap(Material material, ItemDetails itemDetails) {
    Map<String, Object> itemBookInfoMap = new LinkedHashMap<>();
    itemBookInfoMap.put(YamlFields.MATERIAL, material.name());
    itemBookInfoMap.put(YamlFields.DISPLAY_NAME, itemDetails.getDisplayName());
    itemBookInfoMap.put(YamlFields.LORE, itemDetails.getLore());
    itemBookInfoMap.put(YamlFields.CATEGORY, itemDetails.getCategory());
    itemBookInfoMap.put(YamlFields.DIFFICULTY, itemDetails.getDifficulty());
    itemBookInfoMap.put(YamlFields.HOW_TO_GET, itemDetails.getHowToGet());
    itemBookInfoMap.put(YamlFields.HOW_TO_USE, itemDetails.getHowToUse());
    itemBookInfoMap.put(YamlFields.MEMO, itemDetails.getMemo());
    itemBookInfoMap.put(YamlFields.REGISTRATION_NUMBER, itemDetails.getRegistrationNumber());
    return itemBookInfoMap;
  }
}
