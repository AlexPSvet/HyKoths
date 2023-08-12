package com.hyplugins.hykoths.Game;

import com.hyplugins.hykoths.HyKoths;
import com.hyplugins.hykoths.Utils;

import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class KothActiveListener extends BukkitRunnable {
  private final HyKoths plugin;
  
  public KothActiveListener(HyKoths plugin) {
    this.plugin = plugin;
    runTaskTimer(plugin, 0L, 20L);
  }
  
  public void run() {
    for (KothObject koth : this.plugin.getKothConfig().getKoths()) {
      if (!koth.isActive()) continue;
      if (koth.getRemainingTime() == 0) {
        Bukkit.broadcastMessage(this.plugin.getKothConfig().getMessage("koth.koth-stopped").replace("%koth%", koth.getName()));
        koth.stop();
        continue;
      } 
      koth.setRemainingTime();

      List<Player> playersInRadius = Utils.getPlayersWithinRadius(koth.getRegion().getCenterLocation(), 50.0D);
      BossBar kothBossBar = koth.getBossbar();
      List<Player> bossBarPlayers = kothBossBar.getPlayers();
      for (Player bossBarPlayer : bossBarPlayers) {
        if (!playersInRadius.contains(bossBarPlayer)) kothBossBar.removePlayer(bossBarPlayer);
      }
      for (Player playerInRadius : playersInRadius) {
        playerInRadius.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.generateProgressBar(koth.getCapturingPlayerTime(), koth.getCaptureTime(), 5)));
        if (!bossBarPlayers.contains(playerInRadius)) kothBossBar.addPlayer(playerInRadius);
      }

      if (koth.isCapturing()) {
        Player camper = Bukkit.getPlayer(koth.getCapturingPlayer());
        if (camper == null || !koth.getRegion().contains(camper.getLocation())) {
          koth.stopCapture();
          continue;
        } 
        koth.updateCaptureTime();
        continue;
      } 
      koth.chooseKothPlayerCamper();
    } 
  }
}
