package com.resourcepoint;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("srcpnt")) {
            if (args.length == 1) {
                suggestions = Arrays.asList("help", "create", "list", "delete");
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("create")) {
                    suggestions = Arrays.asList("chest", "ender_chest", "iron_block", "gold_block", "diamond_block", "emerald_block", "glass");
                }
            } else if (args.length == 5) {
                if (args[0].equalsIgnoreCase("create")) {
                    suggestions = Arrays.asList("herb", "fishing_rod", "iron", "gold", "diamond");
                }
            }
        }

        // 过滤建议，匹配输入内容
        List<String> filteredSuggestions = new ArrayList<>();
        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                filteredSuggestions.add(suggestion);
            }
        }

        return filteredSuggestions;
    }
}
