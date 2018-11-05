package gg.psyduck.bidoofunleashed.commands.general;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.commands.annotations.Permission;
import com.nickimpact.impactor.api.plugins.IPlugin;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.commands.BU3Command;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

@Aliases({"help"})
public class HelpCmd extends SpongeCommand {

	private BU3Command reference;

	public HelpCmd(SpongePlugin plugin, BU3Command reference) {
		super(plugin);
		this.reference = reference;
	}

	@Override
	public CommandElement[] getArgs() {
		return new CommandElement[0];
	}

	@Override
	public Text getDescription() {
		return Text.of("States any commands the plugin can run for the player in question");
	}

	@Override
	public Text getUsage() {
		return Text.of("/bu3 help");
	}

	@Override
	public SpongeCommand[] getSubCommands() {
		return new SpongeCommand[0];
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		PaginationList list = PaginationList.builder()
				.title(Text.of(TextColors.YELLOW, "Bidoof Unleashed Help"))
				.contents(this.getUsage(src, reference))
				.build();
		list.sendTo(src);

		return CommandResult.success();
	}

	/**
	 * A recursive function which will read from all children of the given command, fetching their usage information.
	 *
	 * @param cmd The command to parse
	 * @return A list of text representing every command's usage.
	 */
	private List<Text> getUsage(CommandSource src, SpongeCommand cmd) {
		List<Text> result = Lists.newArrayList();
		Text x = Text.builder(cmd.getUsage(), "").onHover(TextActions.showText(cmd.getDescription())).build();
		result.add(x);
		for(SpongeCommand child : cmd.getSubCommands()) {
			if(!(src instanceof ConsoleSource) && src.hasPermission(this.buildPermission(BidoofUnleashed.getInstance(), child))) {
				result.addAll(this.getUsage(src, child));
			} else {
				result.addAll(this.getUsage(src, child)); // Ignore permission check for console
			}
		}

		return result;
	}

	private String buildPermission(IPlugin plugin, SpongeCommand cmd) {
		String permission = plugin.getPluginInfo().getID() + ".command.";
		if (this.getClass().isAnnotationPresent(Permission.class)) {
			Permission p = cmd.getClass().getAnnotation(Permission.class);
			if (p.admin()) {
				permission = permission + "admin.";
			}

			if (!p.prefix().equals("")) {
				permission = permission + p.prefix() + ".";
			}

			if (!p.value().equals("")) {
				permission = permission + p.value();
			} else {
				permission = permission + cmd.getAllAliases().get(0).toLowerCase();
			}

			permission = permission + ".";
			if (!p.suffix().equals("")) {
				permission = permission + p.suffix();
			} else {
				permission = permission + "base";
			}
		} else {
			permission = permission + cmd.getAllAliases().get(0).toLowerCase() + ".base";
		}

		return permission;
	}
}
