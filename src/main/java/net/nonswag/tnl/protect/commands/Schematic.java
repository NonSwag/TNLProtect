package net.nonswag.tnl.protect.commands;

import net.nonswag.tnl.core.api.command.CommandSource;
import net.nonswag.tnl.core.api.command.Invocation;
import net.nonswag.tnl.listener.api.command.exceptions.InvalidUseException;
import net.nonswag.tnl.listener.api.command.simple.SubCommand;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.protect.Protect;
import net.nonswag.tnl.protect.api.area.Area;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

class Schematic extends SubCommand {

    Schematic() {
        super("schematic");
    }

    @Override
    protected void execute(@Nonnull Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (args.length < 2) throw new InvalidUseException(this);
        Area area = args.length >= 3 ? Area.get(args[2]) : source.isPlayer() ? Area.highestArea((TNLPlayer) source) : null;
        if (area != null && !area.isGlobalArea()) Protect.getInstance().async(() -> {
            if (args[1].equalsIgnoreCase("load")) {
                if (area.isTooBig()) source.sendMessage("%prefix% §7Warning§8: §6The area is very big");
                if (area.getSchematic().load()) {
                    source.sendMessage("%prefix% §7Loaded schematic§8: §6" + area);
                } else source.sendMessage("%prefix% §cFailed to load schematic §4" + area);
            } else if (args[1].equalsIgnoreCase("delete")) {
                if (area.isTooBig()) source.sendMessage("%prefix% §7Warning§8: §6The area is very big");
                if (area.getSchematic().delete()) {
                    source.sendMessage("%prefix% §7Deleted schematic§8: §6" + area);
                } else source.sendMessage("%prefix% §cFailed to delete schematic §4" + area);
            } else if (args[1].equalsIgnoreCase("save")) {
                if (area.isTooBig()) source.sendMessage("%prefix% §7Warning§8: §6The area is very big");
                if (area.getSchematic().save())
                    source.sendMessage("%prefix% §7Saved schematic§8: §6" + area);
                else source.sendMessage("%prefix% §cFailed to save schematic §4" + area);
            } else throw new InvalidUseException(this);
        });
        else if (area == null) source.sendMessage("%prefix% §c/area schematic " + args[1] + " §8[§6Area§8]");
        else if (area.isGlobalArea()) source.sendMessage("%prefix% §cCan't use a global area");
    }

    @Nonnull
    @Override
    protected List<String> suggest(@Nonnull Invocation invocation) {
        String[] args = invocation.arguments();
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add("delete");
            suggestions.add("load");
            suggestions.add("save");
        } else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("load") || args[1].equalsIgnoreCase("delete")) {
                for (Area area : Area.areas()) {
                    if (!area.isGlobalArea() && area.getSchematic().getFile().exists()) suggestions.add(area.getName());
                }
            } else if (args[1].equalsIgnoreCase("save")) suggestions.addAll(Area.userAreaNames());
        }
        return suggestions;
    }

    @Override
    public void usage(@Nonnull Invocation invocation) {
        CommandSource source = invocation.source();
        if (source.isPlayer()) source.sendMessage("%prefix% §c/area schematic §8[§6Option§8] §8(§6Area§8)");
        else source.sendMessage("%prefix% §c/area schematic §8[§6Option§8] §8[§6Area§8]");
    }
}
