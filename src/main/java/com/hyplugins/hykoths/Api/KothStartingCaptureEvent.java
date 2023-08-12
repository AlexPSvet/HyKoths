package com.hyplugins.hykoths.Api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KothStartingCaptureEvent extends Event {
  private static final HandlerList HANDLERS = new HandlerList();
  
  @NotNull
  public HandlerList getHandlers() {
    return HANDLERS;
  }
  
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }
}
