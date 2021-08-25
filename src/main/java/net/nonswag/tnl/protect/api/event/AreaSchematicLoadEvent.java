package net.nonswag.tnl.protect.api.event;

import net.nonswag.tnl.protect.api.area.Area;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AreaSchematicLoadEvent extends AreaEvent {

    @Nonnull
    private final List<Success> successListeners = new ArrayList<>();

    public AreaSchematicLoadEvent(@Nonnull Area area) {
        super(area);
    }

    @Nonnull
    public List<Success> getSuccessListeners() {
        return successListeners;
    }

    public interface Success {
        void onSuccess(@Nonnull Area area);
    }
}
