package com.hyplugins.hykoths;

import com.hyplugins.hykoths.Game.KothObject;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Utils {

  public static String translateMessage(String message) {
    Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
    Matcher matcher = pattern.matcher(message);
    while (matcher.find()) {
      String color = message.substring(matcher.start(), matcher.end());
      message = message.replace(color, String.valueOf(net.md_5.bungee.api.ChatColor.of(color)));
      matcher = pattern.matcher(message);
    }
    message = ChatColor.translateAlternateColorCodes('&', message);
    return message.replaceAll("&", "");
  }
  
  public static List<String> translateListMessage(List<String> message) {
    List<String> newMessage = new ArrayList<>();
    for (String line : message)
      newMessage.add(translateMessage(line)); 
    return newMessage;
  }
  
  public static List<Player> getPlayersWithinRadius(Location center, double radius) {
    return center.getWorld().getPlayers().stream()
      .filter(player -> (center.distance(player.getLocation()) <= radius))
      .collect(Collectors.toList());
  }
  
  public static Location getNearestLocation(List<KothObject> koths, Player player) {
    double minDistanceSquared = Double.MAX_VALUE;
    Location nearestLocation = null;
    for (Location location : koths.stream().map(k -> k.getRegion().getCenterLocation()).collect(Collectors.toList())) {
      double distanceSquared = location.distanceSquared(player.getLocation());
      if (distanceSquared < minDistanceSquared) {
        minDistanceSquared = distanceSquared;
        nearestLocation = location;
      } 
    } 
    return nearestLocation;
  }

  public static int getRemainingTimeUntilSpecifiedTime(int specifiedTimeInMinutes) {
    ZoneId mexicoCityZone = ZoneId.of("America/Mexico_City");
    ZonedDateTime currentTimeInMexicoCity = ZonedDateTime.now(mexicoCityZone);
    int currentTotalMinutes = currentTimeInMexicoCity.getHour() * 60 + currentTimeInMexicoCity.getMinute();
    int remainingTime = specifiedTimeInMinutes - currentTotalMinutes;
    if (remainingTime < 0)
      remainingTime += 1440;
    return remainingTime * 60 - currentTimeInMexicoCity.getSecond();
  }
  
  public static String generateProgressBar(int timeCaptured, int totalTimeToCapture, int bars) {
    int percentage = timeCaptured * 100 / totalTimeToCapture;
    int completedBars = (bars * timeCaptured / totalTimeToCapture);
    int emptyBars = bars - completedBars;
    StringBuilder bar = new StringBuilder();
    bar.append("&8[");
    int i;
    for (i = 0; i < completedBars; ) {
      bar.append("&5=");
      i++;
    } 
    for (i = 0; i < emptyBars; ) {
      bar.append("&7-");
      i++;
    } 
    bar.append("&8] &d");
    bar.append(percentage);
    bar.append("%");
    return translateMessage(bar.toString());
  }

  public static String replacePlaceHolders(KothObject koth, String message) {
    Location center = koth.getRegion().getCenterLocation();
    return message
            .replace("%koth%", koth.getName())
            .replace("%world_name%", koth.getRegion().getMinLocation().getWorld().getName())
            .replace("%x%", String.valueOf(center.getBlockX()))
            .replace("%y%", String.valueOf(center.getBlockY()))
            .replace("%z%", String.valueOf(center.getBlockZ()))
            .replace("%capture_time%", String.valueOf(koth.getCaptureTime()));
  }
}
