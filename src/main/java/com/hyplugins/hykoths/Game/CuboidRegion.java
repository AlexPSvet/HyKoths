package com.hyplugins.hykoths.Game;

import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CuboidRegion {
  private final Location minLoc;
  
  private final Location maxLoc;
  
  public CuboidRegion(Location loc1, Location loc2) {
    double minX = Math.min(loc1.getX(), loc2.getX());
    double minY = Math.min(loc1.getY(), loc2.getY());
    double minZ = Math.min(loc1.getZ(), loc2.getZ());
    double maxX = Math.max(loc1.getX(), loc2.getX()) + 1.0D;
    double maxY = Math.max(loc1.getY(), loc2.getY()) + 1.0D;
    double maxZ = Math.max(loc1.getZ(), loc2.getZ()) + 1.0D;
    this.minLoc = new Location(loc1.getWorld(), minX, minY, minZ);
    this.maxLoc = new Location(loc1.getWorld(), maxX, maxY, maxZ);
  }
  
  public boolean contains(Location loc) {
    if (loc.getWorld() != this.minLoc.getWorld())
      return false; 
    double x = loc.getX();
    double y = loc.getY();
    double z = loc.getZ();
    return (x >= this.minLoc.getX() && x <= this.maxLoc.getX() && y >= this.minLoc
      .getY() && y <= this.maxLoc.getY() && z >= this.minLoc
      .getZ() && z <= this.maxLoc.getZ());
  }
  
  public Location getMinLocation() {
    return this.minLoc;
  }
  
  public Location getMaxLocation() {
    return this.maxLoc;
  }
  
  public Location getCenterLocation() {
    double centerX = (this.minLoc.getX() + this.maxLoc.getX()) / 2.0D;
    double centerY = (this.minLoc.getY() + this.maxLoc.getY()) / 2.0D;
    double centerZ = (this.minLoc.getZ() + this.maxLoc.getZ()) / 2.0D;
    return new Location(this.minLoc.getWorld(), centerX, centerY, centerZ);
  }
  
  public List<Player> getPlayersInside() {
    World world = this.minLoc.getWorld();
    return (List<Player>)world.getPlayers().stream()
      .filter(player -> contains(player.getLocation()))
      .collect(Collectors.toList());
  }
}
