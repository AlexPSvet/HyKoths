package com.hyplugins.hykoths.Config;

import com.hyplugins.hykoths.HyKoths;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlFile {
  private final File file;
  
  private YamlConfiguration config;
  
  public YamlFile(HyKoths plugin, String fileName) {
    this.file = new File(plugin.getDataFolder(), fileName);
    plugin.saveResource(fileName, false);
    reload();
  }
  
  public YamlConfiguration getConfig() {
    return this.config;
  }
  
  public void save() {
    try {
      this.config.save(this.file);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  public void reload() {
    this.config = YamlConfiguration.loadConfiguration(this.file);
  }
}
