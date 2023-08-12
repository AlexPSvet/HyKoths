package com.hyplugins.hykoths;

import com.hyplugins.hykoths.Game.KothObject;
import com.hyplugins.hykoths.InteractiveEditor.KothCreator.KothCreator;
import com.hyplugins.hykoths.InteractiveEditor.KothEditorMenu.MenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {
  private final HyKoths plugin;
  
  public Events(HyKoths plugin) {
    this.plugin = plugin;
  }
  
  @EventHandler
  public void onChat(AsyncPlayerChatEvent e) {
    Player player = e.getPlayer();
    if (this.plugin.getKothEditor().getEditing().containsKey(player.getUniqueId())) {
      KothCreator kothCreator = (KothCreator)this.plugin.getKothEditor().getEditing().get(player.getUniqueId());
      switch (kothCreator.getEditingStep()) {
        case 0:
        case 1:
        case 3:
        case 4:
        case 5:
        case 6:
          this.plugin.getKothEditor().addNewValue(player, e.getMessage());
          break;
        case 2:
          if (e.getMessage().equalsIgnoreCase("done"))
            this.plugin.getKothEditor().addNewValue(player, null); 
          break;
      } 
      e.setCancelled(true);
    } 
  }
  
  @EventHandler
  public void onBlockInteract(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    if (this.plugin.getKothEditor().getEditing().containsKey(player.getUniqueId())) {
      KothCreator kothCreator = (KothCreator)this.plugin.getKothEditor().getEditing().get(player.getUniqueId());
      if (kothCreator.getEditingStep() == 2 && (!e.getAction().equals(Action.LEFT_CLICK_AIR) || !e.getAction().equals(Action.RIGHT_CLICK_AIR))) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
          kothCreator.setLocation1(e.getClickedBlock().getLocation());
          player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.location2-set"));
        } else if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
          kothCreator.setLocation2(e.getClickedBlock().getLocation());
          player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.location1-set"));
        } 
        e.setCancelled(true);
      } 
    } 
  }
  
  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    Player player = e.getPlayer();
    if (this.plugin.getKothEditor().getEditing().containsKey(player.getUniqueId())) {
      KothCreator kothCreator = (KothCreator)this.plugin.getKothEditor().getEditing().get(player.getUniqueId());
      kothCreator.cancel();
      this.plugin.getKothEditor().getEditing().remove(player.getUniqueId());
      this.plugin.getLogger().warning("Player " + player.getName() + " leaved the server while creating a koth.");
    } 
    for (KothObject koth : this.plugin.getKothConfig().getKoths()) {
      if (koth.getBossbar().getPlayers().contains(player)) {
        koth.getBossbar().removePlayer(player);
        break;
      } 
    } 
  }
  
  @EventHandler
  public void onInventoryInteract(InventoryClickEvent e) {
    MenuManager menus = this.plugin.getMenus();
    Player player = (Player)e.getWhoClicked();
    if (e.getCurrentItem() != null) {
      String title = e.getView().getTitle();
      if (title.equals(menus.getKothListMenu().getTitle()) && menus.getKothListMenu().getPlayersMenu().contains(player.getUniqueId())) {
        e.setCancelled(true);
        KothObject koth = this.plugin.getKothConfig().getKothByName(e.getCurrentItem().getItemMeta().getDisplayName());
        if (koth != null)
          this.plugin.getMenus().getKothEditorMenu().openMenu(player, koth); 
      } else if (title.equals(menus.getKothEditorMenu().getTitle()) && menus.getKothEditorMenu().getPlayersKothData().containsKey(player.getUniqueId())) {
        switch (e.getRawSlot()) {
          case 22:
            player.performCommand("koth stop " + (String)menus.getKothEditorMenu().getPlayersKothData().get(player.getUniqueId()));
            break;
          case 28:
            player.performCommand("koth start " + (String)menus.getKothEditorMenu().getPlayersKothData().get(player.getUniqueId()));
            break;
          case 34:
            player.performCommand("koth delete " + (String)menus.getKothEditorMenu().getPlayersKothData().get(player.getUniqueId()));
            break;
        } 
        e.setCancelled(true);
      } 
    } 
  }
  
  @EventHandler
  public void onInventoryClosed(InventoryCloseEvent e) {
    this.plugin.getMenus().getKothListMenu().getPlayersMenu().remove(e.getPlayer().getUniqueId());
    this.plugin.getMenus().getKothEditorMenu().getPlayersKothData().remove(e.getPlayer().getUniqueId());
  }
}
