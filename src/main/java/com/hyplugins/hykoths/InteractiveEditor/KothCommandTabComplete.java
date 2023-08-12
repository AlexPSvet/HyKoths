package com.hyplugins.hykoths.InteractiveEditor;

import com.hyplugins.hykoths.Game.KothObject;
import com.hyplugins.hykoths.HyKoths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

public class KothCommandTabComplete implements TabCompleter {
  private final HyKoths plugin;
  
  public KothCommandTabComplete(HyKoths plugin) {
    this.plugin = plugin;
  }
  
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
    List<String> argsCase1;
    List<String> argsCase2;
    switch (args.length) {
      case 1:
        List<String> commandArgs = new ArrayList<>(Arrays.asList("create", "reload", "delete", "list", "start", "stop"));
        return commandArgs.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
      case 2:
        switch (args[0]) {
          case "delete":
          case "start":
          case "stop":
            argsCase2 = new ArrayList<>();
            for (KothObject koth : this.plugin.getKothConfig().getKoths()) {
              if (koth.getName().startsWith(args[1]))
                argsCase2.add(koth.getName()); 
            } 
            return argsCase2;
        } 
        break;
    } 
    return null;
  }
}
