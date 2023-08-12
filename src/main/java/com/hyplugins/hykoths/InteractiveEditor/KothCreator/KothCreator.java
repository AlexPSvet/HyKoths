package com.hyplugins.hykoths.InteractiveEditor.KothCreator;

import com.hyplugins.hykoths.Config.KothType;
import com.hyplugins.hykoths.Game.CuboidRegion;
import com.hyplugins.hykoths.HyKoths;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class KothCreator extends BukkitRunnable {
  private final HyKoths plugin;
  
  private int editingStep;
  
  private final UUID playerEditorUUID;
  
  private String name;
  
  private KothType type;
  
  private CuboidRegion region;
  
  private Location location1;
  
  private Location location2;
  
  private List<ItemStack> rewards;
  
  private List<Integer> schedulersInteger;
  
  private List<String> schedulersString;
  
  private List<String> commands;
  
  private int captureTime;
  
  private int duration;
  
  public KothCreator(HyKoths plugin, UUID playerEditorUUID) {
    this.plugin = plugin;
    this.playerEditorUUID = playerEditorUUID;
    runTaskTimer((Plugin)plugin, 0L, 20L);
  }
  
  public void run() {
    Player player = Bukkit.getPlayer(this.playerEditorUUID);
    if (player == null) {
      cancel();
      return;
    } 
    String[] titles = (String[])this.plugin.getKothConfig().getEditingTitles().getOrDefault(Integer.valueOf(this.editingStep), new String[0]);
    String title = titles[0];
    String subtitle = titles[1];
    player.sendTitle(title, subtitle);
    if (this.editingStep == 7)
      cancel(); 
  }
  
  public int getEditingStep() {
    return this.editingStep;
  }
  
  public void setEditingStep(int editingStep) {
    this.editingStep = editingStep;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setType(KothType type) {
    this.type = type;
  }
  
  public KothType getType() {
    return this.type;
  }
  
  public void setRegion(CuboidRegion region) {
    this.region = region;
  }
  
  public CuboidRegion getRegion() {
    return this.region;
  }
  
  public Location getLocation1() {
    return this.location1;
  }
  
  public void setLocation1(Location location1) {
    this.location1 = location1;
  }
  
  public Location getLocation2() {
    return this.location2;
  }
  
  public void setLocation2(Location location2) {
    this.location2 = location2;
  }
  
  public void setRewards(List<ItemStack> rewards) {
    this.rewards = rewards;
  }
  
  public List<ItemStack> getRewards() {
    return this.rewards;
  }
  
  public void setSchedulersInteger(List<Integer> schedulersInteger) {
    this.schedulersInteger = schedulersInteger;
  }
  
  public List<Integer> getSchedulersInteger() {
    return this.schedulersInteger;
  }
  
  public List<String> getSchedulersString() {
    return this.schedulersString;
  }
  
  public void setSchedulersString(List<String> schedulersString) {
    this.schedulersString = schedulersString;
  }
  
  public List<String> getCommands() {
    return this.commands;
  }
  
  public void setCommands(List<String> commands) {
    this.commands = commands;
  }
  
  public void setCaptureTime(int captureTime) {
    this.captureTime = captureTime;
  }
  
  public int getCaptureTime() {
    return this.captureTime;
  }
  
  public void setDuration(int duration) {
    this.duration = duration;
  }
  
  public int getDuration() {
    return this.duration;
  }
}
