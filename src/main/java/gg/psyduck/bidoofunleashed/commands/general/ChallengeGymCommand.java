package gg.psyduck.bidoofunleashed.commands.general;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.commands.arguments.E4Arg;
import gg.psyduck.bidoofunleashed.commands.arguments.GymArg;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.gyms.e4.E4;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Aliases("challenge")
public class ChallengeGymCommand extends SpongeCommand {

	private static final Text GYM = Text.of("gym");
    private static final Text E4 = Text.of("e4");

	public ChallengeGymCommand(SpongePlugin plugin) {
		super(plugin);
	}

	@Override
	public CommandElement[] getArgs() {
		return new CommandElement[]{
                GenericArguments.onlyOne(
                        GenericArguments.seq(
                                new GymArg(GYM),
                                new E4Arg(E4)
                        )
                )
		};
	}

	@Override
	public Text getDescription() {
		return Text.of("Attempts to challenge a gym, assuming you meet its requirements");
	}

	@Override
	public Text getUsage() {
		return Text.of("/bu3 challenge <gym>");
	}

	@Override
	public SpongeCommand[] getSubCommands() {
		return new SpongeCommand[0];
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(src instanceof Player) {
			Gym gym;
			if (args.getOne(GYM).isPresent()) {
			    gym = args.<Gym>getOne(GYM).get();
            } else {
			    gym = args.<E4>getOne(E4).get();
            }
			try {
				for (Requirement requirement : gym.getRequirements()) {
					if (!requirement.passes(gym, (Player) src)) {
						requirement.onInvalid(gym, (Player) src);
						return CommandResult.empty();
					}
				}
			} catch (Exception e) {
				throw new CommandException(Text.of("An error occurred whilst processing your information, please inform a staff member..."));
			}

			gym.queue((Player) src);
			Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
			tokens.put("bu3_gym", s -> Optional.of(Text.of(gym.getName())));
			src.sendMessage(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_CHALLENGE_QUEUED, tokens, null));
			return CommandResult.success();
		}

		throw new CommandException(Text.of("Only players can challenge a gym..."));
	}
}
