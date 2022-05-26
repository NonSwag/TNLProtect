package net.nonswag.tnl.protect.commands;

import net.nonswag.tnl.core.api.command.CommandSource;
import net.nonswag.tnl.core.api.command.Invocation;
import net.nonswag.tnl.listener.api.command.exceptions.InvalidUseException;
import net.nonswag.tnl.listener.api.command.simple.SubCommand;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.protect.api.area.Area;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

class Priority extends SubCommand {

    Priority() {
        super("priority");
    }

    @Override
    protected void execute(@Nonnull Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (args.length >= 2 && args[1].equalsIgnoreCase("get")) {
            Area area = args.length >= 3 ? Area.get(args[2]) : source.isPlayer() ? Area.highestArea((TNLPlayer) source) : null;
            if (area == null) source.sendMessage("%prefix% §c/area priority get §8[§6Area§8]");
            else source.sendMessage("%prefix% §7Priority §8(§a" + area + "§8): §6" + area.getPriority());
        } else if (args.length >= 2 && args[1].equalsIgnoreCase("set")) {
            Area area = args.length >= 4 ? Area.get(args[3]) : source.isPlayer() ? Area.highestArea((TNLPlayer) source) : null;
            if (args.length >= 3) {
                try {
                    int priority = Integer.parseInt(args[2]);
                    if (area != null) {
                        area.setPriority(priority);
                        source.sendMessage("%prefix% §aChanged Priority of §6" + area + "§a to §6" + priority);
                    } else source.sendMessage("%prefix% §c/area priority set " + priority + " §8[§6Area§8]");
                } catch (NumberFormatException ignored) {
                    source.sendMessage("%prefix% §c/area priority set §8[§6Priority§8] §8(§6Area§8)");
                }
            } else source.sendMessage("%prefix% §c/area priority set §8[§6Priority§8] §8(§6Area§8)");
        } else throw new InvalidUseException(this);
    }

    @Nonnull
    @Override
    protected List<String> suggest(@Nonnull Invocation invocation) {
        List<String> suggestions = new ArrayList<>();
        String[] args = invocation.arguments();
        if (args.length == 2) {
            suggestions.add("set");
            suggestions.add("get");
        } else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("set")) for (int i = 0; i <= 10; i++) suggestions.add(String.valueOf(i));
            else if (args[1].equalsIgnoreCase("get")) suggestions.addAll(Area.names());
        } else if (args.length == 4 && args[1].equalsIgnoreCase("set")) suggestions.addAll(Area.names());
        return suggestions;
    }

    @Override
    public void usage(@Nonnull Invocation invocation) {
        CommandSource source = invocation.source();
        if (source.isPlayer()) {
            source.sendMessage("%prefix% §c/area priority set §8[§6Priority§8] §8(§6Area§8)");
            source.sendMessage("%prefix% §c/area priority get §8(§6Area§8)");
        } else {
            source.sendMessage("%prefix% §c/area priority set §8[§6Priority§8] §8[§6Area§8]");
            source.sendMessage("%prefix% §c/area priority get §8[§6Area§8]");
        }
    }
}
