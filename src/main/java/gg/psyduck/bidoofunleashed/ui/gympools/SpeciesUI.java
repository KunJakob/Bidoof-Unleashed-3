package gg.psyduck.bidoofunleashed.ui.gympools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.gui.v2.Icon;
import com.nickimpact.impactor.gui.v2.Layout;
import com.nickimpact.impactor.gui.v2.Page;
import com.nickimpact.impactor.gui.v2.PageDisplayable;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.spec.BU3PokemonSpec;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.battles.gyms.Gym;
import gg.psyduck.bidoofunleashed.ui.icons.PixelmonIcons;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

class SpeciesUI implements PageDisplayable {

	private Player leader;
	private Player challenger;
	private Gym focus;
	private List<BU3PokemonSpec> chosenTeam;
	private EnumPokemon species;
	private Page display;

	SpeciesUI(Player leader, Player challenger, Gym gym, List<BU3PokemonSpec> chosenTeam, EnumPokemon species) {
		this.leader = leader;
		this.challenger = challenger;
		this.focus = gym;
		this.chosenTeam = chosenTeam;
		this.species = species;
		this.display = this.build().define(this.forge(), InventoryDimension.of(7, 3), 1, 1);
		this.display.getViews().forEach(ui -> ui.setCloseAction((event, player) -> {
			if(!event.getCause().getContext().equals(EventContext.empty())) {
				ui.open(player);
			}
		}));
	}

	@Override
	public Page getDisplay() {
		return this.display;
	}

	private Page build() {
		Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
		tokens.put("bu3_pokemon", s -> Optional.of(Text.of(species.name)));
		Page.Builder pb = Page.builder();
		pb.layout(this.layout());
		pb.property(InventoryTitle.of(MessageUtils.fetchAndParseMsg(leader, MsgConfigKeys.SPECIES_TITLE, tokens, null)));

		if(this.focus.getBattleSettings(this.focus.getBattleType(challenger)).getPool().getTeam().size() > 21) {
			pb.previous(Icon.from(ItemStack.builder()
							.itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_left").orElse(ItemTypes.BARRIER))
							.build()),
					52
			);
			pb.next(Icon.from(ItemStack.builder()
							.itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_right").orElse(ItemTypes.BARRIER))
							.build()),
					53
			);
		}

		return pb.build(BidoofUnleashed.getInstance());
	}

	private Layout layout() {
		Layout.Builder lb = Layout.builder();
		lb.row(Icon.BORDER, 0).row(Icon.BORDER, 4);
		lb.column(Icon.BORDER, 0).column(Icon.BORDER, 8).slots(Icon.EMPTY, 45, 53);
		lb.slot(Icon.BORDER, 51);

		int index = 45;
		for(BU3PokemonSpec spec : this.chosenTeam) {
			ItemStack picture = PixelmonIcons.createPicture(EnumPokemon.getFromNameAnyCase(spec.name), spec.shiny != null ? spec.shiny : false, spec.form != null ? spec.form : -1);
			picture = PixelmonIcons.applySpecDetails(picture, spec);
			lb.slot(Icon.from(picture), index++);
		}

		return lb.build();
	}

	private List<Icon> forge() {
		List<BU3PokemonSpec> specs = this.focus.getBattleSettings(this.focus.getBattleType(challenger)).getPool().getTeam().stream().filter(spec -> spec.name.equalsIgnoreCase(this.species.name)).collect(Collectors.toList());
		List<Icon> results = Lists.newArrayList();
		for(BU3PokemonSpec spec : specs) {
			ItemStack picture = PixelmonIcons.createPicture(EnumPokemon.getFromNameAnyCase(spec.name), spec.shiny != null ? spec.shiny : false, spec.form != null ? spec.form : -1);
			picture = PixelmonIcons.applySpecDetails(picture, spec);

			Icon icon = Icon.from(picture);
			icon.addListener(clickable -> {
				this.chosenTeam.add(spec);
				this.display.close(leader);
				new GymPoolUI(leader, challenger, focus, this.chosenTeam).open(leader, 1);
			});
			results.add(icon);
		}

		return results;
	}
}
