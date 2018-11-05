package gg.psyduck.bidoofunleashed.commands.admin;

import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.commands.arguments.E4Arg;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.text.Text;

public class AddE4Member extends SpongeCommand {

	private static final Text E4 = Text.of("e4");
	private static final Text STAGE = Text.of("stage");
	private static final Text LEADER = Text.of("leader");

	public AddE4Member(SpongePlugin plugin) {
		super(plugin);
	}

	@Override
	public CommandElement[] getArgs() {
		return new CommandElement[] {
				new E4Arg(E4),
				GenericArguments.string(Text.of(STAGE)),
				GenericArguments.string(Text.of(LEADER))
		};
	}

	@Override
	public Text getDescription() {
		return Text.of("Adds a member to the elite four instance for handling battles");
	}

	@Override
	public Text getUsage() {
		return Text.of("/bu3 ae4m (e4) (stage #/\"champion\") (player/\"npc\"");
	}

	@Override
	public SpongeCommand[] getSubCommands() {
		return new SpongeCommand[0];
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		EliteFour e4 = args.<EliteFour>getOne(E4).get();
		String stage = args.<String>getOne(STAGE).get();
		String leader = args.<String>getOne(LEADER).get();

		if(src instanceof ConsoleSource && leader.equalsIgnoreCase("npc")) {
			throw new CommandException(Text.of("Only players can add in NPC leaders due to location requirements..."));
		}

		return CommandResult.success();
	}
}
