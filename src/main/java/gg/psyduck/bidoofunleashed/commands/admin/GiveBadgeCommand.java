package gg.psyduck.bidoofunleashed.commands.admin;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.commands.annotations.Permission;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.commands.arguments.GymArg;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.BadgeReference;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.sql.Date;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Aliases({"givebadge", "gb"})
@Permission(admin = true)
public class GiveBadgeCommand extends SpongeCommand {

    public GiveBadgeCommand(SpongePlugin plugin) {
        super(plugin);
    }

    @Override
    public CommandElement[] getArgs() {
        return new CommandElement[] {
                GenericArguments.player(Text.of("target")),
                new GymArg(Text.of("gym"))
        };
    }

    @Override
    public Text getDescription() {
        return Text.of("Gives a gym badge to a player");
    }

    @Override
    public Text getUsage() {
        return Text.of("/bu3 givebadge <target> <gym>");
    }

    @Override
    public SpongeCommand[] getSubCommands() {
        return new SpongeCommand[0];
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.SOURCE_NOT_PLAYER, null, null));
        }
        Player player = (Player) src;
        Player target = (Player) args.getOne("target").get();
        Gym gym = (Gym) args.getOne("gym").get();
        if (!gym.getLeaders().contains(player.getUniqueId())) {
            throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.PLAYER_NOT_LEADER, null, null));
        }

        BadgeReference badge = new BadgeReference(gym.getBadge().getName(), Date.from(Instant.now()), player.getUniqueId());
        PlayerData data = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(target.getUniqueId());
        data.awardBadge(badge);
        BidoofUnleashed.getInstance().getStorage().updatePlayerData(data);

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("player", target.getName());

	    Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
	    tokens.put("bu3_badge", s -> Optional.of(Text.of(badge.getIdentifier())));

        player.sendMessage(MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.COMMANDS_GIVE_BADGE_LEADER, tokens, variables));
        target.sendMessage(MessageUtils.fetchAndParseMsg(target, MsgConfigKeys.COMMANDS_GIVE_BADGE_CHALLENGER, tokens, variables));
        return CommandResult.success();
    }
}
