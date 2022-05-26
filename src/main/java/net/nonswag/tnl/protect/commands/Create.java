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

import javax.annotation.Nonnull;

class Create extends PlayerSubCommand {

    Create() {
        super("create");
    }

    @Override
    protected void execute(@Nonnull Invocation invocation) {
        TNLPlayer player = (TNLPlayer) invocation.source();
        String[] args = invocation.arguments();
        if (args.length < 2) throw new InvalidUseException(this);
        if (Area.get(args[1]) == null) {
            BukkitPlayer worldEdit = WorldEditPlugin.getInstance().wrapPlayer(player.bukkit());
            try {
                Region selection = worldEdit.getSelection();
                BlockVector3 pos1 = selection.getMinimumPoint();
                BlockVector3 pos2 = selection.getMaximumPoint();
                Area.create(player.worldManager().getWorld(), pos1, pos2, args[1]);
                player.messenger().sendMessage("%prefix% §7Created area§8: §6" + args[1]);
            } catch (IncompleteRegionException ignored) {
                player.messenger().sendMessage("%prefix% §cSelect a §8(§4WorldEdit§8)§c region first");
            }
        } else player.messenger().sendMessage("%prefix% §cAn area named §4" + args[1] + "§c does already exist");
    }

    @Override
    public void usage(@Nonnull Invocation invocation) {
        invocation.source().sendMessage("%prefix% §c/area create §8[§6Name§8]");
    }
}
