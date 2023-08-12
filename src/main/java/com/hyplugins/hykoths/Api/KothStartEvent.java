package com.hyplugins.hykoths.Api;

import com.hyplugins.hykoths.Game.KothObject;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KothStartEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  
  private final KothObject koth;
  
  private boolean cancelled;
  
  public KothStartEvent(KothObject koth) {
    this.koth = koth;
  }
  
  public String getKothName() {
    return this.koth.getName();
  }
  
  public Integer getKothDuration() {
    return Integer.valueOf(this.koth.getDuration());
  }
  
  public Integer getCaptureTime() {
    return Integer.valueOf(this.koth.getCaptureTime());
  }
  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  @NotNull
  public HandlerList getHandlers() {
    return HANDLERS;
  }
  
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}
