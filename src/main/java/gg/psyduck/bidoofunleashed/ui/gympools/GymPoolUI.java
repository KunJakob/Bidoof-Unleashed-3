package gg.psyduck.bidoofunleashed.ui.gympools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.gui.v2.Icon;
import com.nickimpact.impactor.gui.v2.Layout;
import com.nickimpact.impactor.gui.v2.Page;
import com.nickimpact.impactor.gui.v2.PageDisplayable;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.forms.EnumNoForm;
import com.pixelmonmod.pixelmon.enums.forms.EnumUnown;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.exceptions.BattleStartException;
import gg.psyduck.bidoofunleashed.api.pixelmon.specs.BU3PokemonSpec;
import gg.psyduck.bidoofunleashed.config.ConfigKeys;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.ui.icons.PixelmonIcons;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import gg.psyduck.bidoofunleashed.utils.TeamSelectors;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
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
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class GymPoolUI implements PageDisplayable {

	private Player leader;
	private Player challenger;
	private Gym focus;
	private EnumBattleType type;
	private List<BU3PokemonSpec> chosenTeam;
	private Page display;

	/** States whether the pool should have its click events for pokemon selection, as well as it's close action */
	private boolean viewing;

	public GymPoolUI(Player leader, Player challenger, Gym gym, boolean viewing) {
		this(leader, challenger, gym, Lists.newArrayList(), viewing);
	}

	GymPoolUI(Player leader, Player challenger, Gym gym, List<BU3PokemonSpec> chosenTeam, boolean viewing) {
		this.leader = leader;
		this.challenger = challenger;
		this.focus = gym;
		this.type = challenger != null ? this.focus.getBattleType(challenger) : EnumBattleType.First;
		this.chosenTeam = chosenTeam;
		this.display = this.build().define(this.forge(), InventoryDimension.of(7, 3), 1, 1);
		this.viewing = viewing;

		if(!viewing) {
			this.display.getViews().forEach(ui -> ui.setCloseAction((event, player) -> {
				if (!event.getCause().getContext().equals(EventContext.empty())) {
					ui.open(player);
				}
			}));
		}
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
		pb.property(InventoryTitle.of(MessageUtils.fetchAndParseMsg(leader, MsgConfigKeys.GYM_TITLE, tokens, null)));

		if(this.focus.getBattleSettings(type).getPool().getTeam().size() > 21) {
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
		lb.column(Icon.BORDER, 0).column(Icon.BORDER, 8).slots(Icon.EMPTY, 17, 26, 35, 45, 53).slots(Icon.BORDER, 16, 25, 34);
		lb.slot(Icon.BORDER, 51);
		if(this.focus.getBattleSettings(type).getPool().getTeam().isEmpty()) {
			lb.slot(Icon.from(ItemStack.builder()
					.itemType(ItemTypes.STAINED_GLASS_PANE)
					.add(Keys.DYE_COLOR, DyeColors.RED)
					.add(Keys.DISPLAY_NAME, MessageUtils.fetchAndParseMsg(leader, MsgConfigKeys.EMPTY_GYM_POOL_TITLE, null, null))
					.add(Keys.ITEM_LORE, MessageUtils.fetchAndParseMsgs(leader, MsgConfigKeys.EMPTY_GYM_POOL_LORE, null, null))
					.build()), 22);
		} else {
			if(!viewing) {
				int index = 45;
				for (BU3PokemonSpec spec : this.chosenTeam) {
					ItemStack picture = PixelmonIcons.createPicture(EnumSpecies.getFromNameAnyCase(spec.name), spec.shiny != null ? spec.shiny : false, spec.form != null ? spec.form : -1);
					PixelmonIcons.applySpecDetails(picture, spec);
					picture.get(Keys.ITEM_LORE).ifPresent(lore -> lore.addAll(Lists.newArrayList(
							Text.EMPTY,
							Text.of(TextColors.RED, "Click to Remove!"))
					));
					lb.slot(Icon.from(picture), index++);
				}
			}

			Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
			tokens.put("bu3_wait", src -> Optional.of(Text.of(BidoofUnleashed.getInstance().getConfig().get(ConfigKeys.TELEPORT_WAIT))));

			if(!viewing) {
				if (this.chosenTeam.size() >= this.focus.getBattleSettings(type).getMinPokemon() && this.chosenTeam.size() <= this.focus.getBattleSettings(type).getMaxPokemon()) {
					ItemStack confirm = ItemStack.builder()
							.itemType(ItemTypes.DYE)
							.add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "Begin Battle!"))
							.add(Keys.DYE_COLOR, DyeColors.LIME)
							.build();
					Icon conf = Icon.from(confirm);
					conf.addListener(clickable -> {
						// Close display
						this.display.close(leader);

						challenger.sendMessage(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.MISC_CHALLENGE_BEGINNING, tokens, null));
						leader.sendMessage(MessageUtils.fetchAndParseMsg(leader, MsgConfigKeys.MISC_CHALLENGE_BEGINNING_LEADER_SELECTED, tokens, null));
						Sponge.getScheduler().createTaskBuilder().execute(() -> {
							try {
								this.focus.startBattle(leader, challenger, chosenTeam);
							} catch (BattleStartException e) {
								challenger.sendMessage(e.getReason());
								leader.sendMessage(e.getReason());
							}
						}).delay(10, TimeUnit.SECONDS).submit(BidoofUnleashed.getInstance());
					});
					lb.slot(conf, 17);
				} else {
					ItemStack confirm = ItemStack.builder()
							.itemType(ItemTypes.DYE)
							.add(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, "Select your team..."))
							.add(Keys.DYE_COLOR, DyeColors.GRAY)
							.build();
					Icon conf = Icon.from(confirm);
					lb.slots(conf, 17);
				}
			}

			if(!viewing) {
				lb.slot(addFavoriteIcon(), 26);
			}

			if(!viewing) {
				ItemStack random = ItemStack.builder()
						.itemType(ItemTypes.DYE)
						.add(Keys.DISPLAY_NAME, MessageUtils.fetchAndParseMsg(leader, MsgConfigKeys.UI_GYM_POOL_RANDOM_TEAM, null, null))
						.add(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE)
						.build();
				Icon rng = Icon.from(random);
				rng.addListener(clickable -> {
					this.display.close(leader);
					List<BU3PokemonSpec> team = TeamSelectors.randomized(this.focus, this.challenger);
					challenger.sendMessage(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.MISC_CHALLENGE_BEGINNING, tokens, null));
					leader.sendMessage(MessageUtils.fetchAndParseMsg(leader, MsgConfigKeys.MISC_CHALLENGE_BEGINNING_LEADER_RANDOM, tokens, null));
					Sponge.getScheduler().createTaskBuilder().execute(() -> {
						try {
							this.focus.startBattle(leader, challenger, team);
						} catch (BattleStartException e) {
							challenger.sendMessage(e.getReason());
							leader.sendMessage(e.getReason());
						}
					}).delay(10, TimeUnit.SECONDS).submit(BidoofUnleashed.getInstance());
				});
				lb.slot(rng, 35);
			}
		}

		return lb.build();
	}

	private List<Icon> forge() {
		List<BU3PokemonSpec> specs = this.focus.getBattleSettings(type).getPool().getTeam();
		Map<EnumSpecies, Map<Byte, Integer>> speciesIndex = Maps.newHashMap();
		List<Icon> results = Lists.newArrayList();
		for(BU3PokemonSpec spec : specs) {
			EnumSpecies species = EnumSpecies.getFromNameAnyCase(spec.name);
			if(speciesIndex.containsKey(species)) {
				Map<Byte, Integer> formToAmount = speciesIndex.get(species);
				byte form = spec.form != null ? spec.form : -1;
				if(formToAmount.containsKey(form)) {
					formToAmount.replace(form, formToAmount.get(form) + 1);
				} else {
					formToAmount.put(form, 1);
				}

				speciesIndex.replace(species, formToAmount);
			} else {
				Map<Byte, Integer> formToAmount = Maps.newHashMap();
				byte form = spec.form != null ? spec.form : -1;
				formToAmount.put(form, 1);
				speciesIndex.put(species, formToAmount);
			}
		}

		for(Map.Entry<EnumSpecies, Map<Byte, Integer>> entry : speciesIndex.entrySet()) {
			Icon icon = PixelmonIcons.getPicture(entry.getKey(), false, -1);
			icon.getDisplay().offer(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_AQUA, entry.getKey().name, " Sets"));

			List<Text> lore = Lists.newArrayList();
			lore.add(Text.of(TextColors.GREEN, "Form Index:"));
			for(Map.Entry<Byte, Integer> fEntry : entry.getValue().entrySet()) {
				IEnumForm form = entry.getKey().getFormEnum(fEntry.getKey());
				String fName = form == EnumNoForm.NoForm ? "None" : form instanceof EnumUnown ? form.getSpriteSuffix().substring(1) : form.getFormSuffix().substring(1);
				lore.add(Text.of(TextColors.GRAY, fName.substring(0, 1).toUpperCase() + fName.substring(1) + ": ", TextColors.YELLOW, fEntry.getValue()));
			}
			icon.getDisplay().offer(Keys.ITEM_LORE, lore);

			if(!viewing) {
				icon.addListener(clickable -> {
					if (this.chosenTeam.size() < this.focus.getBattleSettings(type).getMaxPokemon()) {
						this.display.close(leader);
						new SpeciesUI(leader, challenger, focus, chosenTeam, entry.getKey()).open(leader, 1);
					}
				});
			}

			results.add(icon);
		}

		return results;
	}

	private Icon addFavoriteIcon() {
		PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(this.leader.getUniqueId());

		ItemStack favorite = ItemStack.builder()
				.itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:gs_ball").orElse(ItemTypes.BARRIER))
				.add(Keys.DISPLAY_NAME, pd.getFavoriteTeams().get(this.focus.getName()) != null || this.chosenTeam.size() > 0 ? Text.of(TextColors.YELLOW, "Use/Set your Favorite Team") : Text.of(TextColors.GRAY, "Select a Team..."))
				.add(Keys.ITEM_LORE, pd.getFavoriteTeams().get(this.focus.getName()) != null || this.chosenTeam.size() > 0 ?
						Lists.newArrayList(
								Text.of(TextColors.GREEN, "Left Click: ", TextColors.GRAY, "Use Favorite"),
								Text.of(TextColors.GREEN, "Right Click: ", TextColors.GRAY, "Set Favorite")
						) :
						Lists.newArrayList()
				)
				.build();

		Icon fav = Icon.from(favorite);
		fav.addListener(clickable -> {
			if(clickable.getEvent() instanceof ClickInventoryEvent.Primary) {
				if(pd.getFavoriteTeams().get(this.focus.getName()) != null) {
					List<BU3PokemonSpec> team = pd.getFavoriteTeams().get(this.focus.getName());
					int index = 45;
					for (BU3PokemonSpec spec : team) {
						ItemStack picture = PixelmonIcons.createPicture(EnumSpecies.getFromNameAnyCase(spec.name), spec.shiny != null ? spec.shiny : false, spec.form != null ? spec.form : -1);
						PixelmonIcons.applySpecDetails(picture, spec);
						picture.get(Keys.ITEM_LORE).ifPresent(lore -> lore.addAll(Lists.newArrayList(
								Text.EMPTY,
								Text.of(TextColors.RED, "Click to Remove!"))
						));

						this.chosenTeam.add(spec);
						this.display.apply(Icon.from(picture), index++);
					}
				}
			} else {
				if(this.chosenTeam.size() > 0) {
					pd.getFavoriteTeams().put(this.focus.getName(), Lists.newArrayList(this.chosenTeam));
				}
			}
		});

		return fav;
	}
}
