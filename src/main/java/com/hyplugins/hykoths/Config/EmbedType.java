package com.hyplugins.hykoths.Config;

public enum EmbedType {

  KOTH_STARTING("koth-starting"),
  KOTH_START("koth-started"),
  KOTH_STOP("koth-stopped"),
  RANDOM_KOTH("random-koth");
  
  private String configName;
  
  EmbedType(String configName) {
    this.configName = configName;
  }
  
  public String getConfigName() {
    return this.configName;
  }
}
