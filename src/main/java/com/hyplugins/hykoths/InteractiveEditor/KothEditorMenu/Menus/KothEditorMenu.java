package com.hyplugins.hykoths.InteractiveEditor.KothEditorMenu.Menus;

import com.hyplugins.hykoths.Config.XMaterial;
import com.hyplugins.hykoths.Game.KothObject;
import com.hyplugins.hykoths.InteractiveEditor.KothEditorMenu.MenuObject;
import com.hyplugins.hykoths.Utils;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KothEditorMenu extends MenuObject {
  private final HashMap<UUID, String> kothData;
  
  public KothEditorMenu(String title) {
    super(title);
    this.kothData = new HashMap<>();
  }
  
  public void openMenu(Player player, KothObject koth) {
    Inventory menu = createCopyInventory();
    menu.setItem(28, createButton(koth.getName(), " &8- &a&lSTART KOTH &c%koth% &8- ", XMaterial.GREEN_DYE.parseItem()));
    menu.setItem(22, createButton(koth.getName(), " &8- &c&lSTOP KOTH &c%koth% &8- ", XMaterial.RED_DYE.parseItem()));
    menu.setItem(34, createButton(koth.getName(), " &8- &4&lDELETE KOTH &c%koth% &8- ", new ItemStack(Material.REDSTONE)));
    player.openInventory(menu);
    this.kothData.put(player.getUniqueId(), koth.getName());
  }
  
  public ItemStack createButton(String kothName, String message, ItemStack item) {
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(Utils.translateMessage(message.replace("%koth%", kothName)));
    item.setItemMeta(meta);
    return item;
  }
  
  public HashMap<UUID, String> getPlayersKothData() {
    return this.kothData;
  }
}
