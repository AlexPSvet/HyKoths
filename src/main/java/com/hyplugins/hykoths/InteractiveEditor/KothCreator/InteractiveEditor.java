package com.hyplugins.hykoths.InteractiveEditor.KothCreator;

import com.hyplugins.hykoths.Config.KothType;
import com.hyplugins.hykoths.Game.CuboidRegion;
import com.hyplugins.hykoths.Game.KothObject;
import com.hyplugins.hykoths.HyKoths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InteractiveEditor {
  private final HyKoths plugin;
  
  private final HashMap<UUID, KothCreator> editing;
  
  public InteractiveEditor(HyKoths plugin) {
    this.plugin = plugin;
    this.editing = new HashMap<>();
  }
  
  public void newKothEditor(Player player) {
    KothCreator kothCreator = new KothCreator(this.plugin, player.getUniqueId());
    kothCreator.setEditingStep(0);
    this.editing.put(player.getUniqueId(), kothCreator);
    player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.creator-enabled"));
  }
  
  public void addNewValue(Player player, String value) {
    KothCreator kothData = this.editing.get(player.getUniqueId());
    switch (kothData.getEditingStep()) {
      case 0:
        handleKothName(kothData, value, player);
        break;
      case 1:
        handleKothType(kothData, player, value);
        break;
      case 2:
        handleLocationsStep(kothData, player);
        break;
      case 3:
        handleRewardsStep(kothData, player, value);
        break;
      case 4:
        handleSchedulesStep(value, kothData, player);
        break;
      case 5:
        handleCaptureTimeStep(kothData, value, player);
        break;
      case 6:
        handleKothDurationStep(kothData, value, player);
        break;
    } 
  }
  
  public void handleKothName(KothCreator kothData, String value, Player player) {
    if (this.plugin.getKothConfig().getKoths().stream().anyMatch(k -> k.getName().equals(value))) {
      player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.koth-already-exists").replace("%name%", value));
    } else {
      kothData.setName(value);
      kothData.setEditingStep(1);
      player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.koth-name-set"));
    } 
  }
  
  public void handleKothType(KothCreator kothData, Player player, String value) {
    try {
      kothData.setType(KothType.valueOf(value.toUpperCase()));
      player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.koth-type-set"));
      kothData.setEditingStep(2);
    } catch (IllegalArgumentException e) {
      player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.invalid-koth-type").replace("%type%", value));
    } 
  }
  
  public void handleLocationsStep(KothCreator kothData, Player player) {
    if (kothData.getLocation1() == null || kothData.getLocation2() == null) {
      player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.locations-not-set"));
    } else {
      kothData.setRegion(new CuboidRegion(kothData.getLocation1(), kothData.getLocation2()));
      player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.locations-set"));
      kothData.setEditingStep(3);
    } 
  }
  
  public void handleRewardsStep(KothCreator kothData, Player player, String value) {
    List<String> commands;
    if (!value.equalsIgnoreCase("done")) {
      commands = new ArrayList<>(Arrays.asList(value.split(":")));
    } else {
      commands = new ArrayList<>();
    } 
    kothData.setCommands(commands);
    List<ItemStack> rewards = (List<ItemStack>)Arrays.<ItemStack>stream(player.getInventory().getContents()).filter(Objects::nonNull).collect(Collectors.toList());
    kothData.setRewards(rewards);
    player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.rewards-set"));
    kothData.setEditingStep((kothData.getType() == KothType.TIMER) ? 4 : 5);
  }
  
  public void handleSchedulesStep(String value, KothCreator kothData, Player player) {
    List<String> schedulersString = Arrays.asList(value.split(";"));
    List<Integer> schedulers = new ArrayList<>();
    try {
      for (String s : schedulersString) {
        int hour = Integer.parseInt(s.split(":")[0]);
        int minute = Integer.parseInt(s.split(":")[1]);
        if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59)
          schedulers.add(Integer.valueOf(hour * 60 + minute)); 
      } 
      kothData.setSchedulersInteger(schedulers);
      kothData.setSchedulersString(schedulersString);
      player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.schedulers-set"));
      kothData.setEditingStep(5);
    } catch (NumberFormatException e) {
      player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.invalid-schedulers"));
    } 
  }
  
  public void handleCaptureTimeStep(KothCreator kothData, String value, Player player) {
    try {
      int captureTime = Integer.parseInt(value);
      kothData.setCaptureTime(captureTime);
      player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.capture-time-set"));
      kothData.setEditingStep(6);
    } catch (NumberFormatException e) {
      player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.invalid-capture-time"));
    } 
  }
  
  public void handleKothDurationStep(KothCreator kothData, String value, Player player) {
    try {
      int duration = Integer.parseInt(value);
      if (duration > kothData.getCaptureTime()) {
        kothData.setDuration(duration);
        player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.koth-duration-set"));
        kothData.setEditingStep(7);
        registerNewKothFinalStep(kothData, player);
      } else {
        player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.koth-duration-smaller"));
      } 
    } catch (NumberFormatException e) {
      player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.invalid-koth-duration"));
    } 
  }
  
  public void registerNewKothFinalStep(KothCreator kothData, Player player) {
    this.plugin.getKothConfig().getKoths().add(new KothObject(this.plugin, kothData
          
          .getName(), kothData
          .getType(), kothData
          .getRegion(), kothData
          .getRewards(), kothData
          .getSchedulersInteger(), kothData
          .getCommands(), kothData
          .getCaptureTime(), kothData
          .getDuration()));
    this.plugin.getKothConfig().addKothToConfig(kothData);
    player.sendMessage(this.plugin.getKothConfig().getMessage("koth-creator.koth-created").replace("%koth%", kothData.getName()));
    this.editing.remove(player.getUniqueId());
  }
  
  public HashMap<UUID, KothCreator> getEditing() {
    return this.editing;
  }
}
