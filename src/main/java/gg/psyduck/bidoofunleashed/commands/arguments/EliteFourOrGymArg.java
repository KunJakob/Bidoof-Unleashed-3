package gg.psyduck.bidoofunleashed.commands.arguments;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.commands.elements.BaseCommandElement;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.api.battlables.BU3BattleBase;
import gg.psyduck.bidoofunleashed.e4.E4Stage;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class EliteFourOrGymArg extends BaseCommandElement<BU3Battlable> {

	public EliteFourOrGymArg(@Nullable Text key) {
		super(key);
	}

	@Nullable
	@Override
	protected BU3Battlable parseValue(CommandSource src, CommandArgs args) throws ArgumentParseException {
		String arg = args.next();
		BU3BattleBase<?> base =  BidoofUnleashed.getInstance().getDataRegistry().getBattlables().values().stream()
				.filter(x -> x.getName().equalsIgnoreCase(arg))
				.findAny()
				.orElseThrow(() -> args.createError(Text.of("Invalid instance specified...")));

		if(base instanceof Gym) {
			return (Gym) base;
		}

		return (E4Stage) base;
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext ctx) {
		try {
			String arg = args.peek();
			return BidoofUnleashed.getInstance().getDataRegistry().getBattlables().values().stream().filter(x -> x.getName().toLowerCase().startsWith(arg)).map(BU3BattleBase::getName).collect(Collectors.toList());
		} catch (ArgumentParseException e) {
			return Lists.newArrayList();
		}
	}
}
