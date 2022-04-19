package net.nonswag.tnl.protect.listeners;

import net.nonswag.tnl.protect.api.area.Area;
import net.nonswag.tnl.protect.api.area.Flag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

import javax.annotation.Nonnull;

public class EntityListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDamage(@Nonnull EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Area area = Area.highestArea(event.getEntity().getLocation());
        if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) return;
        if (area.getFlag(Flag.DAMAGE)) return;
        event.getEntity().setFireTicks(0);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFoodLevelChange(@Nonnull FoodLevelChangeEvent event) {
        Area area = Area.highestArea(event.getEntity().getLocation());
        if (event.getItem() != null && !area.getFlag(Flag.HUNGER)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDamage(@Nonnull EntityDamageByEntityEvent event) {
        Area area = Area.highestArea(event.getDamager().getLocation());
        Flag<Boolean> flag = event.getDamager() instanceof Player && event.getEntity() instanceof Player ? Flag.PVP : Flag.PVE;
        if (!area.getFlag(flag)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(@Nonnull PlayerInteractEntityEvent event) {
        Area area = Area.highestArea(event.getRightClicked().getLocation());
        if (!area.getFlag(Flag.ENTITY_INTERACT).test(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onShear(@Nonnull PlayerShearEntityEvent event) {
        Area area = Area.highestArea(event.getEntity().getLocation());
        if (!area.getFlag(Flag.ENTITY_INTERACT).test(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingBreak(@Nonnull HangingBreakByEntityEvent event) {
        Area area = Area.highestArea(event.getEntity().getLocation());
        if (!area.getFlag(Flag.PVE)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArmorStandManipulate(@Nonnull PlayerArmorStandManipulateEvent event) {
        Area area = Area.highestArea(event.getRightClicked().getLocation());
        if (!area.getFlag(Flag.ENTITY_INTERACT).test(event.getPlayer())) event.setCancelled(true);
    }
}
