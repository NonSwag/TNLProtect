package net.nonswag.tnl.protect.api.area;

import lombok.Getter;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public record Flag<T>(@Nonnull String name, @Nonnull T defaultValue, @Nonnull Possibilities<T> possibilities) {
    @Nonnull
    private static final HashMap<String, Flag<?>> flags = new HashMap<>();

    @Nonnull
    public static final Flag<PlayerBound> ENTER = new Flag<>("enter", player -> true, Possibilities.PLAYER_BOUND_VALUES);
    @Nonnull
    public static final Flag<PlayerBound> LEAVE = new Flag<>("leave", player -> true, Possibilities.PLAYER_BOUND_VALUES);
    @Nonnull
    public static final Flag<PlayerBound> USE = new Flag<>("use", player -> true, Possibilities.PLAYER_BOUND_VALUES);
    @Nonnull
    public static final Flag<PlayerBound> BREAK = new Flag<>("break", player -> true, Possibilities.PLAYER_BOUND_VALUES);
    @Nonnull
    public static final Flag<PlayerBound> PLACE = new Flag<>("place", player -> true, Possibilities.PLAYER_BOUND_VALUES);
    @Nonnull
    public static final Flag<PlayerBound> CROP_TRAMPLE = new Flag<>("cropTrample", player -> true, Possibilities.PLAYER_BOUND_VALUES);
    @Nonnull
    public static final Flag<PlayerBound> ENTITY_INTERACT = new Flag<>("entityInteract", player -> true, Possibilities.PLAYER_BOUND_VALUES);
    @Nonnull
    public static final Flag<PlayerBound> ARMOR_STAND_MANIPULATE = new Flag<>("armorStandManipulate", player -> true, Possibilities.PLAYER_BOUND_VALUES);
    @Nonnull
    public static final Flag<Boolean> ENTITY_SPAWN = new Flag<>("entitySpawn", true, Possibilities.BOOLEAN_VALUES);
    @Nonnull
    public static final Flag<Boolean> REDSTONE = new Flag<>("redstone", true, Possibilities.BOOLEAN_VALUES);
    @Nonnull
    public static final Flag<Boolean> DAMAGE = new Flag<>("damage", true, Possibilities.BOOLEAN_VALUES);
    @Nonnull
    public static final Flag<Boolean> PVP = new Flag<>("pvp", true, Possibilities.BOOLEAN_VALUES);
    @Nonnull
    public static final Flag<Boolean> PVE = new Flag<>("pve", true, Possibilities.BOOLEAN_VALUES);
    @Nonnull
    public static final Flag<Boolean> HANGING_BREAK = new Flag<>("hangingBreak", true, Possibilities.BOOLEAN_VALUES);
    @Nonnull
    public static final Flag<Boolean> HUNGER = new Flag<>("hunger", true, Possibilities.BOOLEAN_VALUES);
    @Nonnull
    public static final Flag<Boolean> PHYSICS = new Flag<>("physics", true, Possibilities.BOOLEAN_VALUES);
    @Nonnull
    public static final Flag<Boolean> EXPLOSIONS = new Flag<>("explosions", true, Possibilities.BOOLEAN_VALUES);

    public Flag(@Nonnull String name, @Nonnull T defaultValue, @Nonnull Possibilities<T> possibilities) {
        this.name = name.replace(" ", "");
        this.defaultValue = defaultValue;
        this.possibilities = possibilities;
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
        return name();
    }

    @Nullable
    public static <T> Flag<T> valueOf(@Nonnull String name) {
        return (Flag<T>) flags.get(name);
    }

    @Nonnull
    public static <T> Flag<T>[] values() {
        return flags.values().toArray(new Flag[]{});
    }

    @Nonnull
    public static <T> Flag<T>[] values(@Nonnull Class<T> type) {
        return (Flag<T>[]) flags.values().stream().filter(flag -> flag.type().equals(type)).toArray();
    }

    @Nonnull
    public static List<String> names() {
        return new ArrayList<>(flags.keySet());
    }

    public interface PlayerBound {

        @Nonnull
        PlayerBound DEFAULT = player -> player.permissionManager().isOp() && (player.getGamemode().isCreative() || player.getGamemode().isSpectator());

        boolean test(@Nonnull TNLPlayer player);

        default boolean test(@Nonnull Player player) {
            return test(TNLPlayer.cast(player));
        }
    }

    @Getter
    public static class Possibility<T> {

        @Nonnull
        private final String name;
        @Nonnull
        private final T value;

        public Possibility(@Nonnull String name, @Nonnull T value) {
            this.name = name;
            this.value = value;
        }

        public Possibility(@Nonnull T value) {
            this(String.valueOf(value), value);
        }
    }

    public static class Possibilities<T> extends HashMap<String, T> {

        @Nonnull
        public static Possibilities<Boolean> BOOLEAN_VALUES = Possibilities.of(true, false);
        @Nonnull
        public static Possibilities<PlayerBound> PLAYER_BOUND_VALUES = Possibilities.of(
                new Possibility<PlayerBound>("always", player -> true),
                new Possibility<PlayerBound>("never", player -> false),
                new Possibility<>("determine", PlayerBound.DEFAULT)
        );

        public Possibilities(@Nonnull Map map) {
            super(map);
        }

        public Possibilities() {
        }

        @SafeVarargs
        public static <T> Possibilities<T> of(@Nonnull T... possibilities) {
            Possibility<T>[] array = new Possibility[possibilities.length];
            for (int i = 0; i < possibilities.length; i++) array[i] = new Possibility<>(possibilities[i]);
            return of(array);
        }

        @SafeVarargs
        public static <T> Possibilities<T> of(@Nonnull Possibility<T>... possibilities) {
            HashMap<String, T> map = new HashMap<>();
            for (Possibility<T> possibility : possibilities) map.put(possibility.getName(), possibility.getValue());
            return new Possibilities<>(map);
        }
    }
}
