package gg.psyduck.bidoofunleashed.commands;

import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.commands.admin.AddGymCommand;
import gg.psyduck.bidoofunleashed.commands.admin.AddGymLeaderCommand;
import gg.psyduck.bidoofunleashed.commands.admin.CheckGymPoolCmd;
import gg.psyduck.bidoofunleashed.commands.admin.DeleteGymCommand;
import gg.psyduck.bidoofunleashed.commands.general.ChallengeGymCommand;
import gg.psyduck.bidoofunleashed.commands.general.CheckBadgeCommand;
import gg.psyduck.bidoofunleashed.commands.general.CloseGymCommand;
import gg.psyduck.bidoofunleashed.commands.general.GiveBadgeCommand;
import gg.psyduck.bidoofunleashed.commands.general.GymListCommand;
import gg.psyduck.bidoofunleashed.commands.general.OpenGymCommand;
import gg.psyduck.bidoofunleashed.commands.general.RemoveBadgeCommand;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

@Aliases("bu3")
public class BU3Command extends SpongeCommand {

	public BU3Command(SpongePlugin plugin) {
		super(plugin);
	}

	@Override
	public CommandElement[] getArgs() {
		return new CommandElement[0];
	}

	@Override
	public Text getDescription() {
		return Text.of("The base command to the bidoof unleashed plugin");
	}

	@Override
	public Text getUsage() {
		return Text.of("/bu3 <args>");
	}

	@Override
	public SpongeCommand[] getSubCommands() {
		return new SpongeCommand[] {
				new AddGymCommand(plugin),
                new DeleteGymCommand(plugin),
				new AddGymLeaderCommand(plugin),
				new ChallengeGymCommand(plugin),
				new GymListCommand(plugin),
                new GiveBadgeCommand(plugin),
                new RemoveBadgeCommand(plugin),
				new CheckGymPoolCmd(plugin),
                new OpenGymCommand(plugin),
                new CloseGymCommand(plugin),
		};
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		throw new CommandException(Text.of("Please specify a subcommand..."));
	}
}
