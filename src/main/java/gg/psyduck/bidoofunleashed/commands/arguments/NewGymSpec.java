package gg.psyduck.bidoofunleashed.commands.arguments;

import com.google.common.collect.Lists;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BattlableBuilder;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.extensions.TriConsumer;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import lombok.AllArgsConstructor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.regex.Pattern;

public class NewGymSpec {

	public static Gym parseValue(CommandSource src, String name, String args) throws Exception {
		String[] split = args == null ? new String[0] : args.split(" ");
		Pattern pattern = Pattern.compile("(?<identifier>[a-zA-Z]+):(?<value>[a-zA-Z]+(:[a-zA-Z]+)*)");
		BattlableBuilder<Gym> builder = Gym.builder().name(name);

		for(String mapping : split) {
			java.util.regex.Matcher matcher = pattern.matcher(mapping);
			if(matcher.matches()) {
				String identifier = matcher.group("identifier");
				String value = matcher.group("value");

				for(EnumGymDetails detail : EnumGymDetails.values()) {
					if(detail == EnumGymDetails.NAME) continue;

					if(detail.identifiers.stream().anyMatch(alias -> alias.equalsIgnoreCase(identifier))) {
						detail.application.accept(src, builder, value);
						break;
					}
				}
			}
		}
		return builder.build();
	}

	@AllArgsConstructor
	public enum EnumGymDetails {
		NAME(Lists.newArrayList("name", "n"), (src, builder, s) -> builder.name(s)),
		BADGE_TYPE(Lists.newArrayList("badge-type", "bt"), (src, builder, s) -> {
			if(!Sponge.getRegistry().getType(ItemType.class, s).isPresent()) {
				src.sendMessages(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), TextColors.GRAY, "Invalid item type for badge, ignoring input..."));
			}
			builder.badgeType(s);
		}),
		BADGE_NAME(Lists.newArrayList("badge-name", "bn"), (src, builder, s) -> builder.badgeName(s)),
		LEVEL_CAP_INITIAL(Lists.newArrayList("level-cap-initial", "lvl-cap-init", "li"), (src, builder, s) -> {
			try {
				builder.levelCap(EnumBattleType.First, Integer.parseInt(s));
			} catch (Exception e) {
				src.sendMessages(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), TextColors.GRAY, "Invalid input for level cap, ignoring..."));
			}
		}),
		LEVEL_CAP_REMATCH(Lists.newArrayList("level-cap-rematch", "lvl-cap-rematch", "lr"), (src, builder, s) -> {
			try {
				builder.levelCap(EnumBattleType.Rematch, Integer.parseInt(s));
			} catch (Exception e) {
				src.sendMessages(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), TextColors.GRAY, "Invalid input for level cap, ignoring..."));
			}
		}),
		COOLDOWN(Lists.newArrayList("cooldown", "c", "wait"), (src, builder, s) -> {
			try {
				builder.cooldown(Integer.parseInt(s));
			} catch (Exception e) {
				src.sendMessages(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), TextColors.GRAY, "Invalid input for cooldown, ignoring..."));
			}
		}),
		WEIGHT(Lists.newArrayList("weight", "w"), (src, builder, s) -> {
			try {
				builder.weight(Integer.parseInt(s));
			} catch (Exception e) {
				src.sendMessages(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), TextColors.GRAY, "Invalid input for weight, ignoring..."));
			}
		}),
		CATEGORY(Lists.newArrayList("category", "cat"), (src, builder, s) -> builder.category(s)),
		;

		public List<String> identifiers;
		public TriConsumer<CommandSource, BattlableBuilder<Gym>, String> application;
	}
}
