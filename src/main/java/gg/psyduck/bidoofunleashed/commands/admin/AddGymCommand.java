package gg.psyduck.bidoofunleashed.commands.admin;

import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.commands.annotations.Permission;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.commands.arguments.GymSpec;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

@Aliases({"addgym", "ag"})
@Permission(admin = true)
public class AddGymCommand extends SpongeCommand {

	private final Text SPEC = Text.of("spec");

    public AddGymCommand(SpongePlugin plugin) {
        super(plugin);
    }

    @Override
    public CommandElement[] getArgs() {
        return new CommandElement[] {
        		GenericArguments.remainingJoinedStrings(SPEC)
        };
    }

    @Override
    public Text getDescription() {
        return Text.of("Creates a gym");
    }

    @Override
    public Text getUsage() {
        return Text.of("/", this.getAllAliases().get(0), " <spec>");
    }

    @Override
    public SpongeCommand[] getSubCommands() {
        return new SpongeCommand[0];
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
	    try {
		    BidoofUnleashed.getInstance().getService().addGym(GymSpec.parseValue(args.<String>getOne(SPEC).get()));
		    src.sendMessages(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_ADD_GYM_SUCCESS, null, null));
	    } catch (Exception e) {
	    	e.printStackTrace();
		    throw new CommandException(Text.of(e.getMessage()));
	    }
	    return CommandResult.success();
    }
}
