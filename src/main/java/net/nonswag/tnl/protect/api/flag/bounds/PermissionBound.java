package net.nonswag.tnl.protect.api.flag.bounds;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.ServerOperator;

import javax.annotation.Nonnull;

public interface PermissionBound extends ClassBound<Permissible> {

    @Nonnull
    PermissionBound AUTOMATIC = ServerOperator::isOp;
}
