package net.nonswag.tnl.protect.api.event;

import net.nonswag.tnl.protect.api.area.Area;

import javax.annotation.Nonnull;

public class AreaSchematicDeleteEvent extends AreaEvent {

    public AreaSchematicDeleteEvent(@Nonnull Area area) {
        super(area);
    }
}
