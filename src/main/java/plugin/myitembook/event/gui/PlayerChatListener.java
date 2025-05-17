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
   * プレイヤーがチャット入力した際に発火するイベント。<br> チャット入力をしたプレイヤーのUUIDが入力待ちマップに含まれている場合は、入力した内容とアイテム図鑑の設定状況を取得する。<br>
   * 「cancel」と入力された場合は処理を中断し、アイテムの詳細画面に戻り、そうでない場合はアイテムの詳細情報を編集するメソッドを呼び出す。
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
    itemRegistrator.editItemDetailsBasedOnClick(
        editItem, player, inputContent, currentMaterial, guiSettingStatus);
  }
}
