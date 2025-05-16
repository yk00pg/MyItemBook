package plugin.myitembook.event.data;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import plugin.myitembook.data.PlayerDataLoader;

/**
 * プレイヤーが参加した際に発火するイベントのリスナークラス。
 */
public class PlayerJoinListener implements Listener {

  private final PlayerDataLoader playerDataLoader;

  public PlayerJoinListener(PlayerDataLoader playerDataLoader) {
    this.playerDataLoader = playerDataLoader;
  }

  @EventHandler
  public void onPlayerJoinEvent(PlayerJoinEvent e) {
    Player player = e.getPlayer();
    playerDataLoader.loadYamlFile(player);
  }
}
