package net.nonswag.tnl.protect.api.event;

import net.nonswag.tnl.protect.api.area.Area;

import javax.annotation.Nonnull;

public class AreaCreateEvent extends AreaEvent {

    public AreaCreateEvent(@Nonnull Area area) {
        super(area);
    }

    @Override
    protected boolean denyCancellation() {
        return true;
    }
}
