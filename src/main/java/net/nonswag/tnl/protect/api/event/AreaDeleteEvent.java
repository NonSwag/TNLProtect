package net.nonswag.tnl.protect.api.event;

import net.nonswag.tnl.protect.api.area.Area;

import javax.annotation.Nonnull;

public class AreaDeleteEvent extends AreaEvent {

    public AreaDeleteEvent(@Nonnull Area area) {
        super(area);
    }
}
