package com.hyplugins.hykoths.Config;

import com.hyplugins.hykoths.Discord.DiscordMessage;
import com.hyplugins.hykoths.Discord.Field;
import com.hyplugins.hykoths.Game.CuboidRegion;
import com.hyplugins.hykoths.Game.KothObject;
import com.hyplugins.hykoths.HyKoths;
import com.hyplugins.hykoths.InteractiveEditor.KothCreator.KothCreator;
import com.hyplugins.hykoths.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class KothConfig {
  private final HyKoths plugin;
  
  private final List<KothObject> koths;
  
  private final Map<String, String> messages;
  
  private final YamlFile kothFile;
  
  private final YamlFile messagesFile;
  
  private final Map<Integer, String[]> editingTitles;
  
  private final List<Integer> discordTimeAlertIntervals;
  
  private final List<Integer> minecraftTimeAlertIntervals;
  
  private final HashMap<EmbedType, DiscordMessage> embeds;
  
  public KothConfig(HyKoths plugin) {
    this.plugin = plugin;
    this.kothFile = new YamlFile(plugin, "koths.yml");
    this.messagesFile = new YamlFile(plugin, "messages.yml");
    this.koths = new ArrayList<>();
    this.messages = new HashMap<>();
    this.editingTitles = new HashMap<>();
    this.discordTimeAlertIntervals = new ArrayList<>();
    this.minecraftTimeAlertIntervals = new ArrayList<>();
    this.embeds = new HashMap<>();
    load();
  }
  
  public void load() {
    this.kothFile.reload();
    this.messagesFile.reload();
    this.plugin.reloadConfig();
    this.koths.clear();
    this.messages.clear();
    this.editingTitles.clear();
    this.embeds.clear();
    if (!loadKoths()) {
      this.plugin.setDisabled(false);
      Bukkit.getPluginManager().disablePlugin((Plugin)this.plugin);
      return;
    } 
    loadMessages();
    loadEditingTitles();
    loadTimeAlertIntervals();
    loadDiscordEmbeds();
  }
  
  public void loadDiscordEmbeds() {
    List<EmbedType> embedTypes = new ArrayList<>(Arrays.asList(EmbedType.values()));
    DiscordMessage.setWebhookUrl(plugin.getConfig().getString("discord.webhook-url"));
    for (EmbedType type : embedTypes) {
      ConfigurationSection embedSection = this.plugin.getConfig().getConfigurationSection("discord.discord-embeds." + type.getConfigName());
      String normalMessage = embedSection.getString("normal-message");
      String embedTitle = embedSection.getString("embed.title");
      String embedDescription = embedSection.getString("embed.description");
      String embedImageUrl = embedSection.getString("embed.image-url");
      String embedColor = embedSection.getString("embed.color");
      List<Field> fields = new ArrayList<>();
      DiscordMessage.Author author = new DiscordMessage.Author(embedSection.getString("embed.author.text"), embedSection.getString("embed.author.icon"), embedSection.getString("embed.author.url"));
      DiscordMessage.Footer footer = new DiscordMessage.Footer(embedSection.getString("embed.footer.text"), embedSection.getString("embed.footer.url"));
      for (String keyField : embedSection.getConfigurationSection("embed.fields").getKeys(false)) {
        ConfigurationSection fieldSection = embedSection.getConfigurationSection("embed.fields." + keyField);
        String titleField = fieldSection.getString("title-field");
        String textField = fieldSection.getString("text");
        boolean isInLine = fieldSection.getBoolean("isInLine");
        fields.add(new Field(titleField, textField, isInLine));
      } 
      DiscordMessage embed = new DiscordMessage(normalMessage, embedTitle, embedImageUrl, embedDescription, embedColor, fields, author, footer);
      this.embeds.put(type, embed);
    } 
  }
  
  public void loadTimeAlertIntervals() {
    List<Integer> discordAlertIntervals = this.plugin.getConfig().getIntegerList("discord.koth-alert-intervals");
    List<Integer> minecraftAlertIntervals = this.plugin.getConfig().getIntegerList("koth.koth-alert-intervals");
    Collections.sort(discordAlertIntervals);
    Collections.sort(minecraftAlertIntervals);
    this.discordTimeAlertIntervals.addAll(discordAlertIntervals);
    this.minecraftTimeAlertIntervals.addAll(minecraftAlertIntervals);
  }
  
  public void loadEditingTitles() {
    ConfigurationSection editingTitlesSection = this.messagesFile.getConfig().getConfigurationSection("editing-titles");
    if (editingTitlesSection == null) {
      this.plugin.getLogger().warning("No editing titles defined in config.");
      return;
    } 
    for (String key : editingTitlesSection.getKeys(false)) {
      ConfigurationSection editingTitleSection = editingTitlesSection.getConfigurationSection(key);
      if (editingTitleSection == null)
        continue; 
      String title = editingTitleSection.getString("title");
      String subTitle = editingTitleSection.getString("sub-title");
      if (title != null && subTitle != null)
        this.editingTitles.put(Integer.valueOf(Integer.parseInt(key)), new String[] { ChatColor.translateAlternateColorCodes('&', title), ChatColor.translateAlternateColorCodes('&', subTitle) }); 
    } 
  }
  
  public boolean loadKoths() {
    ConfigurationSection kothsSection = this.kothFile.getConfig().getConfigurationSection("koths");
    if (kothsSection == null) {
      this.plugin.getLogger().warning("No koths defined in config.");
      return false;
    }
    Set<String> kothKeys = kothsSection.getKeys(false);
    for (String kothKey : kothKeys) {
      KothType type;
      ConfigurationSection kothSection = kothsSection.getConfigurationSection(kothKey);
      if (kothSection == null) {
        this.plugin.getLogger().warning("Invalid koth configuration for key " + kothKey);
        continue;
      }
      CuboidRegion region = getRegion(kothSection);
      if (region == null) continue;
      int captureTime = kothSection.getInt("capture-time", 60);
      int duration = kothSection.getInt("duration", 300);
      try {
        type = KothType.valueOf(kothSection.getString("type"));
      } catch (IllegalArgumentException e) {
        continue;
      }
      List<ItemStack> rewards = getRewards(kothSection);
      List<Integer> schedulers = getSchedulers(kothSection);
      List<String> commands = kothSection.getStringList("commands");
      KothObject koth = new KothObject(this.plugin, kothKey, type, region, rewards, schedulers, commands, captureTime, duration);
      this.koths.add(koth);
    }
    return true;
  }
  
  private void loadMessages() {
    ConfigurationSection messagesSection = this.messagesFile.getConfig().getConfigurationSection("messages");
    if (messagesSection == null) {
      this.plugin.getLogger().warning("No messages defined in config.");
      return;
    } 
    for (String categoryKey : messagesSection.getKeys(false)) {
      ConfigurationSection categorySection = messagesSection.getConfigurationSection(categoryKey);
      if (categorySection == null)
        continue; 
      for (String messageKey : categorySection.getKeys(false)) {
        String message = categorySection.getString(messageKey);
        if (message != null)
          this.messages.put(categoryKey + "." + messageKey, ChatColor.translateAlternateColorCodes('&', message)); 
      } 
    } 
  }
  
  private CuboidRegion getRegion(ConfigurationSection kothSection) {
    ConfigurationSection regionSection = kothSection.getConfigurationSection("region");
    if (regionSection == null) {
      this.plugin.getLogger().warning("No region defined for koth " + kothSection.getName());
      return null;
    } 
    String worldName = kothSection.getString("world");
    Location loc1 = getLocation(regionSection, "loc1", worldName);
    Location loc2 = getLocation(regionSection, "loc2", worldName);
    if (loc1 == null || loc2 == null) {
      this.plugin.getLogger().warning("Invalid region configuration for koth " + kothSection.getName());
      return null;
    } 
    return new CuboidRegion(loc1, loc2);
  }
  
  private Location getLocation(ConfigurationSection section, String key, String worldName) {
    ConfigurationSection locSection = section.getConfigurationSection(key);
    if (locSection == null) {
      this.plugin.getLogger().warning("No location defined for " + key);
      return null;
    } 
    World world = Bukkit.getWorld(worldName);
    if (world == null) {
      this.plugin.getLogger().warning("Invalid world name for " + key);
      return null;
    } 
    double x = locSection.getDouble("x");
    double y = locSection.getDouble("y");
    double z = locSection.getDouble("z");
    float pitch = (float)locSection.getDouble("pitch");
    float yaw = (float)locSection.getDouble("yaw");
    return new Location(world, x, y, z, yaw, pitch);
  }
  
  private List<ItemStack> getRewards(ConfigurationSection kothConfig) {
    List<ItemStack> rewards = new ArrayList<>();
    ConfigurationSection rewardsConfig = kothConfig.getConfigurationSection("rewards");
    if (rewardsConfig != null) {
      Set<String> rewardKeys = rewardsConfig.getKeys(false);
      for (String rewardKey : rewardKeys) {
        ConfigurationSection rewardSection = rewardsConfig.getConfigurationSection(rewardKey);
        if (rewardSection != null) {
          int data = rewardSection.getInt("data");
          ItemStack reward = getItemStack(rewardSection.getString("material"), data);
          ItemMeta meta = reward.getItemMeta();
          meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', rewardSection.getString("display-name")));
          meta.setLore(Utils.translateListMessage(rewardSection.getStringList("lore")));
          for (String enchantment : rewardSection.getStringList("enchantments")) {
            String[] args = enchantment.split(":");
            Enchantment enchant = ((XEnchant)XEnchant.matchXEnchantment(args[0]).get()).getEnchant();
            meta.addEnchant(enchant, Integer.parseInt(args[1]), true);
          } 
          reward.setItemMeta(meta);
          rewards.add(reward);
        } 
      } 
    } 
    return rewards;
  }
  
  public ItemStack getItemStack(String stringMaterial, int data) {
    ItemStack item;
    Optional<XMaterial> material = XMaterial.matchXMaterial(Objects.<String>requireNonNull(stringMaterial));
    if (data == 1) {
      item = new ItemStack(((XMaterial)material.get()).parseItem().getType());
    } else {
      item = new ItemStack(((XMaterial)material.get()).parseItem().getType(), 1, (short)data);
    } 
    return item;
  }
  
  private List<Integer> getSchedulers(ConfigurationSection kothSection) {
    List<Integer> schedulers = new ArrayList<>();
    if (kothSection.contains("koth-schedule")) {
      List<String> schedules = kothSection.getStringList("koth-schedule");
      for (String schedule : schedules) {
        try {
          int hour = Integer.parseInt(schedule.split(":")[0]);
          int minute = Integer.parseInt(schedule.split(":")[1]);
          if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59)
            schedulers.add(Integer.valueOf(hour * 60 + minute)); 
        } catch (NumberFormatException|ArrayIndexOutOfBoundsException e) {
          this.plugin.getLogger().warning("Invalid schedule format: " + schedule);
        } 
      } 
    } 
    return schedulers;
  }
  
  public void addKothToConfig(KothCreator koth) {
    ConfigurationSection kothSection = this.kothFile.getConfig().createSection("koths." + koth.getName());
    kothSection.set("type", koth.getType().toString());
    ConfigurationSection regionSection = kothSection.createSection("region");
    Location loc1 = koth.getLocation1();
    Location loc2 = koth.getLocation2();
    kothSection.set("world", loc1.getWorld().getName());
    ConfigurationSection loc1Section = regionSection.createSection("loc1");
    loc1Section.set("x", Integer.valueOf(loc1.getBlockX()));
    loc1Section.set("y", Integer.valueOf(loc1.getBlockY()));
    loc1Section.set("z", Integer.valueOf(loc1.getBlockZ()));
    loc1Section.set("pitch", Float.valueOf(loc1.getPitch()));
    loc1Section.set("yaw", Float.valueOf(loc1.getYaw()));
    ConfigurationSection loc2Section = regionSection.createSection("loc2");
    loc2Section.set("x", Integer.valueOf(loc2.getBlockX()));
    loc2Section.set("y", Integer.valueOf(loc2.getBlockY()));
    loc2Section.set("z", Integer.valueOf(loc2.getBlockZ()));
    loc2Section.set("pitch", Float.valueOf(loc2.getPitch()));
    loc2Section.set("yaw", Float.valueOf(loc2.getYaw()));
    ConfigurationSection rewardsSection = kothSection.createSection("rewards");
    boolean isNewMaterialsVersion = (XMaterial.getVersion() > 8);
    for (ItemStack reward : koth.getRewards()) {
      int values = rewardsSection.getKeys(false).size();
      ConfigurationSection rewardSection = rewardsSection.createSection(String.valueOf(values));
      rewardSection.set("material", reward.getType().name());
      rewardSection.set("amount", Integer.valueOf(reward.getAmount()));
      rewardSection.set("data", Short.valueOf(isNewMaterialsVersion ? 0 : reward.getDurability()));
      ItemMeta meta = reward.getItemMeta();
      rewardSection.set("display-name", (meta.getDisplayName() == null) ? " " : meta.getDisplayName());
      rewardSection.set("lore", (meta.getLore() == null) ? new ArrayList() : meta.getLore());
      List<String> enchantsConfig = new ArrayList<>();
      Map<Enchantment, Integer> enchants = meta.getEnchants();
      for (Enchantment enchant : enchants.keySet())
        enchantsConfig.add(enchant.getName() + ":" + enchants.get(enchant)); 
      rewardSection.set("enchantments", enchantsConfig);
    } 
    kothSection.set("capture-time", Integer.valueOf(koth.getCaptureTime()));
    kothSection.set("duration", Integer.valueOf(koth.getDuration()));
    kothSection.set("koth-schedule", koth.getSchedulersString());
    kothSection.set("commands", koth.getCommands());
    this.kothFile.save();
  }
  
  public void removeKothFromConfig(String kothName) {
    this.kothFile.getConfig().set("koths." + kothName, null);
    this.kothFile.save();
  }
  
  public KothObject getKothByName(String name) {
    return this.plugin.getKothConfig().getKoths()
      .stream()
      .filter(k -> k.getName().equalsIgnoreCase(name))
      .findFirst()
      .orElse(null);
  }
  
  public List<KothObject> getKothType(KothType type) {
    return this.koths.stream().filter(k -> k.getType().equals(type))
      .collect(Collectors.toList());
  }
  
  public List<KothObject> getKoths() {
    return this.koths;
  }
  
  public Map<Integer, String[]> getEditingTitles() {
    return this.editingTitles;
  }
  
  public List<Integer> getDiscordTimeAlertIntervals() {
    return this.discordTimeAlertIntervals;
  }
  
  public List<Integer> getMinecraftTimeAlertIntervals() {
    return this.minecraftTimeAlertIntervals;
  }
  
  public String getMessage(String key) {
    return Utils.translateMessage(this.messages.getOrDefault(key, "Message not found: messages." + key));
  }
  
  public List<String> getListMessage(String key) {
    return Utils.translateListMessage(this.messagesFile.getConfig().getStringList(key));
  }
  
  public HashMap<EmbedType, DiscordMessage> getEmbeds() {
    return this.embeds;
  }
  
  public YamlFile getKothFile() {
    return this.kothFile;
  }
  
  public YamlFile getMessagesFile() {
    return this.messagesFile;
  }
}
