package me.mrCookieSlime.Slimefun.Commands;

import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;


public class SlimefunTabCompleter
        implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return createReturnList(SlimefunCommand.tabs, args[0]);
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                return createReturnList(Slimefun.listIDs(), args[2]);
            }
            if (args[0].equalsIgnoreCase("research")) {
                List<String> researches = new ArrayList<>();
                for (Research res : Research.list()) {
                    researches.add(res.getName().toUpperCase().replace(" ", "_"));
                }
                researches.add("all");
                return createReturnList(researches, args[2]);
            }

            return null;
        }


        return null;
    }


    private List<String> createReturnList(List<String> list, String string) {
        if (string.equals("")) return list;

        List<String> returnList = new ArrayList<>();
        for (String item : list) {
            if (item.toLowerCase().startsWith(string.toLowerCase())) {
                returnList.add(item);
            }
        }
        return returnList;
    }
}


