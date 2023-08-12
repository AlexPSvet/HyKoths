package com.hyplugins.hykoths;

import com.hyplugins.hykoths.Api.HyKothsAPI;
import com.hyplugins.hykoths.Config.KothConfig;
import com.hyplugins.hykoths.Game.KothScheduler;
import com.hyplugins.hykoths.Game.KothActiveListener;
import com.hyplugins.hykoths.InteractiveEditor.KothCommand;
import com.hyplugins.hykoths.InteractiveEditor.KothCommandTabComplete;
import com.hyplugins.hykoths.InteractiveEditor.KothCreator.InteractiveEditor;
import com.hyplugins.hykoths.InteractiveEditor.KothEditorMenu.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class HyKoths extends JavaPlugin {
  private static HyKothsAPI api;
  
  private KothConfig config;
  
  private KothScheduler kothScheduler;
  
  private InteractiveEditor kothEditor;
  
  private MenuManager menus;
  
  private boolean isBossBarEnabled;
  
  private boolean isDisabled;
  
  public void onEnable() {
    this.isDisabled = false;
    saveDefaultConfig();
    this.config = new KothConfig(this);
    this.isBossBarEnabled = getConfig().getBoolean("koth.boss-bar-enabled");
    this.kothScheduler = new KothScheduler(this, getConfig().getInt("random-koth-interval"));
    new KothActiveListener(this);
    getCommand("koth").setExecutor((CommandExecutor)new KothCommand(this));
    getCommand("koth").setTabCompleter((TabCompleter)new KothCommandTabComplete(this));
    this.kothEditor = new InteractiveEditor(this);
    Bukkit.getPluginManager().registerEvents(new Events(this), (Plugin)this);
    this.menus = new MenuManager(this);
    api = new HyKothsAPI(this);
    new PlaceHolderAPI(this).register();
    getLogger().info("HyKoths plugin has been enabled!");
  }
  
  public void onDisable() {
    if (!this.isDisabled) {
      this.kothScheduler.cancel();
      getLogger().info("HyKoths plugin has been disabled!");
    } 
  }
  
  private boolean isVersionAbove18() {
    String versionString = Bukkit.getServer().getBukkitVersion();
    System.out.println(versionString);
    String[] versionComponents = versionString.split("-")[0].split("\\.");
    int majorVersion = Integer.parseInt(versionComponents[0]);
    int minorVersion = Integer.parseInt(versionComponents[1]);
    return (majorVersion == 1 && minorVersion > 8);
  }
  
  public KothConfig getKothConfig() {
    return this.config;
  }
  
  public InteractiveEditor getKothEditor() {
    return this.kothEditor;
  }
  
  public void setDisabled(boolean disabled) {
    this.isDisabled = disabled;
  }
  
  public MenuManager getMenus() {
    return this.menus;
  }
  
  public boolean isBossBarEnabled() {
    return this.isBossBarEnabled;
  }
}
