package com.hyplugins.hykoths.Game;

import com.hyplugins.hykoths.Config.EmbedType;
import com.hyplugins.hykoths.Config.KothType;
import com.hyplugins.hykoths.HyKoths;
import com.hyplugins.hykoths.Utils;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class KothScheduler extends BukkitRunnable {
  private final HyKoths plugin;
  
  private final int counterValue;
  
  private int randomKothCounter;
  
  public KothScheduler(HyKoths plugin, int counterValue) {
    this.plugin = plugin;
    this.counterValue = counterValue;
    this.randomKothCounter = counterValue;
    runTaskTimerAsynchronously(plugin, 0L, 20L);
  }
  
  public void run() {
    for (KothObject koth : this.plugin.getKothConfig().getKothType(KothType.TIMER)) {
      List<Integer> schedulers = koth.getSchedulers();
      for (int scheduler : schedulers) {
        int remainingTime = Utils.getRemainingTimeUntilSpecifiedTime(scheduler);
        for (Integer value : this.plugin.getKothConfig().getDiscordTimeAlertIntervals()) {
          if (remainingTime == value) {
            plugin.getKothConfig().getEmbeds().get(EmbedType.KOTH_STARTING).sendMessage(koth);
            break;
          }
        }
        for (Integer value : this.plugin.getKothConfig().getMinecraftTimeAlertIntervals()) {
          if (remainingTime == value) {
            Bukkit.broadcastMessage(this.plugin.getKothConfig().getMessage("koth.koth-starting").replace("%koth%", koth.getName()).replace("%minutes%", String.valueOf(value/60)));
            break;
          }
        }
        if (remainingTime == 0) {
          if (!koth.isActive()) {
            Bukkit.getScheduler().runTask(plugin, koth::start);
            plugin.getKothConfig().getEmbeds().get(EmbedType.KOTH_START).sendMessage(koth);
          }
        }
      } 
    } 
    if (this.randomKothCounter == 0) {
      Random random = new Random();
      List<KothObject> randomKoths = this.plugin.getKothConfig().getKothType(KothType.RANDOM);
      if (!randomKoths.isEmpty()) {
        int kothIndex = random.nextInt(randomKoths.size());
        KothObject koth = randomKoths.get(kothIndex);
        Bukkit.getScheduler().runTask(plugin, koth::start);
        plugin.getKothConfig().getEmbeds().get(EmbedType.RANDOM_KOTH).sendMessage(koth);
        this.randomKothCounter = this.counterValue;
      } 
    } 
    this.randomKothCounter--;
  }
}
