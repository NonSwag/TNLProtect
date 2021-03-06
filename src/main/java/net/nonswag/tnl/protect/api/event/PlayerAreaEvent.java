package net.nonswag.tnl.protect.api.event;

import lombok.Getter;
import net.nonswag.tnl.listener.api.event.PlayerEvent;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.protect.api.area.Area;

import javax.annotation.Nonnull;

@Getter
public abstract class PlayerAreaEvent extends PlayerEvent {

    @Nonnull
    private final Area area;

    protected PlayerAreaEvent(@Nonnull Area area, @Nonnull TNLPlayer player) {
        super(player);
        this.area = area;
    }
}
