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

class Delete extends SubCommand {

    Delete() {
        super("delete");
    }

    @Override
    protected void execute(@Nonnull Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        Area area = args.length >= 2 ? Area.get(args[1]) : source.isPlayer() ? Area.highestArea((TNLPlayer) source) : null;
        if (area == null) throw new InvalidUseException(this);
        if (area.delete()) source.sendMessage("%prefix% §7Deleted area§8: §6" + area);
        else source.sendMessage("%prefix% §cFailed to delete area §4" + area);
    }

    @Nonnull
    @Override
    protected List<String> suggest(@Nonnull Invocation invocation) {
        if (invocation.arguments().length != 2) return new ArrayList<>();
        return new ArrayList<>(Area.userAreaNames());
    }

    @Override
    public void usage(@Nonnull Invocation invocation) {
        if (invocation.source().isPlayer()) invocation.source().sendMessage("%prefix% §c/area delete §8(§6Area§8)");
        else invocation.source().sendMessage("%prefix% §c/area delete §8[§6Area§8]");
    }
}
