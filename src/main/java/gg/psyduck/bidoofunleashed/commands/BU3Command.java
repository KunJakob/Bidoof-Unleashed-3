package gg.psyduck.bidoofunleashed.commands;

import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.commands.admin.*;
import gg.psyduck.bidoofunleashed.commands.general.ChallengeCommand;
import gg.psyduck.bidoofunleashed.commands.general.HelpCmd;
import gg.psyduck.bidoofunleashed.commands.leader.AcceptChallenge;
import gg.psyduck.bidoofunleashed.commands.leader.CloseGymCommand;
import gg.psyduck.bidoofunleashed.commands.admin.GiveBadgeCommand;
import gg.psyduck.bidoofunleashed.commands.general.GymListCommand;
import gg.psyduck.bidoofunleashed.commands.leader.OpenGymCommand;
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
		return new SpongeCommand[]{
				new HelpCmd(plugin, this),
				new AddGymCommand(plugin),
				new CreateE4Cmd(plugin),
				new EditGymCmd(plugin),
				new DeleteGymCommand(plugin),
				new AddGymLeaderCommand(plugin),
				new AddE4Member(plugin),
				new ChallengeCommand(plugin),
				new GiveBadgeCommand(plugin),
				new RemoveBadgeCommand(plugin),
				new CheckGymPoolCmd(plugin),
				new OpenGymCommand(plugin),
				new CloseGymCommand(plugin),
				new AcceptChallenge(plugin),
				new AddRequirementCommand(plugin),
				new GymArenaSetupCmd(plugin),
				new GymListCommand(plugin),
				new AddPokemonSpec(plugin),
		};
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		throw new CommandException(Text.of("Please specify a subcommand..."));
	}
}
