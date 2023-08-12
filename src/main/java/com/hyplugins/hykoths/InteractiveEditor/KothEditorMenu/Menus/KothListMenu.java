package com.hyplugins.hykoths.InteractiveEditor.KothEditorMenu.Menus;

import com.hyplugins.hykoths.Game.CuboidRegion;
import com.hyplugins.hykoths.Game.KothObject;
import com.hyplugins.hykoths.HyKoths;
import com.hyplugins.hykoths.InteractiveEditor.KothEditorMenu.MenuObject;
import com.hyplugins.hykoths.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KothListMenu extends MenuObject {
  private final HyKoths plugin;
  
  private final List<UUID> playersMenu;
  
  public KothListMenu(HyKoths plugin, String title) {
    super(title);
    this.plugin = plugin;
    this.playersMenu = new ArrayList<>();
  }
  
  public void openMenu(Player player) {
    Inventory listMenu = createCopyInventory();
    for (KothObject koth : this.plugin.getKothConfig().getKoths()) {
      listMenu.addItem(new ItemStack[] { getKothInfoItem(koth) });
    } 
    player.openInventory(listMenu);
    this.playersMenu.add(player.getUniqueId());
  }
  
  public ItemStack getKothInfoItem(KothObject koth) {
    ItemStack item = new ItemStack(Material.PAPER);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(koth.getName());
    List<String> lore = new ArrayList<>();
    lore.add(Utils.translateMessage(" &8- &fKoth type&8: &a" + koth.getType().name()));
    lore.add(Utils.translateMessage(" &8- &fKoth Region&8:"));
    CuboidRegion region = koth.getRegion();
    Location location1 = region.getMinLocation();
    lore.add(Utils.translateMessage("   &fLocation 1&8: &e" + location1.getBlockX() + "&f,&e" + location1.getBlockY() + "&f,&e" + location1.getBlockZ()));
    Location location2 = region.getMaxLocation();
    lore.add(Utils.translateMessage("   &fLocation 2&8: &e" + location2.getBlockX() + "&f,&e" + location2.getBlockY() + "&f,&e" + location2.getBlockZ()));
    lore.add(Utils.translateMessage(" &8- &fCapture Time&8: &c" + koth.getCaptureTime()));
    lore.add(Utils.translateMessage(" &8- &fKoth Duration&8: &a" + koth.getDuration()));
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }
  
  public List<UUID> getPlayersMenu() {
    return this.playersMenu;
  }
}
