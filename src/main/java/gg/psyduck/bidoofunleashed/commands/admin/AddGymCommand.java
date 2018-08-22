package gg.psyduck.bidoofunleashed.commands.admin;

import com.flowpowered.math.vector.Vector3d;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.storage.BU3Storage;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;

import java.util.concurrent.ExecutionException;

@Aliases("addgym")
public class AddGymCommand extends SpongeCommand {

    public AddGymCommand(SpongePlugin plugin) {
        super(plugin);
    }

    @Override
    public CommandElement[] getArgs() {
        return new CommandElement[] {
                GenericArguments.string(Text.of("name")),
                GenericArguments.integer(Text.of("lvl-cap")),
                GenericArguments.catalogedElement(Text.of("badge-type"), ItemType.class),
                GenericArguments.string(Text.of("badge-name"))
        };
    }

    @Override
    public Text getDescription() {
        return Text.of("Creates a gym");
    }

    @Override
    public Text getUsage() {
        return Text.of("/", this.getAllAliases().get(0), " <name> <lvl-cap> <badge>");
    }

    @Override
    public SpongeCommand[] getSubCommands() {
        return new SpongeCommand[0];
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String name = (String) args.getOne("name").get();
        int lvlCap = (int) args.getOne("lvl-cap").get();
        ItemType badge = (ItemType) args.getOne("badge-type").get();
        String badgeName = (String) args.getOne("badge-name").get();

        BU3Storage storage = (BU3Storage) BidoofUnleashed.getInstance().getStorage();

        Gym gym = Gym.builder()
                .name(name)
                .badge(new Badge(badgeName, badge.getName(), null, null, null))
                .arena(new Gym.Arena(
                        new Gym.LocAndRot(new Vector3d(), new Vector3d()),
                        new Gym.LocAndRot(new Vector3d(), new Vector3d()),
                        new Gym.LocAndRot(new Vector3d(), new Vector3d())
                ))
                .levelCap(lvlCap)
                .build();

        try {
            storage.addOrUpdateGym(gym).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return CommandResult.success();
    }
}
