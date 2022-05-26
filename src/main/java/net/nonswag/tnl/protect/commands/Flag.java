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

class Flag extends SubCommand {

    Flag() {
        super("flag");
    }

    @Override
    protected void execute(@Nonnull Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (args.length >= 2 && args[1].equalsIgnoreCase("set")) {
            Area area = args.length >= 5 ? Area.get(args[4]) : source.isPlayer() ? Area.highestArea((TNLPlayer) source) : null;
            var flag = args.length >= 3 ? net.nonswag.tnl.protect.api.area.Flag.valueOf(args[2]) : null;
            if (flag != null) {
                Object value = args.length >= 4 ? flag.possibilities().get(args[3]) : null;
                if (area != null && value != null && area.setFlag(flag, value)) {
                    source.sendMessage("%prefix% §aChanged flag §6" + flag + "§a of area §6" + area + "§a to §6" + args[3]);
                } else if (value != null && area != null) source.sendMessage("%prefix% §cNothing could be changed");
                else source.sendMessage("%prefix% §c/area flag set " + flag + " §8[§6Value§8] §8[§6Area§8]");
            } else source.sendMessage("%prefix% §c/area flag set §8[§6Flag§8] §8[§6Value§8] §8[§6Area§8]");
        } else if (args.length >= 2 && args[1].equalsIgnoreCase("unset")) {
            Area area = args.length >= 4 ? Area.get(args[3]) : source.isPlayer() ? Area.highestArea((TNLPlayer) source) : null;
            var flag = args.length >= 3 ? net.nonswag.tnl.protect.api.area.Flag.valueOf(args[2]) : null;
            if (flag != null && area != null && area.unsetFlag(flag)) {
                source.sendMessage("%prefix% §aUnset flag §6" + flag + "§a of the area §6" + area);
            } else if (flag != null && area != null) source.sendMessage("%prefix% §cNothing could be changed");
            else if (flag != null) source.sendMessage("%prefix% §c/area flag unset " + flag + " §8[§6Area§8]");
            else source.sendMessage("%prefix% §c/area flag unset §8[§6Flag§8] §8(§6Area§8)");
        } else if (args.length >= 2 && args[1].equalsIgnoreCase("info")) {
            Area area = args.length >= 4 ? Area.get(args[3]) : source.isPlayer() ? Area.highestArea((TNLPlayer) source) : null;
            var flag = args.length >= 3 ? net.nonswag.tnl.protect.api.area.Flag.valueOf(args[2]) : null;
            if (flag != null && area != null && area.hasFlag(flag)) {
                source.sendMessage("%prefix% §7Area§8: §6" + area);
                source.sendMessage("%prefix% §7Flag§8: §6" + flag);
                source.sendMessage("%prefix% §7Default Value§8: §6" + flag.defaultValue());
                source.sendMessage("%prefix% §7Provided Value§8: §6" + area.getFlag(flag));
            } else if (area != null && flag != null) {
                source.sendMessage("%prefix% §cThe flag §4" + flag + "§c is not defined for the area §4" + area);
            } else if (flag != null) source.sendMessage("%prefix% §c/area flag info " + flag + " §8[§6Area§8]");
            else source.sendMessage("%prefix% §c/area flag info §8[§6Flag§8] §8(§6Area§8)");
        } else throw new InvalidUseException(this);
    }

    @Nonnull
    @Override
    protected List<String> suggest(@Nonnull Invocation invocation) {
        List<String> suggestions = new ArrayList<>();
        String[] args = invocation.arguments();
        if (args.length == 2) {
            suggestions.add("set");
            suggestions.add("unset");
            suggestions.add("info");
        } else if (args.length == 3) suggestions.addAll(net.nonswag.tnl.protect.api.area.Flag.names());
        else if (args.length == 4 && args[1].equalsIgnoreCase("set")) {
            var flag = net.nonswag.tnl.protect.api.area.Flag.valueOf(args[2]);
            if (flag != null) suggestions.addAll(flag.possibilities().keySet());
        } else if (args.length == 4 || (args.length == 5 && args[1].equals("set"))) {
            suggestions.addAll(Area.userAreaNames());
        }
        return suggestions;
    }

    @Override
    public void usage(@Nonnull Invocation invocation) {
        CommandSource source = invocation.source();
        if (source.isPlayer()) {
            source.sendMessage("%prefix% §c/area flag set §8[§6Flag§8] §8[§6Value§8] §8(§6Area§8)");
            source.sendMessage("%prefix% §c/area flag unset §8[§6Flag§8] §8(§6Area§8)");
            source.sendMessage("%prefix% §c/area flag info §8[§6Flag§8] §8(§6Area§8)");
        } else {
            source.sendMessage("%prefix% §c/area flag set §8[§6Flag§8] §8[§6Value§8] §8[§6Area§8]");
            source.sendMessage("%prefix% §c/area flag unset §8[§6Flag§8] §8[§6Area§8]");
            source.sendMessage("%prefix% §c/area flag info §8[§6Flag§8] §8[§6Area§8]");
        }
    }
}
