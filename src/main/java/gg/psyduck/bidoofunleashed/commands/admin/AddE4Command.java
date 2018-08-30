package gg.psyduck.bidoofunleashed.commands.admin;

import com.flowpowered.math.vector.Vector3d;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.gyms.e4.E4;
import gg.psyduck.bidoofunleashed.impl.requirements.ChampionRequirement;
import gg.psyduck.bidoofunleashed.impl.requirements.GymRequirement;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class AddE4Command extends SpongeCommand {

    public AddE4Command(SpongePlugin plugin) {
        super(plugin);
    }

    @Override
    public CommandElement[] getArgs() {
        return new CommandElement[0];
    }

    @Override
    public Text getDescription() {
        return null;
    }

    @Override
    public Text getUsage() {
        return null;
    }

    @Override
    public SpongeCommand[] getSubCommands() {
        return new SpongeCommand[0];
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String name = (String) args.getOne("name").get();
        String typeArg = (String) args.getOne("type").get();
        E4.Type type = E4.Type.valueOf(typeArg);

        Gym.Builder builder = Gym.builder()
                .name(name)
                .arena(new Gym.Arena(
                        new Gym.LocAndRot(new Vector3d(), new Vector3d()),
                        new Gym.LocAndRot(new Vector3d(), new Vector3d()),
                        new Gym.LocAndRot(new Vector3d(), new Vector3d())
                ))
                .levelCap(100);
        for (Gym gym : BidoofUnleashed.getInstance().getDataRegistry().getGyms()) {
            builder.requirements(new GymRequirement(gym.getBadge().getName()));
        }

        if (type == E4.Type.CHAMPION) {
            builder.requirements(new ChampionRequirement());
        }

        E4 e4 = new E4(builder, type);

        BidoofUnleashed.getInstance().getService().addGym(e4);
        return CommandResult.success();
    }
}
