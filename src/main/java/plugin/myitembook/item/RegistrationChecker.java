package plugin.myitembook.item;

import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import plugin.myitembook.Main;
import plugin.myitembook.data.PlayerData;

/**
 * 入手したアイテムの登録判定とメッセージ表示を行うクラス。
 */
public class RegistrationChecker {

  private final Main main;
  private final ItemRegistrator itemRegistrator;

  private final Map<UUID, PlayerData> playerDataMap;

  public RegistrationChecker(
      Main main, ItemRegistrator itemRegistrator, Map<UUID, PlayerData> playerDataMap) {

    this.main = main;
    this.itemRegistrator = itemRegistrator;
    this.playerDataMap = playerDataMap;
  }

  /**
   * 入手したアイテムが登録済みか判定し、登録されていない場合は登録してメッセージを表示する。<br> メッセージが見えるように開いているインベントリを閉じる。
   *
   * @param player   アイテムを入手したプレイヤー
   * @param material 入手したアイテム（素材）
   */
  public void checkAndHandleItemRegistration(Player player, Material material) {
    Map<Material, ItemDetails> itemDetailsMap = playerDataMap.get(player.getUniqueId()).getItemDetailsMap();

    if (itemDetailsMap.containsKey(material)) {
      return;
    }

    itemRegistrator.registerItemAndItemDetails(player, material);
    sendRegistrationMessage(player, material);

    Bukkit.getScheduler().runTaskLater(main, player :: closeInventory, 25);
  }

  /**
   * アイテムの登録メッセージを表示する。
   *
   * @param player   アイテムを入手したプレイヤー
   * @param material 入手したアイテム（素材）
   */
  private void sendRegistrationMessage(Player player, Material material) {
    player.sendMessage(
        ChatColor.GOLD + "" + ChatColor.BOLD + "[NEW] "
            + ChatColor.RESET + "新しいアイテム："
            + ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + material.name()
            + ChatColor.RESET + " を図鑑に登録しました。");
    player.sendMessage("図鑑を開いてアイテムの情報を追加しましょう！");
  }
}