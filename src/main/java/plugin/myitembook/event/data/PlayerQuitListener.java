package plugin.myitembook.event.data;

import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import plugin.myitembook.data.PlayerData;
import plugin.myitembook.data.PlayerDataSaver;

/**
 * プレイヤーがログアウトする際に発火するイベントのリスナークラス。
 */
public class PlayerQuitListener implements Listener {

  private final PlayerDataSaver playerDataSaver;
  private final Map<UUID, PlayerData> playerDataMap;

  public PlayerQuitListener(PlayerDataSaver playerDataSaver, Map<UUID, PlayerData> playerDataMap) {
    this.playerDataSaver = playerDataSaver;
    this.playerDataMap = playerDataMap;
  }

  @EventHandler
  public void onPlayerQuitEvent(PlayerQuitEvent e) {
    Player player = e.getPlayer();
    playerDataSaver.savePlayerItemBookData(player);
    playerDataMap.remove(player.getUniqueId());
  }
}