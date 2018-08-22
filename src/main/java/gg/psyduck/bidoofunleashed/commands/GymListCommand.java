package gg.psyduck.bidoofunleashed.commands;

import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.storage.BU3Storage;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Aliases("gymlist")
public class GymListCommand extends SpongeCommand {

    public GymListCommand(SpongePlugin plugin) {
        super(plugin);
        this.register(plugin);
    }

    @Override
    public CommandElement[] getArgs() {
        return new CommandElement[0];
    }

    @Override
    public Text getDescription() {
        return Text.of("List all gyms");
    }

    @Override
    public Text getUsage() {
        return Text.of("/gymlist");
    }

    @Override
    public SpongeCommand[] getSubCommands() {
        return new SpongeCommand[0];
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        BU3Storage storage = (BU3Storage) BidoofUnleashed.getInstance().getStorage();
        List<Gym> gyms = new ArrayList<>();
        try {
            gyms = storage.fetchGyms().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        PaginationList.Builder builder = PaginationList.builder()
                .linesPerPage(10)
                .title(Text.of("Gyms List"));

        List<Text> contents = new ArrayList<>();
        for (Gym gym : gyms) {
            Text open = Text.builder()
                    .append(gym.isOpen() ? Text.of(TextColors.GREEN, "OPEN") : Text.of(TextColors.RED, "CLOSED"))
                    .build();

            Text requirements = Text.builder()
                    .append(Text.of("requirements"))
                    .build();
            Text rewards = Text.builder()
                    .append(Text.of("rewards"))
                    .build();
            Text rules = Text.builder()
                    .append(Text.of("rules"))
                    .build();
            Text clauses = Text.builder()
                    .append(Text.of("clauses"))
                    .build();


            Text text = Text.of(
                    gym.getName(), " ", gym.getBadge().getBadge(), " ", open, " ", requirements, " ", rewards, " ", rules, " ", clauses
            );
            contents.add(text);
        }

        builder.contents(contents).sendTo(src);
        return CommandResult.success();
    }
}
