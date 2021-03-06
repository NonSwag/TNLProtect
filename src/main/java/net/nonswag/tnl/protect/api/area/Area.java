package net.nonswag.tnl.protect.api.area;

import com.fastasyncworldedit.core.util.EditSessionBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import lombok.AccessLevel;
import lombok.Getter;
import net.nonswag.tnl.core.api.file.formats.JsonFile;
import net.nonswag.tnl.core.api.file.helper.FileHelper;
import net.nonswag.tnl.core.api.logger.Logger;
import net.nonswag.tnl.core.api.message.Placeholder;
import net.nonswag.tnl.listener.Listener;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.protect.api.event.*;
import net.nonswag.tnl.protect.api.flag.Flag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Getter
public class Area {

    @Nonnull
    private static final JsonFile configuration = new JsonFile("plugins/Protect", "areas.json");
    @Nonnull
    private static final HashMap<String, Area> areas = new HashMap<>();

    static {
        Placeholder.Registry.register(new Placeholder("area", player -> Area.highestArea((TNLPlayer) player)));
    }

    @Nonnull
    private CuboidRegion region;
    @Nonnull
    private final String name;
    @Nonnull
    private final World world;
    @Nonnull
    private BlockVector3 pos1;
    @Nonnull
    private BlockVector3 pos2;
    @Nonnull
    private final Schematic schematic;
    private int priority = 0;
    private boolean globalArea = false;
    @Nonnull
    @Getter(AccessLevel.NONE)
    private final HashMap<Flag<?>, Object> flags = new HashMap<>();

    private Area(@Nonnull World world, @Nonnull BlockVector3 pos1, @Nonnull BlockVector3 pos2, @Nonnull String name) throws IllegalArgumentException {
        this.name = name;
        this.world = world;
        if (pos1.getY() <= 0) this.pos1 = pos1.withY(1);
        else this.pos1 = pos1;
        if (pos2.getY() <= 0) this.pos2 = pos2.withY(1);
        else this.pos2 = pos2;
        this.region = new CuboidRegion(new BukkitWorld(world), getPos1(), getPos2());
        this.schematic = new Schematic();
    }

    private Area(@Nonnull World world) {
        this(world, BlockVector3.at(30000000, 265, 30000000), BlockVector3.at(-30000000, 0, -30000000), world.getName());
        setGlobalArea(true).setPriority(-1);
    }

    @Nonnull
    private Area setGlobalArea(boolean globalArea) {
        this.globalArea = globalArea;
        return this;
    }

    @Nonnull
    public Area setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    @Nonnull
    private Area setRegion(@Nonnull CuboidRegion region) {
        this.region = region;
        return this;
    }

    @Nonnull
    private Area setPos1(@Nonnull BlockVector3 pos1) {
        this.pos1 = pos1;
        return this;
    }

    @Nonnull
    private Area setPos2(@Nonnull BlockVector3 pos2) {
        this.pos2 = pos2;
        return this;
    }

    public boolean isInside(@Nonnull Block block) {
        if (isGlobalArea()) return getWorld().equals(block.getWorld());
        return (block.getX() >= getPos1().getX() && block.getY() >= getPos1().getY() &&
                block.getZ() >= getPos1().getZ() && block.getX() <= getPos2().getX() &&
                block.getY() <= getPos2().getY() && block.getZ() <= getPos2().getZ() &&
                getWorld().equals(block.getWorld()));
    }

    public boolean isInside(@Nonnull Location location) {
        return isInside(location.getBlock());
    }

    public boolean isInside(@Nonnull TNLPlayer player) {
        return isInside(player.worldManager().getLocation());
    }

    private void export() {
        JsonObject root = configuration.getJsonElement().getAsJsonObject();
        JsonObject area = new JsonObject();
        area.addProperty("world", getWorld().getName());
        area.addProperty("priority", getPriority());
        if (!isGlobalArea()) {
            JsonObject pos1 = new JsonObject();
            JsonObject pos2 = new JsonObject();
            pos1.addProperty("x", getPos1().getX());
            pos1.addProperty("y", getPos1().getY());
            pos1.addProperty("z", getPos1().getZ());
            pos2.addProperty("x", getPos2().getX());
            pos2.addProperty("y", getPos2().getY());
            pos2.addProperty("z", getPos2().getZ());
            area.add("pos1", pos1);
            area.add("pos2", pos2);
        }
        JsonObject flags = new JsonObject();
        this.flags.forEach((flag, value) -> {
            for (String s : flag.possibilities().keySet()) {
                if (!flag.possibilities().get(s).equals(value)) continue;
                flags.addProperty(flag.name(), s);
                return;
            }
        });
        area.add("flags", flags);
        root.add(getName(), area);
    }

    public boolean delete() {
        return delete(false);
    }

    public boolean delete(boolean force) {
        if (!force && isGlobalArea()) return false;
        if (!isGlobalArea() && !getSchematic().delete()) return false;
        else if (!new AreaDeleteEvent(this, force).call()) return false;
        configuration.getJsonElement().getAsJsonObject().remove(getName());
        areas.remove(getName());
        return true;
    }

