package net.nonswag.tnl.protect.commands;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.nonswag.tnl.core.api.command.Invocation;
import net.nonswag.tnl.listener.api.command.exceptions.InvalidUseException;
import net.nonswag.tnl.listener.api.command.simple.PlayerSubCommand;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.protect.api.area.Area;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

class Select extends PlayerSubCommand {

    Select() {
        super("select");
    }

    @Override
    protected void execute(@Nonnull Invocation invocation) {
        TNLPlayer player = (TNLPlayer) invocation.source();
        String[] args = invocation.arguments();
        BukkitPlayer worldEdit = WorldEditPlugin.getInstance().wrapPlayer(player.bukkit());
        Area area = args.length >= 2 ? Area.get(args[1]) : Area.highestArea(player);
        if (area == null) throw new InvalidUseException(this);
        if (!area.isGlobalArea()) {
            worldEdit.setSelection(area.getRegion());
            player.messenger().sendMessage("%prefix% §7Selected Area§8: §6" + area);
        } else player.messenger().sendMessage("%prefix% §cCan't select a global area");
    }

    @Nonnull
    @Override
    protected List<String> suggest(@Nonnull Invocation invocation) {
        if (invocation.arguments().length != 2) return new ArrayList<>();
        return new ArrayList<>(Area.userAreaNames());
    }

    @Override
    public void usage(@Nonnull Invocation invocation) {
        invocation.source().sendMessage("%prefix% §c/area select §8(§6Area§8)");
    }
}
