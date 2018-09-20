package gg.psyduck.bidoofunleashed.commands.admin;

import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.commands.arguments.GymArg;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

public class AddPokemonSpec extends SpongeCommand {

	private static final Text GYM = Text.of("gym");
	private static final Text ZONE = Text.of("zone");
	private static final Text SPEC = Text.of("spec");

	public AddPokemonSpec(SpongePlugin plugin) {
		super(plugin);
	}

	@Override
	public CommandElement[] getArgs() {
		return new CommandElement[] {
				new GymArg(GYM),
				GenericArguments.enumValue(ZONE, Setting.class),
				GenericArguments.remainingJoinedStrings(SPEC)
		};
	}

	@Override
	public Text getDescription() {
		return Text.of("Adds a pokemon spec design to the pool of pokemon available to the gym");
	}

	@Override
	public Text getUsage() {
		return Text.of("/bu3 addspec <gym> <battle type> <spec>");
	}

	@Override
	public SpongeCommand[] getSubCommands() {
		return new SpongeCommand[0];
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Gym gym = args.<Gym>getOne(GYM).get();
		Setting settings = args.<Setting>getOne(ZONE).get();
		PokemonSpec spec = new PokemonSpec(args.<String>getOne(SPEC).get().split(" "));

		return CommandResult.success();
	}

	private enum Setting {
		Initial,
		Rematch,
	}
}
