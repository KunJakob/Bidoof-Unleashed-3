package gg.psyduck.bidoofunleashed.commands.arguments;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.commands.elements.BaseCommandElement;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class GymArg extends BaseCommandElement<Gym> {

	public GymArg(@Nullable Text key) {
		super(key);
	}

	@Nullable
	@Override
	protected Gym parseValue(CommandSource src, CommandArgs args) throws ArgumentParseException {
		String arg = args.next();
		return BidoofUnleashed.getInstance().getDataRegistry().getGyms().stream()
				.filter(gym -> gym.getName().equalsIgnoreCase(arg))
				.findAny()
				.orElseThrow(() -> args.createError(Text.of("Invalid gym specified...")));
	}

	@Override
	public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
		return Lists.newArrayList();
	}
}
