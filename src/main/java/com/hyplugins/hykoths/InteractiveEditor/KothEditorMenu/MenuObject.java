package com.hyplugins.hykoths.InteractiveEditor.KothEditorMenu;

import com.hyplugins.hykoths.Config.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuObject {
  private final String title;
  
  private final Inventory inventory;
  
  public MenuObject(String title) {
    this.inventory = loadMenu(title);
    this.title = title;
  }
  
  public Inventory loadMenu(String title) {
    Inventory inventory = Bukkit.createInventory(null, 54, title);
    ItemStack frame = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
    for (int i : new int[] { 
        0, 1, 2, 3, 4, 5, 6, 7, 8, 45, 
        46, 47, 48, 49, 50, 51, 52, 53 })
      inventory.setItem(i, frame); 
    return inventory;
  }
  
  public Inventory createCopyInventory() {
    Inventory copy = Bukkit.createInventory(null, this.inventory.getSize(), this.title);
    copy.setContents(this.inventory.getContents());
    return copy;
  }
  
  public Inventory getInventoryMenu() {
    return this.inventory;
  }
  
  public String getTitle() {
    return this.title;
  }
}
