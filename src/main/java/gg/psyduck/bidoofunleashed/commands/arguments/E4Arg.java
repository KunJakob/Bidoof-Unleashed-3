package gg.psyduck.bidoofunleashed.commands.arguments;

import com.nickimpact.impactor.api.commands.elements.BaseCommandElement;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.gyms.e4.E4;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class E4Arg extends BaseCommandElement<E4> {

    public E4Arg(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected E4 parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String arg = args.next();

        return BidoofUnleashed.getInstance().getDataRegistry().getE4().stream()
                .filter(e4 -> e4.getName().equalsIgnoreCase(arg))
                .findFirst()
                .orElseThrow(() -> args.createError(Text.of("Invalid E4 specified")));
    }

    @Override
    public List<String> complete(CommandSource source, CommandArgs args, CommandContext context) {
        return BidoofUnleashed.getInstance().getDataRegistry().getE4().stream().map(Gym::getName).collect(Collectors.toList());
    }
}
