package net.nonswag.tnl.protect.commands;

import net.nonswag.tnl.core.api.command.Invocation;
import net.nonswag.tnl.listener.api.command.simple.SubCommand;
import net.nonswag.tnl.protect.api.area.Area;

import javax.annotation.Nonnull;
import java.util.Set;

class List extends SubCommand {

    List() {
        super("list");
    }

    @Override
    protected void execute(@Nonnull Invocation invocation) {
        Set<String> names = Area.names();
        invocation.source().sendMessage("%prefix% §7Areas §8(§a" + names.size() + "§8): §6" + String.join("§8, §6", names));
    }
}
