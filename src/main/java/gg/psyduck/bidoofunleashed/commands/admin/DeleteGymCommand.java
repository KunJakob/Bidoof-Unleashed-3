package gg.psyduck.bidoofunleashed.commands.admin;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.commands.annotations.Permission;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.commands.arguments.GymArg;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.battles.gyms.Gym;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.Map;

@Aliases({"deletegym", "dg"})
@Permission(admin = true)
public class DeleteGymCommand extends SpongeCommand {

    public DeleteGymCommand(SpongePlugin plugin) {
        super(plugin);
    }

    @Override
    public CommandElement[] getArgs() {
        return new CommandElement[] {
                new GymArg(Text.of("gym"))
        };
    }

    @Override
    public Text getDescription() {
        return Text.of("Deletes the specified gym");
    }

    @Override
    public Text getUsage() {
        return Text.of("/deletegym <gym>");
    }

    @Override
    public SpongeCommand[] getSubCommands() {
        return new SpongeCommand[0];
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Gym gym = args.<Gym>getOne("gym").get();
        BidoofUnleashed.getInstance().getService().purgeGym(gym);

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("bu3_gym", gym.getName());
        src.sendMessage(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_DELETE_GYM_SUCCESS, null, variables));
        return CommandResult.success();
    }
}
