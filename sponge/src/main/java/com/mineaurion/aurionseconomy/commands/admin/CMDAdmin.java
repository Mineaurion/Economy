package com.mineaurion.aurionseconomy.commands.admin;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import com.mineaurion.help.HELP.Help;

public class CMDAdmin implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			Help.executeList(src,Help.get("economieadmin").get().getChildren());
		return CommandResult.success();
	}
}
