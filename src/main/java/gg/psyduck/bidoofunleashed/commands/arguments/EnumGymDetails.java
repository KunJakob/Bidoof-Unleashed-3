package gg.psyduck.bidoofunleashed.commands.arguments;

import com.google.common.collect.Lists;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
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

@AllArgsConstructor
public enum EnumGymDetails {
	ID(Lists.newArrayList("id"), (src, builder, s) -> builder.id(s)),
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
	public TriConsumer<CommandSource, Gym.Builder, String> application;
}
