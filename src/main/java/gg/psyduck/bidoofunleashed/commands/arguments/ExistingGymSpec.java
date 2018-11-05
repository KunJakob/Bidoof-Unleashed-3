package gg.psyduck.bidoofunleashed.commands.arguments;

import com.google.common.collect.Lists;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
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

public class ExistingGymSpec {

	public static void parseValue(CommandSource src, Gym gym, String args) throws Exception {
		String[] split = args.split(" ");
		Pattern pattern = Pattern.compile("(?<identifier>[a-zA-Z]+):(?<value>[a-zA-Z0-9]+(:[a-zA-Z0-9]+)*)");

		for(String mapping : split) {
			java.util.regex.Matcher matcher = pattern.matcher(mapping);
			if(matcher.matches()) {
				String identifier = matcher.group("identifier");
				String value = matcher.group("value");

				boolean found = false;
				for(EnumGymDetails detail : EnumGymDetails.values()) {
					if(detail.identifiers.stream().anyMatch(alias -> alias.equalsIgnoreCase(identifier))) {
						found = true;
						detail.application.accept(src, gym, value);
						break;
					}
				}
				if(!found) {
					src.sendMessages(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), TextColors.GRAY, "Unrecognized flag: ", TextColors.YELLOW, identifier));
				}
			}
		}
	}

	@AllArgsConstructor
	public enum EnumGymDetails {
		NAME(Lists.newArrayList("name", "n"), (src, gym, s) -> gym.setName(s)),
		BADGE_TYPE(Lists.newArrayList("badge-type", "bt"), (src, gym, s) -> {
			if(!Sponge.getRegistry().getType(ItemType.class, s).isPresent()) {
				src.sendMessages(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), TextColors.GRAY, "Invalid item type for badge, ignoring input..."));
			}
			gym.setBadge(gym.getBadge().itemType(s));
		}),
		BADGE_NAME(Lists.newArrayList("badge-name", "bn"), (src, gym, s) -> gym.setBadge(gym.getBadge().name(s))),
		LEVEL_CAP_INITIAL(Lists.newArrayList("level-cap-initial", "lvl-cap-init", "li"), (src, gym, s) -> {
			try {
				gym.getBattleSettings(EnumBattleType.First).lvlCap(Integer.parseInt(s));
			} catch (Exception e) {
				src.sendMessages(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), TextColors.GRAY, "Invalid input for level cap, ignoring..."));
			}
		}),
		LEVEL_CAP_REMATCH(Lists.newArrayList("level-cap-rematch", "lvl-cap-rematch", "lr"), (src, gym, s) -> {
			try {
				gym.getBattleSettings(EnumBattleType.Rematch).lvlCap(Integer.parseInt(s));
			} catch (Exception e) {
				src.sendMessages(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), TextColors.GRAY, "Invalid input for level cap, ignoring..."));
			}
		}),
		COOLDOWN(Lists.newArrayList("cooldown", "c", "wait"), (src, gym, s) -> {
			try {
				gym.setCooldown(Integer.parseInt(s));
			} catch (Exception e) {
				src.sendMessages(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), TextColors.GRAY, "Invalid input for cooldown, ignoring..."));
			}
		}),
		WEIGHT(Lists.newArrayList("weight", "w"), (src, gym, s) -> {
			try {
				gym.setWeight(Integer.parseInt(s));
			} catch (Exception e) {
				src.sendMessages(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), TextColors.GRAY, "Invalid input for weight, ignoring..."));
			}
		}),
		CATEGORY(Lists.newArrayList("category", "cat"), (src, gym, s) -> gym.setCategory(new Category(s))),
		;

		public List<String> identifiers;
		public TriConsumer<CommandSource, Gym, String> application;
	}
}
