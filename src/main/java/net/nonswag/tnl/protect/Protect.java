package net.nonswag.tnl.protect;

import net.nonswag.tnl.listener.api.plugin.TNLPlugin;
import net.nonswag.tnl.protect.api.area.Area;
import net.nonswag.tnl.protect.commands.AreaCommand;
import net.nonswag.tnl.protect.listeners.*;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Protect extends TNLPlugin {

    @Nullable
    private static Protect instance = null;

    @Override
    public void enable() {
        instance = this;
        getCommandManager().registerCommand(new AreaCommand());
        getEventManager().registerListener(new AreaListener());
        getEventManager().registerListener(new MoveListener());
        getEventManager().registerListener(new WorldListener());
        getEventManager().registerListener(new EntityListener());
        if (Bukkit.getPluginManager().isPluginEnabled("TNLWorlds")) {
            getEventManager().registerListener(new WorldDeleteListener());
        }
        sync(Area::loadAll, 1);
    }

    @Override
    public void disable() {
        Area.saveAreas();
    }

    @Nonnull
    public static Protect getInstance() {
        assert instance != null;
        return instance;
    }
}
