package net.nonswag.tnl.protect.listeners;

import net.nonswag.tnl.protect.api.area.Flag;
import net.nonswag.tnl.protect.api.event.AreaEnterEvent;
import net.nonswag.tnl.protect.api.event.AreaLeaveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class AreaListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAreaEnter(@Nonnull AreaEnterEvent event) {
        if (!event.getArea().getFlag(Flag.ENTER).test(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAreaLeave(@Nonnull AreaLeaveEvent event) {
        if (!event.getArea().getFlag(Flag.LEAVE).test(event.getPlayer())) event.setCancelled(true);
    }
}
