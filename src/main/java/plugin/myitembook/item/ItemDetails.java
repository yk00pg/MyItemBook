package plugin.myitembook.item;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * アイテムの詳細情報を扱うオブジェクト。
 */
@Getter
public class ItemDetails {

  private String displayName;
  private String lore;
  private String category;
  private int difficulty;
  private String howToGet;
  private String howToUse;
  private String memo;
  private final int registrationNumber;

  public ItemDetails(
      String displayName, String lore, String category,
      int difficulty, String howToGet, String howToUse, String memo, int registrationNumber) {

    this.displayName = displayName;
    this.lore = lore;
    this.category = category;
    this.difficulty = difficulty;
    this.howToGet = howToGet;
    this.howToUse = howToUse;
    this.memo = memo;
    this.registrationNumber = registrationNumber;
  }

  /**
   * 入力内容に応じて表示名を更新または削除する。
   *
   * @param inputContent 入力内容
   */
  public void updateDisplayName(String inputContent) {
    if (inputContent.equals("delete")) {
      inputContent = " ";
    }
    this.displayName = inputContent;
  }

  /**
   * 入力内容に応じて説明文を更新または削除する。
   *
   * @param inputContent 入力内容
   */
  public void updateLore(String inputContent) {
    if (inputContent.equals("delete")) {
      inputContent = " ";
    }
    this.lore = inputContent;
  }

  /**
   * 入力内容に応じてカテゴリを更新または削除する。
   *
   * @param inputContent 入力内容
   */
  public void updateCategory(String inputContent) {
    if (inputContent.equals("delete")) {
      inputContent = " ";
    }
    this.category = inputContent;
  }

  /**
   * 入力内容に応じて入手難易度を更新または削除する。<br>
   * 数値に変換できない場合は例外処理をしてメッセージを表示する。
   *
   * @param inputContent 入力内容
   * @param player       チャット入力をしたプレイヤー
   * @return 登録の成否
   */
  public boolean updateDifficulty(String inputContent, Player player) {

    if (inputContent.equals("delete")) {
      inputContent = String.valueOf(0);
    }
    try {
      this.difficulty = Integer.parseInt(inputContent);
      return true;
    } catch (NumberFormatException ex) {
      player.sendMessage(ChatColor.RED + "入手難易度を登録できませんでした。");
      player.sendMessage(ChatColor.RED + "アイコンを再クリックして"
          + ChatColor.YELLOW + "半角数字"
          + ChatColor.RED + "で入力し直してください。");
      return false;
    }
  }

  /**
   * 入力内容に応じて入手方法を更新または削除する。
   *
   * @param inputContent 入力内容
   */
  public void updateHowToGet(String inputContent) {
    if (inputContent.equals("delete")) {
      inputContent = " ";
    }
    this.howToGet = inputContent;
  }

  /**
   * 入力内容に応じて使い方を更新または削除する。
   *
   * @param inputContent 入力内容
   */
  public void updateHowToUse(String inputContent) {
    if (inputContent.equals("delete")) {
      inputContent = " ";
    }
    this.howToUse = inputContent;
  }

  /**
   * 入力内容に応じてメモを更新または削除する。
   *
   * @param inputContent 入力内容
   */
  public void updateMemo(String inputContent) {
    if (inputContent.equals("delete")) {
      inputContent = " ";
    }
    this.memo = inputContent;
  }
}