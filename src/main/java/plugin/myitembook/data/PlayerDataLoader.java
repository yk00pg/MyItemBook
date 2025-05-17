package plugin.myitembook.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugin.myitembook.item.ItemDetails;

/**
 * プレイヤーのアイテム図鑑データをYAMLファイルから読み込むクラス。<br>
 */
@Getter
public class PlayerDataLoader {

  private final JavaPlugin plugin;
  private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

  public PlayerDataLoader(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  /**
   * ログインしたプレイヤーのアイテム図鑑データファイル（YAMLファイル）を読み込む。<br> 本プラグインのデータフォルダ、データファイルの順に参照し、存在しなければ生成して取得する。<br> データファイルを読み込み、データの値を取得する。<br>
   * UUIDとプレイヤーデータ（アイテムの詳細情報マップ、アイテム図鑑データファイル）を紐付けてプレイヤーデータマップを作成する。
   *
   * @param player ログインしたプレイヤー
   */
  public void loadYamlFile(Player player) {
    UUID uuid = player.getUniqueId();
    File parentDir = getOrCreateDataFolder();
    if (parentDir == null) {
      return;
    }

    File playerFile = getOrCreateFile(parentDir, uuid);
    if (playerFile == null) {
      return;
    }

    FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

    Map<Material, ItemDetails> itemDetailsMap = new HashMap<>();
    loadItemEntriesFromFile(playerConfig, itemDetailsMap);
    playerDataMap.put(uuid, new PlayerData(itemDetailsMap, playerFile));
  }

  /**
   * 本プラグインのデータフォルダを参照し、存在しなければ生成して取得する。
   *
   * @return 本プラグインのデータフォルダ
   */
  private File getOrCreateDataFolder() {
    File parentDir = plugin.getDataFolder();
    if (!parentDir.exists()) {
      try {
        parentDir.mkdirs();
        Bukkit.getLogger().info("プラグインのデータフォルダを作成しました: " + parentDir.getPath());
      } catch (Exception ex) {
        Bukkit.getLogger().severe("プラグインのデータフォルダの作成に失敗しました: " + ex.getMessage());
        return null;
      }
    }
    return parentDir;
  }

  /**
   * アイテム図鑑データファイルの存在を参照し、存在しなければ生成して取得する。
   *
   * @param parentDir 本プラグインのデータフォルダ
   * @param uuid      ログインしたプレイヤーのUUID
   * @return アイテム図鑑データファイル
   */
  private File getOrCreateFile(File parentDir, UUID uuid) {
    File playerFile = new File(parentDir, uuid + ".yml");
    if (!(playerFile.exists())) {
      try {
        playerFile.createNewFile();
        Bukkit.getLogger().info("新しいアイテム図鑑データファイルを作成しました: " + playerFile.getName());
      } catch (IOException ex) {
        Bukkit.getLogger().severe("ファイルの作成に失敗しました: " + ex.getMessage());
        return null;
      }
    }
    return playerFile;
  }

  /**
   * アイテム図鑑データをルートフィールドからマップリスト形式で取得し、各フィールドの値を取得する。<br> 素材とアイテムの詳細情報を紐付けて、アイテムの詳細情報マップに追加する。
   *
   * @param playerConfig   アイテム図鑑データ
   * @param itemDetailsMap アイテムの詳細情報マップ
   */
  private void loadItemEntriesFromFile(
      FileConfiguration playerConfig, Map<Material, ItemDetails> itemDetailsMap) {

    List<Map<?, ?>> itemBookInfoList = playerConfig.getMapList(YamlFields.ROOT);
    for (Map<?, ?> itemEntry : itemBookInfoList) {

      String materialName = String.valueOf(itemEntry.get(YamlFields.MATERIAL));
      Material material = parseAndGetMaterial(itemEntry, materialName);
      if (material == null) {
        Bukkit.getLogger().warning("Invalid material: " + materialName);
        continue;
      }

      ItemDetails itemDetails = parseAndGetItemDetails(itemEntry, material);
      itemDetailsMap.put(material, itemDetails);
    }
  }

  /**
   * 素材名が空でない場合、値を素材に変換して取得する。
   *
   * @param itemEntry    アイテム図鑑データのエントリ（フィールド名と値のマップ）
   * @param materialName 素材名
   * @return 素材
   */
  @Nullable
  private Material parseAndGetMaterial(Map<?, ?> itemEntry, String materialName) {
    if (StringUtils.isBlank(materialName)) {
      Bukkit.getLogger().warning("Material field is missing or blank: " + itemEntry);
      return null;
    }
    return Material.matchMaterial(materialName);
  }

  /**
   * 各フィールドの値を適切な型に変換して取得し、アイテムの詳細情報としてまとめる。
   *
   * @param itemEntry アイテム図鑑データのエントリ（フィールド名と値のマップ）
   * @param material  素材
   * @return アイテムの詳細情報
   */
  @NotNull
  private ItemDetails parseAndGetItemDetails(Map<?, ?> itemEntry, Material material) {
    String displayName =
        StringUtils.defaultIfBlank((String) itemEntry.get(YamlFields.DISPLAY_NAME), " ");

    String lore =
        StringUtils.defaultIfBlank((String) itemEntry.get(YamlFields.LORE), " ");

    String category =
        StringUtils.defaultIfBlank((String) itemEntry.get(YamlFields.CATEGORY), " ");

    int difficulty;
    if (itemEntry.get(YamlFields.DIFFICULTY) instanceof Number) {
      difficulty = ((Number) itemEntry.get(YamlFields.DIFFICULTY)).intValue();
    } else {
      difficulty = 0;
      Bukkit.getLogger().warning(
          "Invalid difficulty of this: " + material.name() + "- Setting default to 0");
    }

    String howToGet =
        StringUtils.defaultIfBlank((String) itemEntry.get(YamlFields.HOW_TO_GET), " ");

    String howToUse =
        StringUtils.defaultIfBlank((String) itemEntry.get(YamlFields.HOW_TO_USE), " ");

    String memo =
        StringUtils.defaultIfBlank((String) itemEntry.get(YamlFields.MEMO), " ");

    int registrationNumber;
    if (itemEntry.get(YamlFields.REGISTRATION_NUMBER) instanceof Number) {
      registrationNumber = ((Number) itemEntry.get(YamlFields.REGISTRATION_NUMBER)).intValue();
    } else {
      registrationNumber = 0;
      Bukkit.getLogger().warning(
          "Invalid registrationNumber for this: " + material.name() + "- Setting default to 0");
    }

    return new ItemDetails(
        displayName, lore, category, difficulty, howToGet, howToUse, memo, registrationNumber);
  }
}