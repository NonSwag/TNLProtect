package net.nonswag.tnl.protect.listeners;

import net.nonswag.tnl.listener.events.PlayerDamageBlockEvent;
import net.nonswag.tnl.protect.api.area.Area;
import net.nonswag.tnl.protect.api.area.Flag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import javax.annotation.Nonnull;

public class WorldListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(@Nonnull PlayerDamageBlockEvent event) {
        if (event.getBlockDamageType().isItemAction()) return;
        Area area = Area.highestArea(event.getBlock());
        if (!area.getFlag(Flag.BREAK).test(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(@Nonnull BlockPlaceEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getFlag(Flag.PLACE).test(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(@Nonnull BlockBreakEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getFlag(Flag.BREAK).test(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(@Nonnull org.bukkit.event.player.PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Area area = Area.highestArea(event.getClickedBlock());
        if (!event.getAction().equals(Action.PHYSICAL) && event.getClickedBlock().getType().isInteractable()) {
            if (!area.getFlag(Flag.USE).test(event.getPlayer())) event.setCancelled(true);
        } else if (!area.getFlag(Flag.CROP_TRAMPLE).test(event.getPlayer()) && event.getClickedBlock().getType().equals(Material.FARMLAND)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(@Nonnull BlockPhysicsEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getFlag(Flag.PHYSICS)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(@Nonnull BlockFromToEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getFlag(Flag.PHYSICS)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(@Nonnull BlockGrowEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getFlag(Flag.PHYSICS)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(@Nonnull BlockExplodeEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getFlag(Flag.EXPLOSIONS)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(@Nonnull ExplosionPrimeEvent event) {
        Area area = Area.highestArea(event.getEntity().getLocation());
        if (!area.getFlag(Flag.EXPLOSIONS)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(@Nonnull EntityExplodeEvent event) {
        event.blockList().removeIf(block -> !Area.highestArea(block).getFlag(Flag.EXPLOSIONS));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(@Nonnull BlockRedstoneEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getFlag(Flag.REDSTONE)) event.setNewCurrent(0);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(@Nonnull PlayerBucketFillEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getFlag(Flag.BREAK).test(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(@Nonnull PlayerBucketEmptyEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getFlag(Flag.PLACE).test(event.getPlayer())) event.setCancelled(true);
    }
}
