package gg.psyduck.bidoofunleashed.commands.admin;

import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.commands.annotations.Permission;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

@Aliases({"setuparena", "sa", "sarena"})
@Permission(admin = true)
public class GymArenaSetupCmd extends SpongeCommand {

	private static final Text GYM = Text.of("gym");
	private static final Text AREA = Text.of("area");

	public GymArenaSetupCmd(SpongePlugin plugin) {
		super(plugin);
	}

	@Override
	public CommandElement[] getArgs() {
		return new CommandElement[] {
				new GymArg(GYM),
				GenericArguments.enumValue(AREA, ArenaArea.class)
		};
	}

	@Override
	public Text getDescription() {
		return Text.of();
	}

	@Override
	public Text getUsage() {
		return Text.of("/bu3 setuparena <gym> <leader | challenger | spectators>");
	}

	@Override
	public SpongeCommand[] getSubCommands() {
		return new SpongeCommand[0];
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)) {
			throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.SOURCE_NOT_PLAYER, null, null));
		}

		Player player = (Player) src;
		Gym gym = args.<Gym>getOne(GYM).get();
		ArenaArea area = args.<ArenaArea>getOne(AREA).get();
		switch(area) {
			case CHALLEGNER:
				gym.getArena().setChallenger(new Gym.LocAndRot(player.getWorld().getUniqueId(), player.getPosition(), player.getRotation()));
				break;
			case LEADER:
				gym.getArena().setLeader(new Gym.LocAndRot(player.getWorld().getUniqueId(), player.getPosition(), player.getRotation()));
				break;
			case SPECTATORS:
				gym.getArena().setSpectators(new Gym.LocAndRot(player.getWorld().getUniqueId(), player.getPosition(), player.getRotation()));
				break;
		}

		src.sendMessages(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_ARENA_AREA_SETUP, null, null));
		BidoofUnleashed.getInstance().getStorage().updateGym(gym);

		return CommandResult.success();
	}

	private enum ArenaArea {
		CHALLEGNER,
		LEADER,
		SPECTATORS,
	}
}
