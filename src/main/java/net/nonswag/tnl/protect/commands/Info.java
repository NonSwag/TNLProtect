package net.nonswag.tnl.protect.commands;

import com.sk89q.worldedit.math.BlockVector3;
import net.nonswag.tnl.core.api.command.CommandSource;
import net.nonswag.tnl.core.api.command.Invocation;
import net.nonswag.tnl.listener.api.command.exceptions.InvalidUseException;
import net.nonswag.tnl.listener.api.command.simple.SubCommand;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.protect.api.area.Area;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

class Info extends SubCommand {

    Info() {
        super("info");
    }

    @Override
    protected void execute(@Nonnull Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        Area area = args.length >= 2 ? Area.get(args[1]) : source.isPlayer() ? Area.highestArea((TNLPlayer) source) : null;
        if (area == null) throw new InvalidUseException(this);
        source.sendMessage("%prefix% §7Area§8: §6" + area);
        if (!area.isGlobalArea()) {
            BlockVector3 p1 = area.getPos1();
            BlockVector3 p2 = area.getPos2();
            source.sendMessage("%prefix% §7World§8: §6" + area.getWorld().getName());
            source.sendMessage("%prefix% §7Pos1§8: §6" + p1.getX() + "§8, §6" + p1.getY() + "§8, §6" + p1.getZ());
            source.sendMessage("%prefix% §7Pos2§8: §6" + p2.getX() + "§8, §6" + p2.getY() + "§8, §6" + p2.getZ());
        } else source.sendMessage("%prefix% §7Bounds§8: §6Global");
        source.sendMessage("%prefix% §7Priority§8: §6" + area.getPriority());
    }

    @Override
    public void usage(@Nonnull Invocation invocation) {
        if (invocation.source().isPlayer()) invocation.source().sendMessage("%prefix% §c/area info §8(§6Area§8)");
        else invocation.source().sendMessage("%prefix% §c/area info §8[§6Area§8]");
    }

    @Nonnull
    @Override
    protected List<String> suggest(@Nonnull Invocation invocation) {
        if (invocation.arguments().length != 2) return new ArrayList<>();
        return new ArrayList<>(Area.names());
    }
}
