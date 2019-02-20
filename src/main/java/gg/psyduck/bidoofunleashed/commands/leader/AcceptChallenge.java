package gg.psyduck.bidoofunleashed.commands.leader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.exceptions.BattleStartException;
import gg.psyduck.bidoofunleashed.commands.arguments.GymArg;
import gg.psyduck.bidoofunleashed.config.ConfigKeys;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.ui.gympools.GymPoolUI;
import gg.psyduck.bidoofunleashed.utils.GeneralUtils;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Aliases({"acceptchallenge", "accept"})
public class AcceptChallenge extends SpongeCommand {

	private static final Text GYM = Text.of("gym");

	public AcceptChallenge(SpongePlugin plugin) {
		super(plugin);
	}

	@Override
	public CommandElement[] getArgs() {
		return new CommandElement[] {
				new GymArg(GYM)
		};
	}

	@Override
	public Text getDescription() {
		return Text.of("Accepts the next challenge waiting in line to battle the gym");
	}

	@Override
	public Text getUsage() {
		return Text.of("/bu3 accept <gym>");
	}

	@Override
	public SpongeCommand[] getSubCommands() {
		return new SpongeCommand[0];
	}

	@Override
	@SuppressWarnings("ConstantConditions")
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.SOURCE_NOT_PLAYER, null, null));
		}

		Player leader = (Player) src;
		if(GeneralUtils.search(leader.getUniqueId()).size() == 0) {
			throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.PLAYER_NOT_LEADER, null, null));
		}

		Gym gym = args.<Gym>getOne(GYM).get();
		if(gym != null) {
			if(!gym.getLeaders().contains(leader.getUniqueId())) {
				throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.PLAYER_NOT_LEADER_OF_GYM, null, null));
			}

			if(gym.getQueue().isEmpty()) {
				throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_ACCEPT_EMPTY_QUEUE, null, null));
			}

			UUID turn = gym.getQueue().poll();
			if(gym.getQueue().size() != 0 && leader.getUniqueId().equals(turn)) {
				gym.getQueue().add(turn);
				turn = gym.getQueue().poll();
			}

			Player challenger = Sponge.getServer().getPlayer(turn).orElseThrow(() -> new CommandException(Text.of("Something happened finding the next player")));
			if(gym.getBattleSettings(gym.getBattleType(challenger)).getPool().getTeam().isEmpty()) {
				throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_ACCEPT_EMPTY_TEAM_POOL, null, null));
			}

			PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(challenger.getUniqueId());
			pd.setQueued(false);
			//challenger.sendMessage(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.COMMANDS_ACCEPT_LEADER_SELECTING_TEAM, null, null));
			//new GymPoolUI(player, next, gym, false).open(player, 1);
			Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
			tokens.put("bu3_wait", cs -> Optional.of(Text.of(BidoofUnleashed.getInstance().getConfig().get(ConfigKeys.TELEPORT_WAIT))));
			challenger.sendMessage(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.MISC_CHALLENGE_BEGINNING, tokens, null));
			leader.sendMessage(MessageUtils.fetchAndParseMsg(leader, MsgConfigKeys.MISC_CHALLENGE_BEGINNING_LEADER_SELECTED, tokens, null));
			Sponge.getScheduler().createTaskBuilder().execute(() -> {
				try {
					gym.startBattle(leader, challenger, Lists.newArrayList());
				} catch (BattleStartException e) {
					challenger.sendMessage(e.getReason());
					leader.sendMessage(e.getReason());
				}
			}).delay(10, TimeUnit.SECONDS).submit(BidoofUnleashed.getInstance());
		} else {
			throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_ACCEPT_NOT_ON_DUTY, null, null));
		}

		return CommandResult.success();
	}
}
