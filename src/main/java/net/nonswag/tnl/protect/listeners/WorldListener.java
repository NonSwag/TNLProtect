package net.nonswag.tnl.protect.listeners;

import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.listener.events.PlayerDamageBlockEvent;
import net.nonswag.tnl.listener.events.PlayerInteractEvent;
import net.nonswag.tnl.protect.api.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import javax.annotation.Nonnull;

public class WorldListener implements Listener {

    @EventHandler
    public void onWorldEvent(@Nonnull PlayerDamageBlockEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getAction().onAction(event.getPlayer(), Area.ActionEvent.Type.BREAK)) {
            event.setCancelled(true);
            event.setUpdate(true);
        }
    }

    @EventHandler
    public void onWorldEvent(@Nonnull BlockPlaceEvent event) {
        TNLPlayer player = TNLPlayer.cast(event.getPlayer());
        Area area = Area.highestArea(event.getBlock());
        if (!area.getAction().onAction(player, Area.ActionEvent.Type.BUILD)) event.setCancelled(true);
    }

    @EventHandler
    public void onWorldEvent(@Nonnull BlockBreakEvent event) {
        TNLPlayer player = TNLPlayer.cast(event.getPlayer());
        Area area = Area.highestArea(event.getBlock());
        if (!area.getAction().onAction(player, Area.ActionEvent.Type.BREAK)) event.setCancelled(true);
    }

    @EventHandler
    public void onWorldEvent(@Nonnull HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) return;
        TNLPlayer player = TNLPlayer.cast((Player) event.getRemover());
        Area area = Area.highestArea(event.getEntity().getLocation());
        if (!area.getAction().onAction(player, Area.ActionEvent.Type.BREAK)) event.setCancelled(true);
    }

    @EventHandler
    public void onWorldEvent(@Nonnull org.bukkit.event.player.PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        TNLPlayer player = TNLPlayer.cast(event.getPlayer());
        Area area = Area.highestArea(event.getClickedBlock());
        if (!area.getAction().onAction(player, Area.ActionEvent.Type.INTERACT)) {
            area = Area.highestArea(event.getClickedBlock().getRelative(event.getBlockFace()));
            if (!area.getAction().onAction(player, Area.ActionEvent.Type.INTERACT)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onWorldEvent(@Nonnull PlayerInteractEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getAction().onAction(event.getPlayer(), Area.ActionEvent.Type.INTERACT)) event.setCancelled(true);
    }

    @EventHandler
    public void onWorldEvent(@Nonnull PlayerBucketFillEvent event) {
        TNLPlayer player = TNLPlayer.cast(event.getPlayer());
        Area area = Area.highestArea(event.getBlock());
        if (!area.getAction().onAction(player, Area.ActionEvent.Type.BREAK)) event.setCancelled(true);
    }

    @EventHandler
    public void onWorldEvent(@Nonnull PlayerBucketEmptyEvent event) {
        TNLPlayer player = TNLPlayer.cast(event.getPlayer());
        Area area = Area.highestArea(event.getBlock());
        if (!area.getAction().onAction(player, Area.ActionEvent.Type.BUILD)) event.setCancelled(true);
    }
}
