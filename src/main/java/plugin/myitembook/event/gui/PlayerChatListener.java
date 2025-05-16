package plugin.myitembook.event.gui;

import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import plugin.myitembook.gui.click.EditItem;
import plugin.myitembook.gui.management.GuiSettingStatus;
import plugin.myitembook.gui.management.ItemDetailsGuiManager;
import plugin.myitembook.item.ItemRegistrator;

/**
 * プレイヤーがチャット入力をした際に発火するイベントのリスナークラス。
 */
public class PlayerChatListener implements Listener {

  private final ItemDetailsGuiManager itemDetailsGuiManager;
  private final ItemRegistrator itemRegistrator;

  private final Map<UUID, GuiSettingStatus> playerCurrentGuiStatusMap;
  private final Map<UUID, EditItem> awaitingInputMap;

  public PlayerChatListener(
      ItemDetailsGuiManager itemDetailsGuiManager, ItemRegistrator itemRegistrator,
      Map<UUID, GuiSettingStatus> playerCurrentGuiStatusMap, Map<UUID, EditItem> awaitingInputMap) {

    this.itemDetailsGuiManager = itemDetailsGuiManager;
    this.itemRegistrator = itemRegistrator;
    this.playerCurrentGuiStatusMap = playerCurrentGuiStatusMap;
    this.awaitingInputMap = awaitingInputMap;
  }

  /**
   * <p>チャット入力を行ったプレイヤーのUUIDが入力待ちマップに格納されていたらアイテムの詳細情報の編集処理に進む。
   * 「cancel」と入力された場合は処理を中断し、アイテムの詳細画面に戻る。
   *
   * @param e イベント
   */
  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent e) {
    Player player = e.getPlayer();
    UUID uuid = player.getUniqueId();

    if (!awaitingInputMap.containsKey(uuid)) {
      return;
    }
    e.setCancelled(true);

    String inputContent = e.getMessage();
    GuiSettingStatus guiSettingStatus = playerCurrentGuiStatusMap.get(uuid);
    Material currentMaterial = guiSettingStatus.getCurrentMaterial();

    if (inputContent.equals("cancel")) {
      player.sendMessage("登録がキャンセルされました。アイテムの詳細画面に戻ります。");
      itemDetailsGuiManager.reopenItemDetailsGui(player, currentMaterial, guiSettingStatus);
      return;
    }

    EditItem editItem = awaitingInputMap.remove(uuid);
    itemRegistrator.editItemBasedOnClick(
        editItem, player, inputContent, currentMaterial, guiSettingStatus);
  }
}
