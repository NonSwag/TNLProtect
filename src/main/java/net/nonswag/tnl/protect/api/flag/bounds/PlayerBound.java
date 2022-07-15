package net.nonswag.tnl.protect.api.flag.bounds;

import net.nonswag.tnl.listener.api.player.TNLPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface PlayerBound extends EntityBound<Player> {

    @Nonnull
    PlayerBound AUTOMATIC = player -> player.permissionManager().isOp() && (player.getGamemode().isCreative() || player.getGamemode().isSpectator());

    boolean test(@Nonnull TNLPlayer player);

    @Override
    default boolean test(@Nonnull Player player) {
        return test(TNLPlayer.cast(player));
    }
}
