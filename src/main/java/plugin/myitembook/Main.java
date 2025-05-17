package plugin.myitembook;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.myitembook.command.OpenItemBookCommand;
import plugin.myitembook.data.PlayerData;
import plugin.myitembook.data.PlayerDataLoader;
import plugin.myitembook.data.PlayerDataSaver;
import plugin.myitembook.event.data.PlayerJoinListener;
import plugin.myitembook.event.data.PlayerQuitListener;
import plugin.myitembook.event.getitem.CraftItemListener;
import plugin.myitembook.event.getitem.PickupItemListener;
import plugin.myitembook.event.gui.InventoryClickListener;
import plugin.myitembook.event.gui.PlayerChatListener;
import plugin.myitembook.gui.click.EditItem;
import plugin.myitembook.gui.click.ItemBookClickHandler;
import plugin.myitembook.gui.click.ItemDetailsClickHandler;
import plugin.myitembook.gui.management.GuiSettingStatus;
import plugin.myitembook.gui.management.ItemBookGuiManager;
import plugin.myitembook.gui.management.ItemDetailsGuiManager;
import plugin.myitembook.item.ItemRegistrator;
import plugin.myitembook.item.RegistrationChecker;

public final class Main extends JavaPlugin {

  @Override
  public void onEnable() {
    // データを扱うクラスをインスタンス化し、関連マップを取得
    PlayerDataLoader playerDataLoader = new PlayerDataLoader(this);
    Map<UUID, PlayerData> playerDataMap = playerDataLoader.getPlayerDataMap();

    PlayerDataSaver playerDataSaver = new PlayerDataSaver(playerDataMap);

    // GUI管理を担うクラスをインスタンス化し、関連マップを取得
    ItemBookGuiManager itemBookGuiManager = new ItemBookGuiManager(this, playerDataMap);
    Map<UUID, GuiSettingStatus> playerCurrentGuiStatusMap = itemBookGuiManager.getPlayerCurrentGuiStatusMap();

    ItemDetailsGuiManager itemDetailsGuiManager = new ItemDetailsGuiManager(this, playerDataMap);

    // GUIのクリック処理を扱うクラスをインスタンス化し、関連マップを取得
    ItemBookClickHandler itemBookClickHandler =
        new ItemBookClickHandler(itemBookGuiManager, itemDetailsGuiManager, playerDataSaver, playerCurrentGuiStatusMap);

    ItemDetailsClickHandler itemDetailsClickHandler =
        new ItemDetailsClickHandler(itemBookGuiManager, itemDetailsGuiManager, playerDataSaver);
    Map<UUID, EditItem> awaitingRegistrationMap = itemDetailsClickHandler.getAwaitingInputMap();

    // アイテム登録を担うクラスをインスタンス化
    ItemRegistrator itemRegistrator = new ItemRegistrator(itemDetailsGuiManager, playerDataMap);
    RegistrationChecker registrationChecker = new RegistrationChecker(this, itemRegistrator, playerDataMap);

    // イベントリスナーを登録
    Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(playerDataLoader), this);
    Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(playerDataSaver, playerDataMap), this);
    Bukkit.getPluginManager().registerEvents(new PickupItemListener(registrationChecker), this);
    Bukkit.getPluginManager().registerEvents(new CraftItemListener(registrationChecker), this);
    Bukkit.getPluginManager().registerEvents(
        new InventoryClickListener(itemBookClickHandler, itemDetailsClickHandler, playerCurrentGuiStatusMap), this);
    Bukkit.getPluginManager().registerEvents(
        new PlayerChatListener(
            itemDetailsGuiManager, itemRegistrator, playerCurrentGuiStatusMap, awaitingRegistrationMap), this);

    // コマンドを登録
    OpenItemBookCommand openItemBookCommand = new OpenItemBookCommand(itemBookGuiManager);
    Objects.requireNonNull(getCommand("itembook")).setExecutor(openItemBookCommand);
  }
}