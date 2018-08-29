package gg.psyduck.bidoofunleashed.commands.admin;

import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.commands.annotations.Permission;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.Roles;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

@Aliases({"addgymleader", "agl"})
@Permission(admin = true)
public class AddGymLeaderCommand extends SpongeCommand {

    public AddGymLeaderCommand(SpongePlugin plugin) {
        super(plugin);
    }

    @Override
    public CommandElement[] getArgs() {
        return new CommandElement[] {
                GenericArguments.string(Text.of("gym-name")),
                GenericArguments.player(Text.of("player"))
        };
    }

    @Override
    public Text getDescription() {
        return Text.of("add a leader to a gym");
    }

    @Override
    public Text getUsage() {
        return Text.of("/addgymleader <gym-name> <player>");
    }

    @Override
    public SpongeCommand[] getSubCommands() {
        return new SpongeCommand[0];
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String name = (String) args.getOne("gym-name").get();
        Player user = args.<Player>getOne("player").get();

        Gym gym = BidoofUnleashed.getInstance().getDataRegistry().getGyms().stream().filter(g -> g.getName().equals(name)).findFirst()
                .orElseThrow(() -> new CommandException(Text.of("Invalid gym name")));

        BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(user.getUniqueId()).setRole(Roles.LEADER);
        gym.addLeader(user.getUniqueId(), EnumLeaderType.PLAYER);
        BidoofUnleashed.getInstance().getStorage().addOrUpdateGym(gym);
        return CommandResult.success();
    }
}
