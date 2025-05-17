package plugin.myitembook.gui.management;

import lombok.Getter;

/**
 * アイテム図鑑GUIの並び順を扱うenum。
 */
public enum OrderType {

  ASC_ORDER("昇順"),
  REGISTRATION_ORDER("登録順");

  @Getter
  private final String orderType;

  OrderType(String orderType) {
    this.orderType = orderType;
  }
}