    @Nonnull
    public Schematic getSchematic() {
        if (isGlobalArea()) throw new UnsupportedOperationException("Not allowed for global areas");
        return schematic;
    }

    @Nonnull
    public List<TNLPlayer> getPlayers() {
        List<TNLPlayer> players = new ArrayList<>();
        for (TNLPlayer all : Listener.getOnlinePlayers()) if (equals(highestArea(all))) players.add(all);
        return players;
    }

    public boolean redefine(@Nonnull World world, @Nonnull BlockVector3 pos1, @Nonnull BlockVector3 pos2) {
        if (isGlobalArea()) return false;
        AreaRedefineEvent redefineEvent = new AreaRedefineEvent(this, world, pos1, pos2);
        if (!redefineEvent.call()) return false;
        world = redefineEvent.getWorld();
        pos1 = redefineEvent.getPos1();
        pos2 = redefineEvent.getPos2();
        if (pos1.getY() <= 0) setPos1(pos1.withY(1));
        else setPos1(pos1);
        if (pos2.getY() <= 0) setPos2(pos2.withY(1));
        else setPos2(pos2);
        setRegion(new CuboidRegion(new BukkitWorld(world), getPos1(), getPos2()));
        new AreaPostRedefineEvent(redefineEvent).call();
        return true;
    }

    public boolean isTooBig() {
        return isGlobalArea() || getRegion().size() >= 10000000;
    }

    @Nonnull
    public <T> T getFlag(@Nonnull Flag<T> flag) {
        return (T) flags.getOrDefault(flag, flag.defaultValue());
    }

    public <T> boolean setFlag(@Nonnull Flag<T> flag, @Nonnull T value) {
        if (getFlag(flag).equals(value)) return false;
        flags.put(flag, value);
        return true;
    }

    public boolean hasFlag(@Nonnull Flag<?> flag) {
        return flags.containsKey(flag);
    }

    public boolean unsetFlag(@Nonnull Flag<?> flag) {
        if (!hasFlag(flag)) return false;
        flags.remove(flag);
        return true;
    }

    @Override
    public String toString() {
        return getName();
    }

    public final class Schematic {

        @Getter
        @Nonnull
        private final File file = new File("plugins/Protect/Schematics/" + Area.this + ".schem");

        private Schematic() {
        }

        public boolean load() {
            if (isGlobalArea() || !getFile().exists()) return false;
            if (getRegion().getWorld() == null) return false;
            AreaSchematicLoadEvent event = new AreaSchematicLoadEvent(Area.this);
            if (!event.call()) return false;
            try (Clipboard clipboard = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(new FileInputStream(getFile())).read()) {
                EditSession editSession = new EditSession(new EditSessionBuilder(getRegion().getWorld()));
                for (Entity entity : getRegion().getWorld().getEntities(getRegion())) entity.remove();
                Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(getPos1()).copyEntities(true).ignoreAirBlocks(false).build();
                Operations.complete(operation);
                editSession.flushQueue();
                for (AreaSchematicLoadEvent.Success s : event.getSuccessListeners()) s.onSuccess(Area.this);
                return true;
            } catch (IOException e) {
                Logger.error.println(e);
            }
            return false;
        }

