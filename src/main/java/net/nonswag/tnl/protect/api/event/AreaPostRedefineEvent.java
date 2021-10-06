package net.nonswag.tnl.protect.api.event;

import javax.annotation.Nonnull;

public class AreaPostRedefineEvent extends AreaEvent {

    @Nonnull
    private final AreaRedefineEvent redefineEvent;

    public AreaPostRedefineEvent(@Nonnull AreaRedefineEvent redefineEvent) {
        super(redefineEvent.getArea());
        this.redefineEvent = redefineEvent;
    }

    @Nonnull
    public AreaRedefineEvent getRedefineEvent() {
        return redefineEvent;
    }

    @Override
    public boolean isCancelled() {
        return getRedefineEvent().isCancelled();
    }

    @Override
    protected boolean denyCancellation() {
        return true;
    }
}
