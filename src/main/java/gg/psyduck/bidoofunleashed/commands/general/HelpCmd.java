package gg.psyduck.bidoofunleashed.commands.general;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.commands.BU3Command;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
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
				.contents(this.getUsage(reference))
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
	private List<Text> getUsage(SpongeCommand cmd) {
		List<Text> result = Lists.newArrayList();
		result.add(cmd.getUsage());
		for(SpongeCommand child : cmd.getSubCommands()) {
			result.addAll(this.getUsage(child));
		}

		return result;
	}
}
