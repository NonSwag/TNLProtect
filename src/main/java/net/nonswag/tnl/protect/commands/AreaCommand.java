package net.nonswag.tnl.protect.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import net.nonswag.tnl.listener.api.message.Message;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.protect.api.area.Area;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Set;

public class AreaCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (sender instanceof Player) {
            TNLPlayer player = TNLPlayer.cast((Player) sender);
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (args.length >= 2) {
                        if (Area.get(args[1]) == null) {
                            BukkitPlayer worldEdit = WorldEditPlugin.getInstance().wrapPlayer(player.getBukkitPlayer());
                            try {
                                Region selection = worldEdit.getSelection();
                                BlockVector3 pos1 = selection.getMinimumPoint();
                                BlockVector3 pos2 = selection.getMaximumPoint();
                                Area.create(player.getWorld(), pos1, pos2, args[1]);
                                player.sendMessage("%prefix% §7Created Area§8: §6" + args[1]);
                            } catch (IncompleteRegionException ignored) {
                                player.sendMessage("%prefix% §cSelect a §8(§4WorldEdit§8)§c region first");
                            }
                        } else player.sendMessage("%prefix% §cA Area named §4" + args[1] + "§c does already exist");
                    } else player.sendMessage("%prefix% §c/area create §8[§6Name§8]");
                } else if (args[0].equalsIgnoreCase("delete")) {
                    Area area;
                    if (args.length >= 2) area = Area.get(args[1]);
                    else area = Area.highestArea(player);
                    if (area != null) {
                        if (area.delete()) player.sendMessage("%prefix% §7Deleted Area§8: §6" + area.getName());
                        else player.sendMessage("%prefix% §cFailed to delete Area §4" + area.getName());
                    } else player.sendMessage("%prefix% §c/area delete §8[§6Area§8]");
                } else if (args[0].equalsIgnoreCase("priority")) {
                    if (args.length >= 2) {
                        Area area = Area.get(args[1]);
                        if (area != null) {
                            if (args.length >= 3) {
                                try {
                                    int priority = Integer.parseInt(args[2]);
                                    area.setPriority(priority);
                                    player.sendMessage("%prefix% §aChanged Priority of §6" + area.getName() + "§a to §6" + priority);
                                } catch (NumberFormatException ignored) {
                                    player.sendMessage("%prefix% §c/area priority " + area.getName() + " §8[§6Priority§8]");
                                }
                            } else player.sendMessage("%prefix% §7Priority§8: §6" + area.getPriority());
                        } else player.sendMessage("%prefix% §c/area priority §8[§6Area§8] §8(§6Priority§8)");
                    } else player.sendMessage("%prefix% §c/area priority §8[§6Area§8] §8(§6Priority§8)");
                } else if (args[0].equalsIgnoreCase("schematic")) {
                    if (args.length >= 2) {
                        Area area;
                        if (args.length >= 3) area = Area.get(args[2]);
                        else area = Area.highestArea(player);
                        if (area != null && area.isGlobalArea()) {
                            player.sendMessage("%prefix% §cCan't use a global area");
                        } else if (area == null) {
                            player.sendMessage("%prefix% §c/area schematic " + args[1] + " §8[§6Area§8]");
                        } else {
                            if (args[1].equalsIgnoreCase("load")) {
                                if (area.getSchematic().load()) {
                                    player.sendMessage("%prefix% §7Loaded Schematic§8: §6" + area.getName());
                                } else {
                                    player.sendMessage("%prefix% §cFailed to load Schematic §4" + area.getName());
                                }
                            } else if (args[1].equalsIgnoreCase("delete")) {
                                if (area.getSchematic().delete()) {
                                    player.sendMessage("%prefix% §7Deleted Schematic§8: §6" + area.getName());
                                } else {
                                    player.sendMessage("%prefix% §cFailed to delete Schematic §4" + area.getName());
                                }
                            } else if (args[1].equalsIgnoreCase("save")) {
                                if (area.getSchematic().save()) {
                                    player.sendMessage("%prefix% §7Saved Schematic§8: §6" + area.getName());
                                } else {
                                    player.sendMessage("%prefix% §cFailed to save Schematic §4" + area.getName());
                                }
                            } else player.sendMessage("%prefix% §c/area schematic §8[§6Option§8] §8[§6Area§8]");
                        }
                    } else player.sendMessage("%prefix% §c/area schematic §8[§6Option§8] §8(§6Area§8)");
                } else if (args[0].equalsIgnoreCase("list")) {
                    Set<String> names = Area.names();
                    player.sendMessage("%prefix% §7Areas §8(§a" + names.size() + "§8): §6" + String.join("§8, §6", names));
                } else if (args[0].equalsIgnoreCase("select")) {
                    BukkitPlayer worldEdit = WorldEditPlugin.getInstance().wrapPlayer(player.getBukkitPlayer());
                    Area area;
                    if (args.length >= 2) area = Area.get(args[1]);
                    else area = Area.highestArea(player);
                    if (area != null) {
                        if (area.isGlobalArea()) player.sendMessage("%prefix% §cCan't select a global area");
                        else {
                            worldEdit.setSelection(area.getRegion());
                            player.sendMessage("%prefix% §7Selected Area§8: §6" + area.getName());
                        }
                    } else player.sendMessage("%prefix% §c/area select §8[§6Area§8]");
                } else if (args[0].equalsIgnoreCase("info")) {
                    Area area;
                    if (args.length >= 2) area = Area.get(args[1]);
                    else area = Area.highestArea(player);
                    if (area != null) {
                        player.sendMessage("%prefix% §7Area§8: §6" + area.getName());
                        if (area.isGlobalArea()) player.sendMessage("%prefix% §7Bounds§8: §6Global");
                        else {
                            BlockVector3 p1 = area.getPos1();
                            BlockVector3 p2 = area.getPos2();
                            player.sendMessage("%prefix% §7World§8: §6" + area.getWorld().getName());
                            player.sendMessage("%prefix% §7Pos1§8: §6" + p1.getX() + "§8, §6" + p1.getY() + "§8, §6" + p1.getZ());
                            player.sendMessage("%prefix% §7Pos2§8: §6" + p2.getX() + "§8, §6" + p2.getY() + "§8, §6" + p2.getZ());
                        }
                        player.sendMessage("%prefix% §7Priority§8: §6" + area.getPriority());
                    } else player.sendMessage("%prefix% §c/area info §8[§6Area§8]");
                } else {
                    player.sendMessage("%prefix% §c/area schematic §8[§6Option§8] §8(§6Area§8)");
                    player.sendMessage("%prefix% §c/area priority §8[§6Area§8] §8(§6Priority§8)");
                    player.sendMessage("%prefix% §c/area select §8(§6Area§8)");
                    player.sendMessage("%prefix% §c/area create §8[§6Name§8]");
                    player.sendMessage("%prefix% §c/area delete §8(§6Area§8)");
                    player.sendMessage("%prefix% §c/area info §8(§6Area§8)");
                    player.sendMessage("%prefix% §c/area list");
                }
            } else {
                player.sendMessage("%prefix% §c/area schematic §8[§6Option§8] §8(§6Area§8)");
                player.sendMessage("%prefix% §c/area priority §8[§6Area§8] §8(§6Priority§8)");
                player.sendMessage("%prefix% §c/area select §8(§6Area§8)");
                player.sendMessage("%prefix% §c/area create §8[§6Name§8]");
                player.sendMessage("%prefix% §c/area delete §8(§6Area§8)");
                player.sendMessage("%prefix% §c/area info §8(§6Area§8)");
                player.sendMessage("%prefix% §c/area list");
            }
        } else sender.sendMessage(Message.PLAYER_COMMAND_EN.getText());
        return false;
    }
}
