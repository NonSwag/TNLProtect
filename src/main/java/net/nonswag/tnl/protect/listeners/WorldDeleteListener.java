package net.nonswag.tnl.protect.listeners;

import net.nonswag.tnl.protect.api.area.Area;
import net.nonswag.tnl.world.api.events.WorldDeleteEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class WorldDeleteListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldEvent(@Nonnull WorldDeleteEvent event) {
        if (!Area.get(event.getWorld()).delete(true)) event.setCancelled(true);
    }
}
