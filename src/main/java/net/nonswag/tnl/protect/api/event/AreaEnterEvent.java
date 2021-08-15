package net.nonswag.tnl.protect.api.event;

import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.protect.api.area.Area;

import javax.annotation.Nonnull;

public class AreaEnterEvent extends PlayerAreaEvent {

    public AreaEnterEvent(@Nonnull Area area, @Nonnull TNLPlayer player) {
        super(area, player);
    }
}
