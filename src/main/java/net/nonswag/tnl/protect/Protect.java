package net.nonswag.tnl.protect;

import net.nonswag.tnl.listener.api.command.CommandManager;
import net.nonswag.tnl.listener.api.event.EventManager;
import net.nonswag.tnl.protect.api.area.Area;
import net.nonswag.tnl.protect.commands.AreaCommand;
import net.nonswag.tnl.protect.completer.AreaCommandTabCompleter;
import net.nonswag.tnl.protect.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Protect extends JavaPlugin {

    @Nullable
    private static Protect instance = null;

    @Override
    public void onEnable() {
        setInstance(this);
        CommandManager commandManager = CommandManager.cast(this);
        EventManager eventManager = EventManager.cast(this);
        commandManager.registerCommand("area", "tnl.protect", new AreaCommand(), new AreaCommandTabCompleter());
        eventManager.registerListener(new AreaListener());
        eventManager.registerListener(new MoveListener());
        eventManager.registerListener(new WorldListener());
        eventManager.registerListener(new DamageListener());
        if (Bukkit.getPluginManager().isPluginEnabled("TNLWorlds")) {
            eventManager.registerListener(new WorldDeleteListener());
        }
        Area.loadAll();
    }

    @Override
    public void onDisable() {
        Area.saveAreas();
    }

    public static void setInstance(@Nonnull Protect instance) {
        Protect.instance = instance;
    }

    @Nonnull
    public static Protect getInstance() {
        assert instance != null;
        return instance;
    }
}
