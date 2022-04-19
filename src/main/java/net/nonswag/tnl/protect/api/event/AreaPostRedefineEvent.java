package net.nonswag.tnl.protect.api.event;

import com.sk89q.worldedit.math.BlockVector3;
import net.nonswag.tnl.protect.api.area.Area;
import org.bukkit.World;

import javax.annotation.Nonnull;

public class AreaPostRedefineEvent extends AreaRedefineEvent {

    public AreaPostRedefineEvent(@Nonnull Area area, @Nonnull World world, @Nonnull BlockVector3 pos1, @Nonnull BlockVector3 pos2) {
        super(area, world, pos1, pos2);
    }

    public AreaPostRedefineEvent(@Nonnull AreaRedefineEvent event) {
        this(event.getArea(), event.getWorld(), event.getPos1(), event.getPos2());
        super.setCancelled(event.isCancelled());
    }

    @Override
    public void setCancelled(boolean cancelled) {
    }
}
