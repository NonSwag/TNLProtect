package net.nonswag.tnl.protect.api.event;

import lombok.Getter;
import net.nonswag.tnl.protect.api.area.Area;

import javax.annotation.Nonnull;

@Getter
public class AreaDeleteEvent extends AreaEvent {

    private final boolean forced;

    public AreaDeleteEvent(@Nonnull Area area, boolean forced) {
        super(area);
        this.forced = forced;
    }

    public AreaDeleteEvent(@Nonnull Area area) {
        this(area, false);
    }
}
