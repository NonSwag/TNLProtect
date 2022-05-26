package net.nonswag.tnl.protect.commands;

import net.nonswag.tnl.listener.api.command.simple.SimpleCommand;

public class AreaCommand extends SimpleCommand {

    public AreaCommand() {
        super("area", "tnl.protect");
        addSubCommand(new Create());
        addSubCommand(new Delete());
        addSubCommand(new Flag());
        addSubCommand(new Info());
        addSubCommand(new List());
        addSubCommand(new Priority());
        addSubCommand(new Redefine());
        addSubCommand(new Schematic());
        addSubCommand(new Select());
    }
}
