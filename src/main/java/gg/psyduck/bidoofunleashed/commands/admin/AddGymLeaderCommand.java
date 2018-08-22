package gg.psyduck.bidoofunleashed.commands.admin;

import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.storage.BU3Storage;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Aliases("addgymleader")
public class AddGymLeaderCommand extends SpongeCommand {

    public AddGymLeaderCommand(SpongePlugin plugin) {
        super(plugin);
    }

    @Override
    public CommandElement[] getArgs() {
        return new CommandElement[] {
                GenericArguments.string(Text.of("gym-name")),
                GenericArguments.user(Text.of("player"))
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
        User user = (User) args.getOne("user").get();

        BU3Storage storage = (BU3Storage) BidoofUnleashed.getInstance().getStorage();
        List<Gym> gyms = new ArrayList<>();
        try {
            gyms = storage.fetchGyms().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        Gym gym = gyms.stream().filter(g -> g.getName().equals(name)).findFirst()
                .orElseThrow(() -> new CommandException(Text.of("Invalid gym name")));
        gym.addLeader(user.getUniqueId(), EnumLeaderType.PLAYER);
        return CommandResult.success();
    }
}
