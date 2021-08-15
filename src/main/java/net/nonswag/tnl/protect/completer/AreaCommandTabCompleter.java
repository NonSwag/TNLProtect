package net.nonswag.tnl.protect.completer;

import net.nonswag.tnl.protect.api.area.Area;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AreaCommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length <= 1) {
            suggestions.add("create");
            suggestions.add("list");
            suggestions.add("info");
            suggestions.add("priority");
            if (!Area.userAreas().isEmpty()) {
                suggestions.add("select");
                suggestions.add("delete");
                suggestions.add("schematic");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("select")) {
                suggestions.addAll(Area.userAreaNames());
            } else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("priority")) {
                suggestions.addAll(Area.names());
            } else if (args[0].equalsIgnoreCase("schematic")) {
                suggestions.add("load");
                suggestions.add("save");
                suggestions.add("delete");
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("priority")) {
                for (int i = 0; i <= 10; i++) suggestions.add(String.valueOf(i));
            } else if (args[0].equalsIgnoreCase("schematic")) {
                if (args[1].equalsIgnoreCase("load") || args[1].equalsIgnoreCase("save") || args[1].equalsIgnoreCase("delete")) {
                    suggestions.addAll(Area.userAreaNames());
                }
            }
        }
        if (args.length > 0 && !suggestions.isEmpty()) {
            suggestions.removeIf(completer -> !completer.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        }
        return suggestions;
    }
}
