package com.hyplugins.hykoths.Api;

import com.hyplugins.hykoths.Config.KothConfig;
import com.hyplugins.hykoths.Game.KothObject;
import com.hyplugins.hykoths.HyKoths;
import java.util.List;
import java.util.stream.Collectors;

public class HyKothsAPI {
  private final HyKoths plugin;
  
  public HyKothsAPI(HyKoths plugin) {
    this.plugin = plugin;
  }
  
  public void startKoth(String kothName) {
    KothConfig a = this.plugin.getKothConfig();
    KothObject d = a.getKothByName(kothName);
    if (d != null) {
      if (!d.isActive())
        d.start(); 
    } else {
      this.plugin.getLogger().warning("API USAGE - Koth Start - Koth " + kothName + " doesn't exist.");
    } 
  }
  
  public void stopKoth(String kothName) {
    KothConfig a = this.plugin.getKothConfig();
    KothObject d = a.getKothByName(kothName);
    if (d != null) {
      if (d.isActive())
        d.stop(); 
    } else {
      this.plugin.getLogger().warning("API USAGE - Koth Stop - Koth " + kothName + " doesn't exist.");
    } 
  }
  
  public List<String> getKothListNames() {
    KothConfig a = this.plugin.getKothConfig();
    return (List<String>)a.getKoths().stream().map(KothObject::getName).collect(Collectors.toList());
  }
}
