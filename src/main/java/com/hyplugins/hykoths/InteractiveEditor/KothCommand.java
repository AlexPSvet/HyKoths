package com.hyplugins.hykoths.InteractiveEditor;

import com.hyplugins.hykoths.Config.EmbedType;
import com.hyplugins.hykoths.Game.KothObject;
import com.hyplugins.hykoths.HyKoths;
import com.hyplugins.hykoths.InteractiveEditor.KothCreator.KothCreator;
import com.hyplugins.hykoths.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KothCommand implements CommandExecutor {
  private final HyKoths plugin;
  
  public KothCommand(HyKoths plugin) {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    switch (args.length) {
      case 0:
        for (String line : this.plugin.getKothConfig().getListMessage("command-list"))
          sender.sendMessage(Utils.translateMessage(line));
        return false;
      case 1:
        switch (args[0]) {
          case "create":
            handleEditorMode(sender);
            return false;
          case "reload":
            this.plugin.getKothConfig().load();
            sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.plugin-reloaded"));
            return false;
          case "list":
            handleListCommand(sender);
            return false;
          case "editor":
            handleEditorCommand(sender);
            return false;
        } 
        sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.command-not-found"));
        return false;
      case 2:
        switch (args[0]) {
          case "delete":
            handleKothDeleteCommand(sender, args[1]);
            return false;
          case "start":
            handleStartKothCommand(sender, args[1]);
            return false;
          case "stop":
            handleStopCommand(sender, args[1]);
            return false;
        } 
        sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.command-not-found"));
        return false;
    } 
    sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.command-not-found"));
    return false;
  }
  
  public void handleEditorMode(CommandSender sender) {
    if (sender instanceof Player && sender.hasPermission("hykoths.creator")) {
      if (this.plugin.getKothEditor().getEditing().containsKey(((Player)sender).getUniqueId())) {
        KothCreator kothCreator = this.plugin.getKothEditor().getEditing().get(((Player)sender).getUniqueId());
        kothCreator.cancel();
        this.plugin.getKothEditor().getEditing().remove(((Player)sender).getUniqueId());
        sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.creator-remove"));
      } else {
        this.plugin.getKothEditor().newKothEditor((Player)sender);
      } 
    } else {
      sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.no-console"));
    } 
  }
  
  public void handleListCommand(CommandSender sender) {
    if (sender.hasPermission("hykoths.list")) {
      sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.koth-list-message"));
      String formatMessage = this.plugin.getKothConfig().getMessage("commands.koth-list-format");
      for (KothObject koth : this.plugin.getKothConfig().getKoths())
        sender.sendMessage(formatMessage.replace("%koth-name%", koth.getName()).replace("%koth-type%", koth.getType().name()).replace("%duration%", koth.getDuration() + "s").replace("%camp-duration%", koth.getCaptureTime() + "s")); 
    } else {
      sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.no-perms"));
    } 
  }
  
  public void handleEditorCommand(CommandSender sender) {
    if (sender instanceof Player) {
      if (sender.hasPermission("hykoths.editor")) {
        this.plugin.getMenus().getKothListMenu().openMenu((Player)sender);
      } else {
        sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.no-perms"));
      } 
    } else {
      sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.no-console"));
    } 
  }
  
  public void handleKothDeleteCommand(CommandSender sender, String arg) {
    if (sender.hasPermission("hykoths.delete")) {
      KothObject koth = this.plugin.getKothConfig().getKothByName(arg);
      if (koth != null) {
        if (koth.isActive())
          Bukkit.broadcastMessage(this.plugin.getKothConfig().getMessage("commands.koth-stop-deleted").replace("%koth%", koth.getName())); 
        koth.stop();
        this.plugin.getKothConfig().removeKothFromConfig(koth.getName());
        this.plugin.getKothConfig().getKoths().remove(koth);
        sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.koth-stop-success"));
      } else {
        sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.koth-name-invalid").replace("%value%", arg));
      } 
    } else {
      sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.no-perms"));
    } 
  }
  
  public void handleStartKothCommand(CommandSender sender, String arg) {
    if (sender.hasPermission("hykoths.start")) {
      KothObject koth = this.plugin.getKothConfig().getKothByName(arg);
      if (koth != null) {
        if (koth.isActive()) {
          sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.koth-already-active").replace("%koth%", koth.getName()));
        } else {
          koth.start();
          plugin.getKothConfig().getEmbeds().get(EmbedType.KOTH_START).sendMessage(koth);
          sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.koth-start-admin").replace("%koth%", koth.getName()));
        } 
      } else {
        sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.koth-name-invalid").replace("%value%", arg));
      } 
    } else {
      sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.no-perms"));
    } 
  }
  
  public void handleStopCommand(CommandSender sender, String arg) {
    if (sender.hasPermission("hykoths.stop")) {
      KothObject koth = this.plugin.getKothConfig().getKothByName(arg);
      if (koth != null) {
        if (koth.isActive()) {
          koth.stop();
          Bukkit.broadcastMessage(this.plugin.getKothConfig().getMessage("commands.koth-force-stop").replace("%koth%", koth.getName()));
          sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.koth-force-stop-admin-message").replace("%koth%", koth.getName()));
        } else {
          sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.koth-not-active").replace("%koth%", koth.getName()));
        } 
      } else {
        sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.koth-name-invalid").replace("%value%", arg));
      } 
    } else {
      sender.sendMessage(this.plugin.getKothConfig().getMessage("commands.no-perms"));
    } 
  }
}
