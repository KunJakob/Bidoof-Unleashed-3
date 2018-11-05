package gg.psyduck.bidoofunleashed.commands.admin;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.commands.annotations.Permission;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.commands.arguments.NewGymSpec;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Aliases({"addgym", "ag", "creategym", "cg"})
@Permission(admin = true)
public class AddGymCommand extends SpongeCommand {

	private final Text NAME = Text.of("name");
	private final Text SPEC = Text.of("spec");

    public AddGymCommand(SpongePlugin plugin) {
        super(plugin);
    }

    @Override
    public CommandElement[] getArgs() {
        return new CommandElement[] {
        		GenericArguments.string(NAME),
        		GenericArguments.optional(GenericArguments.remainingJoinedStrings(SPEC))
        };
    }

    @Override
    public Text getDescription() {
        return Text.of("Creates a gym");
    }

    @Override
    public Text getUsage() {
        return Text.of("/bu3 ", this.getAllAliases().get(0), " <spec>");
    }

    @Override
    public SpongeCommand[] getSubCommands() {
        return new SpongeCommand[0];
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
	    try {
	    	String name = args.<String>getOne(NAME).get();
		    Gym gym = NewGymSpec.parseValue(src, name, args.<String>getOne(SPEC).orElse(null));
		    if(BidoofUnleashed.getInstance().getDataRegistry().getBattlables().values().stream().anyMatch(g -> g.getName().equalsIgnoreCase(gym.getName()))) {
		    	throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_ADD_GYM_EXISTS, null, null));
		    }

		    BidoofUnleashed.getInstance().getService().addGym(gym);
		    src.sendMessages(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_ADD_GYM_SUCCESS, null, null));
		    if(!gym.isReady()) {
			    Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
			    tokens.put("bu3_gym", cs -> Optional.of(Text.of(gym.getName())));
			    src.sendMessages(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.MISC_GYM_MORE_REQUIRED, tokens, null));
		    }
	    } catch (Exception e) {
	    	e.printStackTrace();
		    throw new CommandException(Text.of("An error prevented the creation of the gym..."));
	    }
	    return CommandResult.success();
    }
}
