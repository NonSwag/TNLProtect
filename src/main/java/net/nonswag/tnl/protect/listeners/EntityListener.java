package net.nonswag.tnl.protect.listeners;

import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.listener.events.EntityDamageByPlayerEvent;
import net.nonswag.tnl.listener.events.PlayerInteractAtEntityEvent;
import net.nonswag.tnl.protect.api.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

import javax.annotation.Nonnull;

public class EntityListener implements Listener {

    @EventHandler
    public void onDamage(@Nonnull EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        TNLPlayer player = TNLPlayer.cast((Player) event.getEntity());
        Area area = Area.highestArea(event.getEntity().getLocation());
        if (!area.getAction().onAction(player, Area.ActionEvent.Type.DAMAGE, event.getEntity())) {
            if (!event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                if (player.bukkit().getFireTicks() > 0) player.bukkit().setFireTicks(0);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(@Nonnull EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        TNLPlayer player = TNLPlayer.cast((Player) event.getDamager());
        Area area = Area.highestArea(event.getEntity().getLocation());
        if (!area.getAction().onAction(player, Area.ActionEvent.Type.ATTACK, event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(@Nonnull EntityDamageByPlayerEvent event) {
        Area area = Area.highestArea(event.getEntity().getLocation());
        if (!area.getAction().onAction(event.getPlayer(), Area.ActionEvent.Type.ATTACK, event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(@Nonnull PlayerInteractAtEntityEvent event) {
        Area area = Area.highestArea(event.getEntity().getLocation());
        if (!area.getAction().onAction(event.getPlayer(), Area.ActionEvent.Type.ENTITY_INTERACT, event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onShear(@Nonnull PlayerShearEntityEvent event) {
        TNLPlayer player = TNLPlayer.cast(event.getPlayer());
        Area area = Area.highestArea(event.getEntity().getLocation());
        if (!area.getAction().onAction(player, Area.ActionEvent.Type.ENTITY_INTERACT, event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreak(@Nonnull HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) return;
        TNLPlayer player = TNLPlayer.cast((Player) event.getRemover());
        Area area = Area.highestArea(event.getEntity().getLocation());
        if (!area.getAction().onAction(player, Area.ActionEvent.Type.ATTACK, event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onArmorStandManipulate(@Nonnull PlayerArmorStandManipulateEvent event) {
        TNLPlayer player = TNLPlayer.cast(event.getPlayer());
        Area area = Area.highestArea(event.getRightClicked().getLocation());
        if (!area.getAction().onAction(player, Area.ActionEvent.Type.ATTACK, event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityInteract(@Nonnull org.bukkit.event.player.PlayerInteractAtEntityEvent event) {
        TNLPlayer player = TNLPlayer.cast(event.getPlayer());
        Area area = Area.highestArea(event.getRightClicked().getLocation());
        if (!area.getAction().onAction(player, Area.ActionEvent.Type.ATTACK, event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityInteract(@Nonnull PlayerInteractEntityEvent event) {
        TNLPlayer player = TNLPlayer.cast(event.getPlayer());
        Area area = Area.highestArea(event.getRightClicked().getLocation());
        if (!area.getAction().onAction(player, Area.ActionEvent.Type.ATTACK, event.getRightClicked())) {
            event.setCancelled(true);
        }
    }
}
