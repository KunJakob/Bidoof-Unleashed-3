package gg.psyduck.bidoofunleashed.commands.arguments;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.commands.elements.BaseCommandElement;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class E4Arg extends BaseCommandElement<EliteFour> {

	public E4Arg(@Nullable Text key) {
		super(key);
	}

	@Nullable
	@Override
	protected EliteFour parseValue(CommandSource src, CommandArgs args) throws ArgumentParseException {
		String arg = args.next();
		return BidoofUnleashed.getInstance().getDataRegistry().getBattlables().values().stream()
				.filter(base -> base instanceof EliteFour)
				.map(base -> (EliteFour) base)
				.filter(e4 -> e4.getName().equalsIgnoreCase(arg))
				.findAny()
				.orElseThrow(() -> args.createError(Text.of("Invalid elite four specified...")));
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext ctx) {
		try {
			String arg = args.peek();
			return BidoofUnleashed.getInstance().getService().getAllEliteFour().stream().filter(e4 -> e4.getName().toLowerCase().startsWith(arg)).map(EliteFour::getName).collect(Collectors.toList());
		} catch (ArgumentParseException e) {
			return Lists.newArrayList();
		}
	}
}
