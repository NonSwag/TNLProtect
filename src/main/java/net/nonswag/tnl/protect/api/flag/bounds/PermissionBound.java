package net.nonswag.tnl.protect.api.flag.bounds;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import javax.annotation.Nonnull;

public interface PermissionBound extends ClassBound<Permissible> {

    @Nonnull
    PermissionBound AUTOMATIC = permissible -> {
        if (!(permissible instanceof Player player)) return permissible.isOp();
        return permissible.isOp() && player.getGameMode().equals(GameMode.CREATIVE);
    };
}
