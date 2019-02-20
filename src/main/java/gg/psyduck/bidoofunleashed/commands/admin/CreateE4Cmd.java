package gg.psyduck.bidoofunleashed.commands.admin;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.commands.annotations.Permission;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BattlableBuilder;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.extensions.TriConsumer;
import gg.psyduck.bidoofunleashed.commands.arguments.NewGymSpec;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.e4.Champion;
import gg.psyduck.bidoofunleashed.e4.E4Stage;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import lombok.AllArgsConstructor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.regex.Pattern;

@Aliases({"createE4", "ce4"})
@Permission(admin = true)
public class CreateE4Cmd extends SpongeCommand {

	public CreateE4Cmd(SpongePlugin plugin) {
		super(plugin);
	}

	@Override
	public CommandElement[] getArgs() {
		return new CommandElement[] {
				GenericArguments.string(Text.of("category")),
				GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("spec")))
		};
	}

	@Override
	public Text getDescription() {
		return Text.of();
	}

	@Override
	public Text getUsage() {
		return Text.of("/bu3 ce4 (category) (spec)");
	}

	@Override
	public SpongeCommand[] getSubCommands() {
		return new SpongeCommand[0];
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player) && !(src instanceof ConsoleSource)) {
			throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.SOURCE_NOT_PLAYER, null, null));
		}

		String category = args.<String>getOne(Text.of("category")).get();
		String[] split = args.<String>getOne(Text.of("spec")).get().split(" ");
		Pattern pattern = Pattern.compile("(?<identifier>[a-zA-Z]+):(?<value>[a-zA-Z]+(:[a-zA-Z]+)*)");
		EliteFour.E4Builder builder = (EliteFour.E4Builder) EliteFour.builder().name(MessageUtils.titlize(category + " Elite Four")).category(category);

		for(String mapping : split) {
			java.util.regex.Matcher matcher = pattern.matcher(mapping);
			if(matcher.matches()) {
				String identifier = matcher.group("identifier");
				String value = matcher.group("value");

				for(EnumEliteFourDetails detail : EnumEliteFourDetails.values()) {
					if(detail.identifiers.stream().anyMatch(alias -> alias.equalsIgnoreCase(identifier))) {
						detail.application.accept(src, builder, value);
						break;
					}
				}
			}
		}

		EliteFour e4 = builder.build();
		for(int i = 1; i <= 4; i++) {
			e4.getStages().add(new E4Stage(e4, i));
		}
		e4.setChampion(new Champion(e4));
		BidoofUnleashed.getInstance().getService().addE4(e4.initialize());
		src.sendMessages(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_ADD_ELITE_FOUR_SUCCESS, null, null));

		return CommandResult.success();
	}

	@AllArgsConstructor
	public enum EnumEliteFourDetails {
		BADGE_TYPE(Lists.newArrayList("badge-type", "bt"), (src, builder, s) -> {
			if(!Sponge.getRegistry().getType(ItemType.class, s).isPresent()) {
				src.sendMessages(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), TextColors.GRAY, "Invalid item type for badge, ignoring input..."));
			}
			builder.badgeType(s);
		}),
		BADGE_NAME(Lists.newArrayList("badge-name", "bn"), (src, builder, s) -> builder.badgeName(s)),
		COOLDOWN(Lists.newArrayList("cooldown", "c", "wait"), (src, builder, s) -> {
			try {
				builder.cooldown(Integer.parseInt(s));
			} catch (Exception e) {
				src.sendMessages(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), TextColors.GRAY, "Invalid input for cooldown, ignoring..."));
			}
		}),
		;

		public List<String> identifiers;
		public TriConsumer<CommandSource, EliteFour.E4Builder, String> application;
	}
}
