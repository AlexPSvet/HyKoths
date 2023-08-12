package com.hyplugins.hykoths.Game;

import com.hyplugins.hykoths.Api.KothStartEvent;
import com.hyplugins.hykoths.Config.EmbedType;
import com.hyplugins.hykoths.Config.KothType;
import com.hyplugins.hykoths.HyKoths;
import com.hyplugins.hykoths.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class KothObject {
  private final HyKoths plugin;
  
  private String name;
  
  private final KothType type;
  
  private CuboidRegion region;
  
  private final BossBar bossbar;
  
  private List<ItemStack> rewards;
  
  private List<Integer> schedulers;
  
  private List<String> commands;
  
  private int captureTime;
  
  private int duration;
  
  private UUID capturingPlayer;
  
  private String capturingPlayerName;
  
  private int remainingTime;
  
  private int capturingPlayerTime;
  
  private boolean active;
  
  private boolean isCapturing;
  
  public KothObject(HyKoths plugin, String name, KothType type, CuboidRegion region, List<ItemStack> rewards, List<Integer> schedulers, List<String> commands, int captureTime, int duration) {
    this.plugin = plugin;
    this.name = name;
    this.type = type;
    this.region = region;
    this.rewards = rewards;
    this.schedulers = schedulers;
    this.commands = commands;
    this.captureTime = captureTime;
    this.duration = duration;
    this.remainingTime = duration;
    this.capturingPlayerTime = 0;
    this.active = false;
    this.isCapturing = false;
    this.bossbar = Bukkit.createBossBar(" ", BarColor.PURPLE, BarStyle.SOLID);
  }
  
  public void start() {
    KothStartEvent event = new KothStartEvent(this);
    Bukkit.getPluginManager().callEvent(event);
    if (!event.isCancelled()) {
      this.active = true;
      this.bossbar.setTitle(Utils.translateMessage(this.plugin.getKothConfig().getMessage("boss-bar.no-one-capturing").replace("%koth%", this.name)));
      Bukkit.broadcastMessage(Utils.replacePlaceHolders(this, this.plugin.getKothConfig().getMessage("koth.koth-started")));
    }
  }
  
  public void stop() {
    this.capturingPlayer = null;
    this.capturingPlayerName = null;
    this.remainingTime = this.duration;
    this.capturingPlayerTime = 0;
    this.isCapturing = false;
    this.active = false;
    if (this.plugin.isBossBarEnabled()) {
      this.bossbar.setTitle(" ");
      bossbar.setProgress(0);
      this.bossbar.removeAll();
    }
    plugin.getKothConfig().getEmbeds().get(EmbedType.KOTH_STOP).sendMessage(this);
  }
  
  public void chooseKothPlayerCamper() {
    List<Player> players = this.region.getPlayersInside();
    if (!players.isEmpty()) {
      Player player = players.get(0);
      Bukkit.broadcastMessage(Utils.replacePlaceHolders(this, this.plugin.getKothConfig().getMessage("koth.koth-camping")).replace("%player%", player.getName()));
      this.capturingPlayer = player.getUniqueId();
      this.capturingPlayerName = player.getName();
      this.isCapturing = true;
      if (this.plugin.isBossBarEnabled())
        this.bossbar.setTitle(Utils.translateMessage(this.plugin.getKothConfig().getMessage("boss-bar.capturing").replace("%koth%", this.name).replace("%name%", player.getName()))); 
    } 
  }
  
  public void updateCaptureTime() {
    Player player = Bukkit.getPlayer(this.capturingPlayer);
    this.capturingPlayerTime++;

    if (this.capturingPlayerTime >= this.captureTime) {
      Bukkit.broadcastMessage(Utils.replacePlaceHolders(this, this.plugin.getKothConfig().getMessage("koth.koth-captured")).replace("%player%", player.getName()));
      for (ItemStack item : this.rewards) {
        List<ItemStack> items = new ArrayList<>(player.getInventory().addItem(new ItemStack[] { item }).values());
        for (ItemStack itemStack : items)
          player.getLocation().getWorld().dropItemNaturally(player.getLocation(), itemStack); 
      } 
      for (String command : this.commands)
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
      celebrateVictory(player);
      stop();
    }
    bossbar.setProgress((float) getCapturingPlayerTime() / getCaptureTime());
  }
  
  public void stopCapture() {
    Bukkit.broadcastMessage(Utils.replacePlaceHolders(this, this.plugin.getKothConfig().getMessage("koth.koth-lost")).replace("%player%", this.capturingPlayerName));
    if (this.plugin.isBossBarEnabled()) {
      this.bossbar.setProgress(0.0D);
      this.bossbar.setTitle(Utils.translateMessage(this.plugin.getKothConfig().getMessage("boss-bar.no-one-capturing").replace("%koth%", this.name)));
    } 
    this.capturingPlayerTime = 0;
    this.isCapturing = false;
    this.capturingPlayer = null;
    this.capturingPlayerName = null;
  }
  
  public void celebrateVictory(final Player winner) {
    (new BukkitRunnable() {
        int count = 0;
        
        public void run() {
          this.count++;
          if (this.count > 3) {
            cancel();
            return;
          } 
          winner.getWorld().playEffect(winner.getLocation(), Effect.BLAZE_SHOOT, 0);
          winner.getWorld().playEffect(winner.getLocation().add(-1.0D, 1.0D, 1.0D), Effect.BLAZE_SHOOT, 0);
          winner.getWorld().playEffect(winner.getLocation().add(1.0D, -1.0D, 1.0D), Effect.BLAZE_SHOOT, 0);
          winner.getWorld().playEffect(winner.getLocation().add(1.0D, 1.0D, -1.0D), Effect.BLAZE_SHOOT, 0);
        }
      }).runTaskTimer(this.plugin, 0L, 20L);
    winner.sendTitle(this.plugin.getKothConfig().getMessage("koth.victory-title"), this.plugin.getKothConfig().getMessage("koth.victory-sub-title"));
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public KothType getType() {
    return this.type;
  }
  
  public CuboidRegion getRegion() {
    return this.region;
  }
  
  public void setRegion(CuboidRegion region) {
    this.region = region;
  }
  
  public List<ItemStack> getRewards() {
    return this.rewards;
  }
  
  public void setRewards(List<ItemStack> rewards) {
    this.rewards = rewards;
  }
  
  public List<Integer> getSchedulers() {
    return this.schedulers;
  }
  
  public void setSchedulers(List<Integer> schedulers) {
    this.schedulers = schedulers;
  }
  
  public List<String> getCommands() {
    return this.commands;
  }
  
  public void setCommands(List<String> commands) {
    this.commands = commands;
  }
  
  public int getCaptureTime() {
    return this.captureTime;
  }
  
  public void setCaptureTime(int captureTime) {
    this.captureTime = captureTime;
  }
  
  public int getCapturingPlayerTime() {
    return this.capturingPlayerTime;
  }
  
  public int getDuration() {
    return this.duration;
  }
  
  public void setDuration(int duration) {
    this.duration = duration;
  }
  
  public boolean isActive() {
    return this.active;
  }
  
  public boolean isCapturing() {
    return this.isCapturing;
  }
  
  public UUID getCapturingPlayer() {
    return this.capturingPlayer;
  }
  
  public int getRemainingTime() {
    return this.remainingTime;
  }
  
  public void setRemainingTime() {
    this.remainingTime--;
  }
  
  public BossBar getBossbar() {
    return this.bossbar;
  }
}
