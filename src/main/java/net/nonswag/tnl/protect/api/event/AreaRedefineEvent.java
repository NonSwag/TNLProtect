package net.nonswag.tnl.protect.api.event;

import com.sk89q.worldedit.math.BlockVector3;
import lombok.Getter;
import net.nonswag.tnl.protect.api.area.Area;
import org.bukkit.World;

import javax.annotation.Nonnull;

@Getter
public class AreaRedefineEvent extends AreaEvent {

    @Nonnull
    private World world;
    @Nonnull
    private BlockVector3 pos1;
    @Nonnull
    private BlockVector3 pos2;

    public AreaRedefineEvent(@Nonnull Area area, @Nonnull World world, @Nonnull BlockVector3 pos1, @Nonnull BlockVector3 pos2) {
        super(area);
        this.world = world;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    @Nonnull
    public AreaRedefineEvent setWorld(@Nonnull World world) {
        this.world = world;
        return this;
    }

    @Nonnull
    public AreaRedefineEvent setPos1(@Nonnull BlockVector3 pos1) {
        this.pos1 = pos1;
        return this;
    }

    @Nonnull
    public AreaRedefineEvent setPos2(@Nonnull BlockVector3 pos2) {
        this.pos2 = pos2;
        return this;
    }
}
