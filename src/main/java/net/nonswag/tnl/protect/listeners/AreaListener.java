package net.nonswag.tnl.protect.listeners;

import net.nonswag.tnl.protect.api.area.Area;
import net.nonswag.tnl.protect.api.event.AreaEnterEvent;
import net.nonswag.tnl.protect.api.event.AreaLeaveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class AreaListener implements Listener {

    @EventHandler
    public void onAreaEnter(@Nonnull AreaEnterEvent event) {
        if (!event.getArea().getAction().onAction(event.getPlayer(), Area.ActionEvent.Type.ENTER)) {
            if (!event.getPlayer().getPermissionManager().hasPermission("tnl.admin")) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAreaLeave(@Nonnull AreaLeaveEvent event) {
        if (!event.getArea().getAction().onAction(event.getPlayer(), Area.ActionEvent.Type.LEAVE)) {
            if (!event.getPlayer().getPermissionManager().hasPermission("tnl.admin")) event.setCancelled(true);
        }
    }
}