        public boolean save() {
            if (isGlobalArea()) return false;
            BlockArrayClipboard clipboard = new BlockArrayClipboard(getRegion());
            com.sk89q.worldedit.world.World world = getRegion().getWorld();
            if (world == null) return false;
            EditSession editSession = new EditSession(new EditSessionBuilder(world));
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, getRegion(), clipboard, getPos1());
            forwardExtentCopy.setCopyingEntities(true);
            Operations.complete(forwardExtentCopy);
            try {
                FileHelper.create(getFile());
                try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(getFile()))) {
                    writer.write(clipboard);
                    return true;
                }
            } catch (IOException e) {
                Logger.error.println(e);
            }
            return false;
        }

        @Nullable
        public Clipboard getSchematic() {
            if (isGlobalArea() || !getFile().exists()) return null;
            try (ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(new FileInputStream(getFile()))) {
                return reader.read();
            } catch (IOException e) {
                Logger.error.println(e);
                return null;
            }
        }

        public boolean delete() {
            if (isGlobalArea() || !getFile().exists()) return false;
            if (!new AreaSchematicDeleteEvent(Area.this).call()) return false;
            FileHelper.delete(file);
            return true;
        }
    }

    @Nonnull
    public static Collection<Area> areas() {
        return areas.values();
    }

    @Nonnull
    public static List<Area> areas(@Nonnull World world) {
        List<Area> areas = new ArrayList<>();
        for (Area area : areas()) if (area.getWorld().equals(world)) areas.add(area);
        return areas;
    }

    @Nonnull
    public static Set<String> names() {
        return areas.keySet();
    }

    public static void saveAreas() {
        areas().forEach(Area::export);
        configuration.save();
    }

    @Nonnull
    public static List<Area> areas(@Nonnull TNLPlayer player) {
        return areas(player.worldManager().getLocation());
    }

    @Nonnull
    public static List<Area> areas(@Nonnull Location location) {
        return areas(location.getBlock());
    }

    @Nonnull
    public static List<Area> areas(@Nonnull Block block) {
        List<Area> areas = new ArrayList<>();
        for (Area area : areas(block.getWorld())) if (area.isInside(block)) areas.add(area);
        return areas;
    }

    @Nonnull
    public static List<Area> userAreas() {
        List<Area> areas = new ArrayList<>();
        for (Area area : areas()) if (!area.isGlobalArea()) areas.add(area);
        return areas;
    }

    @Nonnull
    public static List<String> userAreaNames() {
        List<String> areas = new ArrayList<>();
        for (Area area : userAreas()) areas.add(area.getName());
        return areas;
    }

    @Nonnull
    public static Area highestArea(@Nonnull TNLPlayer player) {
        return highestArea(player.worldManager().getLocation());
    }

    @Nonnull
    public static Area highestArea(@Nonnull Location location) {
        return highestArea(location.getBlock());
    }

    @Nonnull
    public static Area highestArea(@Nonnull Block block) {
        Area highest = null;
        for (Area area : areas(block)) {
            if (highest == null || highest.getPriority() < area.getPriority()) highest = area;
        }
        return highest == null ? get(block.getWorld()) : highest;
    }

    @Nullable
    public static Area get(@Nonnull String name) {
        return areas.get(name);
    }

    @Nonnull
    public static Area get(@Nonnull World world) {
        Area area = get(world.getName());
        return area == null ? Area.create(world) : area;
    }

    public static boolean exists(@Nonnull String name) {
        JsonObject root = configuration.getJsonElement().getAsJsonObject();
        return root.has(name) && root.get(name).isJsonObject();
    }

    @Nonnull
    public static Area load(@Nonnull String name) throws NullPointerException {
        if (!exists(name)) throw new NullPointerException("Region not found");
        JsonObject root = configuration.getJsonElement().getAsJsonObject();
        JsonObject area = root.getAsJsonObject(name);
        if (!area.has("world")) throw new NullPointerException("No world defined");
        if (!area.has("priority")) throw new NullPointerException("No priority defined");
        World world = Bukkit.getWorld(area.get("world").getAsString());
        int priority = area.get("priority").getAsInt();
        if (world == null) throw new NullPointerException("World does not exist");
        if (world.getName().equals(name)) return create(world).setPriority(priority);
        if (!area.has("pos1") || !area.get("pos1").isJsonObject()) throw new NullPointerException("No position (1)");
        if (!area.has("pos2") || !area.get("pos2").isJsonObject()) throw new NullPointerException("No position (2)");
        JsonObject pos1 = area.getAsJsonObject("pos1");
        JsonObject pos2 = area.getAsJsonObject("pos2");
        if (!pos1.has("x") || !pos1.has("y") || !pos1.has("z")) throw new NullPointerException("Invalid position (1)");
        if (!pos2.has("x") || !pos2.has("y") || !pos2.has("z")) throw new NullPointerException("Invalid position (2)");
        BlockVector3 location1 = BlockVector3.at(pos1.get("x").getAsInt(), pos1.get("y").getAsInt(), pos1.get("z").getAsInt());
        BlockVector3 location2 = BlockVector3.at(pos2.get("x").getAsInt(), pos2.get("y").getAsInt(), pos2.get("z").getAsInt());
        Area created = create(world, location1, location2, name).setPriority(priority);
        if (!area.has("flags") || !area.get("flags").isJsonObject()) return created;
        JsonObject flags = area.getAsJsonObject("flags");
        for (Map.Entry<String, JsonElement> entry : flags.entrySet()) {
            for (Flag<Object> flag : Flag.values()) {
                if (!entry.getKey().equals(flag.name())) continue;
                Object value = flag.possibilities().get(entry.getValue().getAsString());
                if (value != null) created.setFlag(flag, value);
            }
        }
        return created;
    }

    @Nonnull
    public static List<Area> loadAll() {
        JsonObject root = configuration.getJsonElement().getAsJsonObject();
        List<Area> areas = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
            try {
                areas.add(load(entry.getKey()));
            } catch (Exception e) {
                Logger.error.println("Invalid area <'" + entry.getKey() + "'>", e.getMessage());
            }
        }
        return areas;
    }

    @Nonnull
    public static Area create(@Nonnull World world, @Nonnull BlockVector3 pos1, @Nonnull BlockVector3 pos2, @Nonnull String name) {
        Area area = new Area(world, pos1, pos2, name);
        areas.put(area.getName(), area);
        new AreaCreateEvent(area).call();
        return area;
    }

    @Nonnull
    private static Area create(@Nonnull World world) {
        Area area = new Area(world);
        areas.put(area.getName(), area);
        return area;
    }
}
