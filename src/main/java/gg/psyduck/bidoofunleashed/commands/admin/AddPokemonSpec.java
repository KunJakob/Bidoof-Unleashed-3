package gg.psyduck.bidoofunleashed.commands.admin;

import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.commands.annotations.Permission;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.client.gui.pokemoneditor.ImportExportConverter;
import com.pixelmonmod.pixelmon.comm.PixelmonData;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.pixelmon.specs.BU3PokemonSpec;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.commands.arguments.GymArg;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import net.minecraft.world.World;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

@Aliases({"aps", "addpoke", "addpokemonspec"})
@Permission(admin = true)
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
				GenericArguments.enumValue(ZONE, EnumBattleType.class),
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
		EnumBattleType settings = args.<EnumBattleType>getOne(ZONE).get();
		BU3PokemonSpec spec = new BU3PokemonSpec(args.<String>getOne(SPEC).get().split(" "));
		PixelmonData data = new PixelmonData(spec.create((World) Sponge.getServer().getWorlds().iterator().next()));
		data.pokeball = EnumPokeballs.PokeBall;
		String export = "\n" + ImportExportConverter.getExportText(data);

		gym.getBattleSettings(settings).getPool().getTeam().add(spec);
		gym.getBattleSettings(settings).getPool().append(export);

		src.sendMessages(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_ADD_POKEMON, null, null));

		return CommandResult.success();
	}
}
