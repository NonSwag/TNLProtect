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
import net.nonswag.tnl.listener.TNLListener;
import net.nonswag.tnl.listener.api.file.formats.JsonFile;
import net.nonswag.tnl.listener.api.file.helper.FileHelper;
import net.nonswag.tnl.listener.api.logger.Logger;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.listener.types.BlockLocation;
import net.nonswag.tnl.protect.api.event.AreaCreateEvent;
import net.nonswag.tnl.protect.api.event.AreaDeleteEvent;
import net.nonswag.tnl.protect.api.event.AreaSchematicDeleteEvent;
import net.nonswag.tnl.protect.api.event.AreaSchematicLoadEvent;
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

public class Area {

    @Nonnull
    private static final JsonFile configuration = new JsonFile("plugins/Protect", "areas.json");
    @Nonnull
    private static final HashMap<String, Area> areas = new HashMap<>();

    @Nonnull
    private final CuboidRegion region;
    @Nonnull
    private final String name;
    @Nonnull
    private final World world;
    @Nonnull
    private final BlockVector3 pos1;
    @Nonnull
    private final BlockVector3 pos2;
    @Nonnull
    private final Schematic schematic;
    @Nonnull
    private ActionEvent action = new ActionEvent() {
        @Override
        public <T> boolean onAction(@Nonnull TNLPlayer player, @Nonnull Type<T> type, @Nullable T target) {
            if ((player.getGamemode().isCreative() || player.getGamemode().isSpectator()) &&
                    player.getPermissionManager().hasPermission("tnl.admin")) return true;
            else if (ActionEvent.Type.INTERACT.equals(type) && player.getItemInHand().getType().isEdible() &&
                    player.getFoodLevel() < 20) return true;
            else return type.isAllowed();
        }
    };
    private int priority = 0;
    private boolean globalArea = false;

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
    public CuboidRegion getRegion() {
        return region;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public World getWorld() {
        return world;
    }

    @Nonnull
    public BlockVector3 getPos1() {
        return pos1;
    }

    @Nonnull
    public BlockVector3 getPos2() {
        return pos2;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isGlobalArea() {
        return globalArea;
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

    public boolean isInside(@Nonnull BlockLocation location) {
        return isInside(location.getLocation());
    }

    public boolean isInside(@Nonnull TNLPlayer player) {
        return isInside(player.getLocation());
    }

    private void export() {
        JsonObject root = getConfiguration().getJsonElement().getAsJsonObject();
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
        root.add(getName(), area);
    }

    public boolean delete() {
        return delete(false);
    }

    public boolean delete(boolean force) {
        if (!force && isGlobalArea()) return false;
        if (!isGlobalArea() && !getSchematic().delete()) return false;
        else if (new AreaDeleteEvent(this).call()) {
            getConfiguration().getJsonElement().getAsJsonObject().remove(getName());
            getAreas().remove(getName());
            return true;
        } else return false;
    }

    @Nonnull
    public Schematic getSchematic() {
        if (isGlobalArea()) throw new NullPointerException("Not allowed for global areas");
        return schematic;
    }

    @Nonnull
    public ActionEvent getAction() {
        return action;
    }

    @Nonnull
    public Area setAction(@Nonnull ActionEvent action) {
        this.action = action;
        return this;
    }

    @Nonnull
    public List<TNLPlayer> getPlayers() {
        List<TNLPlayer> players = new ArrayList<>();
        for (TNLPlayer all : TNLListener.getInstance().getOnlinePlayers()) {
            if (equals(highestArea(all))) players.add(all);
        }
        return players;
    }

    public class Schematic {

        @Nonnull
        private final File file = new File("plugins/Protect/Schematics/" + getName() + ".schem");

        private Schematic() {
        }

        @Nonnull
        public File getFile() {
            return file;
        }

        public boolean load() {
            if (isGlobalArea()) return false;
            if (!getFile().exists()) return false;
            if (getRegion().getWorld() == null) return false;
            AreaSchematicLoadEvent event = new AreaSchematicLoadEvent(Area.this);
            if (!event.call()) return false;
            try (Clipboard clipboard = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(new FileInputStream(getFile())).read()) {
                EditSession editSession = new EditSession(new EditSessionBuilder(getRegion().getWorld()));
                for (Entity entity : getRegion().getWorld().getEntities(getRegion())) entity.remove();
                Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(getPos1()).copyEntities(true).ignoreAirBlocks(false).build();
                Operations.complete(operation);
                editSession.flushQueue();
                for (AreaSchematicLoadEvent.Success success : event.getSuccessListeners()) {
                    success.onSuccess(Area.this);
                }
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
            try (ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(new FileInputStream(getFile()))) {
                return reader.read();
            } catch (IOException e) {
                Logger.error.println(e);
            }
            return null;
        }

        public boolean delete() {
            if (!file.exists()) return true;
            if (new AreaSchematicDeleteEvent(Area.this).call()) {
                FileHelper.deleteDirectory(file);
                return true;
            } else return false;
        }
    }

    @Nonnull
    public static JsonFile getConfiguration() {
        return configuration;
    }

    @Nonnull
    private static HashMap<String, Area> getAreas() {
        return areas;
    }

    @Nonnull
    public static Collection<Area> areas() {
        return getAreas().values();
    }

    @Nonnull
    public static List<Area> areas(@Nonnull World world) {
        List<Area> areas = new ArrayList<>();
        for (Area area : areas()) if (area.getWorld().equals(world)) areas.add(area);
        return areas;
    }

    @Nonnull
    public static Set<String> names() {
        return getAreas().keySet();
    }

    public static void saveAreas() {
        for (Area area : areas()) area.export();
        getConfiguration().save();
    }

    @Nonnull
    public static List<Area> areas(@Nonnull TNLPlayer player) {
        return areas(player.getLocation());
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
        return highestArea(player.getLocation());
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
        return getAreas().get(name);
    }

    @Nonnull
    public static Area get(@Nonnull World world) {
        Area area = get(world.getName());
        if (area == null) area = Area.create(world);
        return area;
    }

    public static boolean exists(@Nonnull String name) {
        JsonObject root = getConfiguration().getJsonElement().getAsJsonObject();
        return root.has(name) && root.get(name).isJsonObject();
    }

    @Nonnull
    public static Area load(@Nonnull String name) throws NullPointerException {
        if (!exists(name)) throw new NullPointerException("Region not found");
        JsonObject root = getConfiguration().getJsonElement().getAsJsonObject();
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
        return create(world, location1, location2, name).setPriority(priority);
    }

    @Nonnull
    public static List<Area> loadAll() {
        JsonObject root = getConfiguration().getJsonElement().getAsJsonObject();
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
        getAreas().put(area.getName(), area);
        new AreaCreateEvent(area).call();
        return area;
    }

    @Nonnull
    public static Area create(@Nonnull World world) {
        Area area = new Area(world);
        getAreas().put(area.getName(), area);
        return area;
    }

    public abstract static class ActionEvent {

        public boolean onAction(@Nonnull TNLPlayer player, @Nonnull Type<Void> type) {
            return onAction(player, type, null);
        }

        public abstract <T> boolean onAction(@Nonnull TNLPlayer player, @Nonnull Type<T> type, @Nullable T target);

        public static class Type<T> {
            @Nonnull
            public static final Type<Void> ENTER = new Type<>(true);
            @Nonnull
            public static final Type<Void> LEAVE = new Type<>(true);
            @Nonnull
            public static final Type<Block> BREAK = new Type<>();
            @Nonnull
            public static final Type<Block> BUILD = new Type<>();
            @Nonnull
            public static final Type<Block> INTERACT = new Type<>();
            @Nonnull
            public static final Type<org.bukkit.entity.Entity> ENTITY_INTERACT = new Type<>();
            @Nonnull
            public static final Type<org.bukkit.entity.Entity> ATTACK = new Type<>();
            @Nonnull
            public static final Type<org.bukkit.entity.Entity> DAMAGE = new Type<>();

            private final boolean allowed;

            Type() {
                this(false);
            }

            Type(boolean allowed) {
                this.allowed = allowed;
            }

            public boolean isAllowed() {
                return allowed;
            }
        }
    }
}
