package net.nonswag.tnl.protect.api.event;

import lombok.Getter;
import net.nonswag.tnl.listener.api.event.TNLEvent;
import net.nonswag.tnl.protect.api.area.Area;

import javax.annotation.Nonnull;

@Getter
public abstract class AreaEvent extends TNLEvent {

    @Nonnull
    private final Area area;

    protected AreaEvent(@Nonnull Area area) {
        this.area = area;
    }
}
