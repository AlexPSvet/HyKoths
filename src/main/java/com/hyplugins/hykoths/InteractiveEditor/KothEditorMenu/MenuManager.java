package com.hyplugins.hykoths.InteractiveEditor.KothEditorMenu;

import com.hyplugins.hykoths.HyKoths;
import com.hyplugins.hykoths.InteractiveEditor.KothEditorMenu.Menus.KothEditorMenu;
import com.hyplugins.hykoths.InteractiveEditor.KothEditorMenu.Menus.KothListMenu;
import com.hyplugins.hykoths.Utils;

public class MenuManager {
  private final KothListMenu kothListMenu;
  
  private KothEditorMenu kothEditorMenu;
  
  public MenuManager(HyKoths plugin) {
    this.kothListMenu = new KothListMenu(plugin, Utils.translateMessage("&cKoth List Menu"));
    this.kothEditorMenu = new KothEditorMenu(Utils.translateMessage("&cKoth Editor Menu"));
  }
  
  public KothListMenu getKothListMenu() {
    return this.kothListMenu;
  }
  
  public KothEditorMenu getKothEditorMenu() {
    return this.kothEditorMenu;
  }
}
