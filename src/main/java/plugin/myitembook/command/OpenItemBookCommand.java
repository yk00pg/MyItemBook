package plugin.myitembook.command;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugin.myitembook.gui.management.ItemBookGuiManager;
import plugin.myitembook.gui.management.OrderType;

/**
 * アイテム図鑑GUIを開くコマンド。
 */
@Getter
public class OpenItemBookCommand implements CommandExecutor {

  private final ItemBookGuiManager itemBookGuiManager;

  public OpenItemBookCommand(ItemBookGuiManager itemBookGuiManager) {
    this.itemBookGuiManager = itemBookGuiManager;
  }

  @Override
  public boolean onCommand(
      @NotNull CommandSender commandSender, @NotNull Command command,
      @NotNull String s, @NotNull String[] strings) {

    if (commandSender instanceof Player player) {
      itemBookGuiManager.openItemBookGui(player, 0, OrderType.ASC_ORDER);
    }
    return false;
  }
}
