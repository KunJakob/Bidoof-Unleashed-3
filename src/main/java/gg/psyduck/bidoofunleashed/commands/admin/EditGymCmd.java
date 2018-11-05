package gg.psyduck.bidoofunleashed.commands.admin;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.commands.annotations.Permission;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.commands.arguments.ExistingGymSpec;
import gg.psyduck.bidoofunleashed.commands.arguments.GymArg;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Aliases({"editgym", "eg"})
@Permission(admin = true)
public class EditGymCmd extends SpongeCommand {

	private static final Text GYM = Text.of("gym");
	private static final Text SPEC = Text.of("spec");

	public EditGymCmd(SpongePlugin plugin) {
		super(plugin);
	}

	@Override
	public CommandElement[] getArgs() {
		return new CommandElement[] {
				new GymArg(GYM),
				GenericArguments.remainingJoinedStrings(SPEC)
		};
	}

	@Override
	public Text getDescription() {
		return Text.of("Edits a gym to the specified spec");
	}

	@Override
	public Text getUsage() {
		return Text.of("/bu3 editgym <gym> <spec>");
	}

	@Override
	public SpongeCommand[] getSubCommands() {
		return new SpongeCommand[0];
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Gym gym = args.<Gym>getOne(GYM).get();
		String spec = args.<String>getOne(SPEC).get();

		try {
			String prior = gym.getName();
			ExistingGymSpec.parseValue(src, gym, spec);
			if(gym.getName().equalsIgnoreCase(prior)) {
				BidoofUnleashed.getInstance().getStorage().updateGym(gym);
			} else {
				BidoofUnleashed.getInstance().getStorage().addGym(gym);
				gym.initialize();
			}

			Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
			tokens.put("bu3_gym", cs -> Optional.of(Text.of(gym.getName())));
			src.sendMessage(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_EDIT_GYM_SUCCESS, tokens, null));
			if(!gym.isReady()) {
				src.sendMessage(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.MISC_GYM_MORE_REQUIRED, tokens, null));
			}
		} catch (Exception e) {
			throw new CommandException(Text.of("An error prevented the editing of that gym..."));
		}

		return CommandResult.success();
	}
}
