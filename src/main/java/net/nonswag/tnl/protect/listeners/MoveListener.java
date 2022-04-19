package net.nonswag.tnl.protect.listeners;

import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.protect.api.area.Area;
import net.nonswag.tnl.protect.api.event.AreaEnterEvent;
import net.nonswag.tnl.protect.api.event.AreaLeaveEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import javax.annotation.Nonnull;

public class MoveListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMove(@Nonnull PlayerMoveEvent event) {
        for (Area area : Area.areas()) {
            Location location = event.getTo() == null ? event.getPlayer().getLocation() : event.getTo();
            if (!area.isInside(event.getFrom()) && area.isInside(location)) {
                event.setCancelled(!new AreaEnterEvent(area, TNLPlayer.cast(event.getPlayer())).call());
            } else if (area.isInside(event.getFrom()) && !area.isInside(location)) {
                event.setCancelled(!new AreaLeaveEvent(area, TNLPlayer.cast(event.getPlayer())).call());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onTeleport(@Nonnull PlayerTeleportEvent event) {
        for (Area area : Area.areas()) {
            Location location = event.getTo() == null ? event.getPlayer().getLocation() : event.getTo();
            if (!area.isInside(event.getFrom()) && area.isInside(location)) {
                event.setCancelled(!new AreaEnterEvent(area, TNLPlayer.cast(event.getPlayer())).call());
            } else if (area.isInside(event.getFrom()) && !area.isInside(location)) {
                event.setCancelled(!new AreaLeaveEvent(area, TNLPlayer.cast(event.getPlayer())).call());
            }
        }
    }
}
