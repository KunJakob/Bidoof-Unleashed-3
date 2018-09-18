package gg.psyduck.bidoofunleashed.commands.admin;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.commands.annotations.Permission;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.commands.arguments.GymArg;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.battles.gyms.Badge;
import gg.psyduck.bidoofunleashed.battles.gyms.Gym;
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

@Aliases("removebadge")
@Permission(admin = true)
public class RemoveBadgeCommand extends SpongeCommand {

    public RemoveBadgeCommand(SpongePlugin plugin) {
        super(plugin);
    }

    @Override
    public CommandElement[] getArgs() {
        return new CommandElement[]{
                GenericArguments.player(Text.of("target")),
                new GymArg(Text.of("gym"))
        };
    }

    @Override
    public Text getDescription() {
        return Text.of("Takes a badge away from a player");
    }

    @Override
    public Text getUsage() {
        return Text.of("/removebadge <target> <gym>");
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
        if (!gym.getLeaders().containsKey(player.getUniqueId())) {
            throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.PLAYER_NOT_LEADER, null, null));
        }

        Optional<Badge> optBadge = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(target.getUniqueId()).getBadges()
                .stream().filter(b -> b.getName().equals(gym.getBadge().getName()) && b.getItemType().equals(gym.getBadge().getItemType())).findFirst();

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("player", target.getName());

        if (!optBadge.isPresent()) {
            throw new CommandException(MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.BADGE_NOT_FOUND, null, variables));
        }

	    Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
	    tokens.put("bu3_badge", s -> Optional.of(Text.of(optBadge.get().getName())));

        BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(target.getUniqueId()).removeBadge(optBadge.get());

        player.sendMessage(MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.COMMANDS_REMOVE_BADGE_LEADER, null, variables));
        target.sendMessage(MessageUtils.fetchAndParseMsg(target, MsgConfigKeys.COMMANDS_REMOVE_BADGE_CHALLENGER, null, variables));
        return CommandResult.success();
    }
}
