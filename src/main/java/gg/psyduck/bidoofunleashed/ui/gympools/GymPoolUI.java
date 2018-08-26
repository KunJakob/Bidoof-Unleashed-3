package gg.psyduck.bidoofunleashed.ui.gympools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.gui.v2.Icon;
import com.nickimpact.impactor.gui.v2.Layout;
import com.nickimpact.impactor.gui.v2.Page;
import com.nickimpact.impactor.gui.v2.PageDisplayable;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.spec.BU3PokemonSpec;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.ui.icons.PixelmonIcons;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class GymPoolUI implements PageDisplayable {

	private Player player;
	private Gym focus;
	private Page display;

	public GymPoolUI(Player player, Gym gym) {
		this.player = player;
		this.focus = gym;
		this.display = this.build().define(this.forge(), InventoryDimension.of(7, 3), 1, 1);
	}

	@Override
	public Page getDisplay() {
		return this.display;
	}

	private Page build() {
		Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
		tokens.put("bu3_gym", s -> Optional.of(Text.of(focus.getName())));
		Page.Builder pb = Page.builder();
		pb.layout(this.layout());
		pb.property(InventoryTitle.of(MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.GYM_TITLE, tokens, null)));

		if(this.focus.getPool().getTeam().size() > 21) {
			pb.previous(Icon.from(ItemStack.builder()
					.itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_left").orElse(ItemTypes.BARRIER))
					.build()),
					48
			);
			pb.current(Icon.from(ItemStack.builder()
							.itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_monitor").orElse(ItemTypes.BARRIER))
							.build()),
					49
			);
			pb.next(Icon.from(ItemStack.builder()
							.itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_right").orElse(ItemTypes.BARRIER))
							.build()),
					50
			);
		}

		return pb.build(BidoofUnleashed.getInstance());
	}

	private Layout layout() {
		Layout.Builder lb = Layout.builder();
		lb.row(Icon.BORDER, 0).row(Icon.BORDER, 4);
		lb.column(Icon.BORDER, 0).column(Icon.BORDER, 8);
		if(this.focus.getPool().getTeam().isEmpty()) {
			lb.slot(Icon.from(ItemStack.builder()
					.itemType(ItemTypes.STAINED_GLASS_PANE)
					.add(Keys.DYE_COLOR, DyeColors.RED)
					.add(Keys.DISPLAY_NAME, MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.EMPTY_GYM_POOL_TITLE, null, null))
					.add(Keys.ITEM_LORE, MessageUtils.fetchAndParseMsgs(player, MsgConfigKeys.EMPTY_GYM_POOL_LORE, null, null))
					.build()), 22);
		}

		return lb.build();
	}

	private List<Icon> forge() {
		List<BU3PokemonSpec> specs = this.focus.getPool().getTeam();
		Map<EnumPokemon, Map<Byte, Integer>> speciesIndex = Maps.newHashMap();
		List<Icon> results = Lists.newArrayList();
		for(BU3PokemonSpec spec : specs) {
			EnumPokemon species = EnumPokemon.getFromNameAnyCase(spec.name);
			if(speciesIndex.containsKey(species)) {
				Map<Byte, Integer> formToAmount = speciesIndex.get(species);
				byte form = spec.form != null ? spec.form : 0;
				if(formToAmount.containsKey(form)) {
					formToAmount.replace(form, formToAmount.get(form) + 1);
				} else {
					formToAmount.put(form, 1);
				}

				speciesIndex.replace(species, formToAmount);
			} else {
				Map<Byte, Integer> formToAmount = Maps.newHashMap();
				byte form = spec.form != null ? spec.form : 0;
				formToAmount.put(form, 1);
				speciesIndex.put(species, formToAmount);
			}
		}

		for(Map.Entry<EnumPokemon, Map<Byte, Integer>> entry : speciesIndex.entrySet()) {
			Icon icon = PixelmonIcons.getPicture(entry.getKey(), false, -1);
			icon.getDisplay().offer(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_AQUA, entry.getKey().name, " Sets"));

			List<Text> lore = Lists.newArrayList();
			lore.add(Text.of(TextColors.GREEN, "Form Index:"));
			for(Map.Entry<Byte, Integer> fEntry : entry.getValue().entrySet()) {
				String fName = entry.getKey().getFormEnum(fEntry.getKey()).getFormSuffix().substring(1);
				lore.add(Text.of(TextColors.GRAY, fName.substring(0, 1).toUpperCase() + fName.substring(1) + ": ", TextColors.YELLOW, fEntry.getValue()));
			}
			icon.getDisplay().offer(Keys.ITEM_LORE, lore);

			results.add(icon);
		}

		return results;
	}
}
