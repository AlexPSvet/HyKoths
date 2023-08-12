package com.hyplugins.hykoths;

import com.hyplugins.hykoths.Game.KothObject;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class PlaceHolderAPI extends PlaceholderExpansion {

  private final HyKoths plugin;

  public PlaceHolderAPI(HyKoths plugin) {
    this.plugin = plugin;
  }

  public String getAuthor() {
    return "HyStudios";
  }
  
  public String getIdentifier() {
    return "hykoths";
  }
  
  public String getVersion() {
    return "1.0";
  }
  
  public String onPlaceholderRequest(Player player, String params) {
    if (player != null) {
      String[] args = params.split("_");
      if (args.length == 2) {
        String kothName = args[0];
        KothObject koth = plugin.getKothConfig().getKoths().stream().filter(k -> k.getName().equals(kothName)).collect(Collectors.toList()).get(0);
        switch (args[1].toLowerCase()) {
          case "active":
            return koth.isActive() ? Utils.translateMessage("&2&lSI") : Utils.translateMessage("&4&lNO");
          case "bar":
            return Utils.generateProgressBar(koth.getCapturingPlayerTime(), koth.getCaptureTime(), 5);
          case "camper":
            if (koth.getCapturingPlayer() == null) {
              return "Ningún jugador";
            } else {
              return Bukkit.getPlayer(koth.getCapturingPlayer()).getName();
            }
          case "capturetime":
            return String.valueOf(koth.getCapturingPlayerTime());
          case "maxcapturetime":
            return String.valueOf(koth.getCaptureTime());
        }
      }
    }
    return "N/A";
  }
}
