package net.nonswag.tnl.protect.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import net.nonswag.tnl.core.api.command.Invocation;
import net.nonswag.tnl.listener.api.command.exceptions.InvalidUseException;
import net.nonswag.tnl.listener.api.command.simple.PlayerSubCommand;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.protect.api.area.Area;
import org.bukkit.Bukkit;
import org.bukkit.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

class Redefine extends PlayerSubCommand {

    Redefine() {
        super("redefine");
    }

    @Override
    protected void execute(@Nonnull Invocation invocation) {
        TNLPlayer player = (TNLPlayer) invocation.source();
        String[] args = invocation.arguments();
        Area area = args.length >= 2 ? Area.get(args[1]) : Area.highestArea(player);
        if (area == null) throw new InvalidUseException(this);
        if (!area.isGlobalArea()) {
            BukkitPlayer worldEdit = WorldEditPlugin.getInstance().wrapPlayer(player.bukkit());
            try {
                Region selection = worldEdit.getSelection();
                if (selection.getWorld() == null) throw new IncompleteRegionException();
                BlockVector3 pos1 = selection.getMinimumPoint();
                BlockVector3 pos2 = selection.getMaximumPoint();
                String name = selection.getWorld().getName();
                World world = Bukkit.getWorld(name);
                if (world != null && area.redefine(world, pos1, pos2)) {
                    player.messenger().sendMessage("%prefix% §7Redefined area§8: §6" + area);
                } else player.messenger().sendMessage("%prefix% §cFailed to redefine area §4" + area);
            } catch (IncompleteRegionException ignored) {
                player.messenger().sendMessage("%prefix% §cSelect a §8(§4WorldEdit§8)§c region first");
            }
        } else player.messenger().sendMessage("%prefix% §cCan't redefine a global area");
    }

    @Nonnull
    @Override
    protected List<String> suggest(@Nonnull Invocation invocation) {
        if (invocation.arguments().length != 2) return new ArrayList<>();
        return new ArrayList<>(Area.userAreaNames());
    }

    @Override
    public void usage(@Nonnull Invocation invocation) {
        if (invocation.source().isPlayer()) invocation.source().sendMessage("%prefix% §c/area redefine §8(§6Area§8)");
        else invocation.source().sendMessage("%prefix% §c/area redefine §8[§6Area§8]");
    }
}
