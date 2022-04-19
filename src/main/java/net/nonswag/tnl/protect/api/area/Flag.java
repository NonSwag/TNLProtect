package net.nonswag.tnl.protect.api.area;

import net.nonswag.tnl.listener.api.player.TNLPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

public record Flag<T>(@Nonnull String name, @Nonnull T defaultValue) {
    @Nonnull
    private static final HashMap<String, Flag<?>> flags = new HashMap<>();

    @Nonnull
    public static final Flag<PlayerBound> ENTER = new Flag<>("enter", player -> true);
    @Nonnull
    public static final Flag<PlayerBound> LEAVE = new Flag<>("leave", player -> true);
    @Nonnull
    public static final Flag<PlayerBound> USE = new Flag<>("use", PlayerBound.DEFAULT);
    @Nonnull
    public static final Flag<PlayerBound> BREAK = new Flag<>("break", PlayerBound.DEFAULT);
    @Nonnull
    public static final Flag<PlayerBound> PLACE = new Flag<>("place", PlayerBound.DEFAULT);
    @Nonnull
    public static final Flag<PlayerBound> CROP_TRAMPLE = new Flag<>("cropTrample", PlayerBound.DEFAULT);
    @Nonnull
    public static final Flag<PlayerBound> ENTITY_INTERACT = new Flag<>("entityInteract", PlayerBound.DEFAULT);
    @Nonnull
    public static final Flag<Boolean> REDSTONE = new Flag<>("redstone", false);
    @Nonnull
    public static final Flag<Boolean> DAMAGE = new Flag<>("damage", false);
    @Nonnull
    public static final Flag<Boolean> PVP = new Flag<>("pvp", false);
    @Nonnull
    public static final Flag<Boolean> PVE = new Flag<>("pve", false);
    @Nonnull
    public static final Flag<Boolean> HUNGER = new Flag<>("hunger", false);
    @Nonnull
    public static final Flag<Boolean> PHYSICS = new Flag<>("physics", false);
    @Nonnull
    public static final Flag<Boolean> EXPLOSIONS = new Flag<>("explosions", false);

    public Flag(@Nonnull String name, @Nonnull T defaultValue) {
        this.name = name.replace(" ", "");
        this.defaultValue = defaultValue;
        flags.put(name(), this);
    }

    @Nonnull
    public Class<T> type() {
        return (Class<T>) defaultValue().getClass();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flag<?> flag = (Flag<?>) o;
        return name.equals(flag.name) && defaultValue.equals(flag.defaultValue);
    }

    @Override
    public String toString() {
        return "Flag{" +
                "name='" + name + '\'' +
                ", defaultValue=" + defaultValue +
                '}';
    }

    @Nullable
    public static Flag<?> valueOf(@Nonnull String name) {
        return flags.get(name);
    }

    @Nonnull
    public static Flag<?>[] values() {
        return flags.values().toArray(new Flag[]{});
    }

    @Nonnull
    public static <T> Flag<T>[] values(@Nonnull Class<T> type) {
        return (Flag<T>[]) flags.values().stream().filter(flag -> flag.type().equals(type)).toArray();
    }

    public interface PlayerBound {

        @Nonnull
        PlayerBound DEFAULT = player -> player.permissionManager().isOp() && (player.getGamemode().isCreative() || player.getGamemode().isSpectator());

        boolean test(@Nonnull TNLPlayer player);

        default boolean test(@Nonnull Player player) {
            return test(TNLPlayer.cast(player));
        }
    }
}
