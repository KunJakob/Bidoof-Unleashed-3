package gg.psyduck.bidoofunleashed.commands.leader;

import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.commands.arguments.GymArg;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.players.Roles;
import gg.psyduck.bidoofunleashed.ui.gympools.GymPoolUI;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

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

		Player player = (Player) src;

		PlayerData data = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());
		if(data.getRole().ordinal() < Roles.LEADER.ordinal()) {
			throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.PLAYER_NOT_LEADER, null, null));
		}

		Gym gym = args.<Gym>getOne(GYM).get();
		if(gym != null) {
			if(!gym.getLeaders().contains(player.getUniqueId())) {
				throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.PLAYER_NOT_LEADER_OF_GYM, null, null));
			}

			if(gym.getQueue().isEmpty()) {
				throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_ACCEPT_EMPTY_QUEUE, null, null));
			}

			Player next = Sponge.getServer().getPlayer(gym.getQueue().poll()).orElseThrow(() -> new CommandException(Text.of("Something happened finding the next player")));
			if(gym.getBattleSettings(gym.getBattleType(next)).getPool().getTeam().isEmpty()) {
				throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_ACCEPT_EMPTY_TEAM_POOL, null, null));
			}

			PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(((Player) src).getUniqueId());
			pd.setQueued(false);
			next.sendMessage(MessageUtils.fetchAndParseMsg(next, MsgConfigKeys.COMMANDS_ACCEPT_LEADER_SELECTING_TEAM, null, null));
			new GymPoolUI(player, next, gym).open(player, 1);
		} else {
			throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_ACCEPT_NOT_ON_DUTY, null, null));
		}

		return CommandResult.success();
	}
}
