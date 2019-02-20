package gg.psyduck.bidoofunleashed.commands.general;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.api.battlables.BU3BattleBase;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.commands.arguments.EliteFourOrGymArg;
import gg.psyduck.bidoofunleashed.commands.arguments.GymArg;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.e4.E4Stage;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.Sponge;
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
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Aliases("challenge")
public class ChallengeCommand extends SpongeCommand {

	private static final Text GYM = Text.of("gym");
	private static final Text STAGE = Text.of("stage");

	public ChallengeCommand(SpongePlugin plugin) {
		super(plugin);
	}

	@Override
	public CommandElement[] getArgs() {
		return new CommandElement[]{
				new EliteFourOrGymArg(GYM),
				GenericArguments.optional(GenericArguments.integer(STAGE))
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
			BU3BattleBase base = args.<BU3BattleBase>getOne(GYM).get();
			BU3Battlable gym;
			if(base instanceof Gym) {
				gym = (Gym) base;
			} else {
				Optional<Integer> stage = args.getOne(STAGE);
				if(stage.isPresent()) {
					gym = ((EliteFour) base).getStages().get(Math.max(0, Math.min(3, stage.get() - 1)));
				} else {
					gym = ((EliteFour) base).getStages().get(0);
				}
			}

			try {
				for (Requirement requirement : gym.getBattleSettings(gym.getBattleType((Player) src)).getRequirements()) {
					if (!requirement.passes(gym, (Player) src)) {
						requirement.onInvalid(gym, (Player) src);
						return CommandResult.empty();
					}
				}
			} catch (Exception e) {
				throw new CommandException(Text.of("An error occurred whilst processing your information, please inform a staff member..."));
			}

			if(gym.isOpen()) {
				PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(((Player) src).getUniqueId());
				if(pd.isQueued()) {
					throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.MISC_ALREADY_QUEUED, null, null));
				}

				if (!gym.queue((Player) src)) {
					throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.ERRORS_CANT_CHALLENGE, null, null));
				}
				Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
				tokens.put("bu3_gym", s -> Optional.of(Text.of(gym.getName() + (gym instanceof E4Stage ? " Stage" : " Gym"))));
				tokens.put("bu3_queue_position", s -> Optional.of(Text.of(gym.getQueue().size())));
				src.sendMessage(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_CHALLENGE_QUEUED, tokens, null));

				pd.setQueued(true);

				for(UUID uuid : gym.getLeaders()) {
					Sponge.getServer().getPlayer(uuid).ifPresent(player -> player.sendMessage(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.NEW_CHALLENGER_QUEUED, tokens, null)));
				}
			} else {
				throw new CommandException(Text.of("That gym is not open..."));
			}
			return CommandResult.success();
		}

		throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.SOURCE_NOT_PLAYER, null, null));
	}
}
