package gg.psyduck.bidoofunleashed.ui.gympools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.gui.v2.Icon;
import com.nickimpact.impactor.gui.v2.Layout;
import com.nickimpact.impactor.gui.v2.Page;
import com.nickimpact.impactor.gui.v2.PageDisplayable;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.spec.BU3PokemonSpec;
import gg.psyduck.bidoofunleashed.config.ConfigKeys;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.battles.gyms.Gym;
import gg.psyduck.bidoofunleashed.ui.icons.PixelmonIcons;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.EventContext;
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

	public GymPoolUI(Player leader, Player challenger, Gym gym) {
		this(leader, challenger, gym, Lists.newArrayList());
	}

	GymPoolUI(Player leader, Player challenger, Gym gym, List<BU3PokemonSpec> chosenTeam) {
		this.leader = leader;
		this.challenger = challenger;
		this.focus = gym;
		this.type = this.focus.getBattleType(challenger);
		this.chosenTeam = chosenTeam;
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
			int index = 45;
			for(BU3PokemonSpec spec : this.chosenTeam) {
				ItemStack picture = PixelmonIcons.createPicture(EnumPokemon.getFromNameAnyCase(spec.name), spec.shiny != null ? spec.shiny : false, spec.form != null ? spec.form : -1);
				picture = PixelmonIcons.applySpecDetails(picture, spec);
				picture.get(Keys.ITEM_LORE).ifPresent(lore -> lore.addAll(Lists.newArrayList(
						Text.EMPTY,
						Text.of(TextColors.RED, "Click to Remove!"))
				));
				lb.slot(Icon.from(picture), index++);
			}

			Map<String, Object> variables = Maps.newHashMap();
			variables.put("bu3_wait", BidoofUnleashed.getInstance().getConfig().get(ConfigKeys.TELEPORT_WAIT));

			if(this.chosenTeam.size() > this.focus.getBattleSettings(type).getMinPokemon()) {
				ItemStack confirm = ItemStack.builder().itemType(ItemTypes.DYE).add(Keys.DYE_COLOR, DyeColors.LIME).build();
				Icon conf = Icon.from(confirm);
				conf.addListener(clickable -> {
					// Close display
					this.display.close(leader);

					// Set leader team
					Optional<PlayerStorage> optStorage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) leader);
					optStorage.ifPresent(storage -> {
						storage.partyPokemon = new NBTTagCompound[6];
						for(BU3PokemonSpec spec : this.chosenTeam) {
							EntityPixelmon pokemon = spec.create((World) leader.getWorld());
							storage.addToParty(pokemon);
						}
						storage.sendUpdatedList();
					});

					challenger.sendMessage(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.MISC_CHALLENGE_BEGINNING, null, variables));
					leader.sendMessage(MessageUtils.fetchAndParseMsg(leader, MsgConfigKeys.MISC_CHALLENGE_BEGINNING_LEADER_SELECTED, null, variables));
					Sponge.getScheduler().createTaskBuilder().execute(() -> this.focus.startBattle(leader, challenger)).delay(10, TimeUnit.SECONDS).submit(BidoofUnleashed.getInstance());
				});
				lb.slot(conf, 17);
			}

			ItemStack random = ItemStack.builder().itemType(ItemTypes.DYE).add(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).build();
			Icon rng = Icon.from(random);
			rng.addListener(clickable -> {
				this.display.close(leader);

				// Randomly select team
				Random x = new Random();

				int min = this.focus.getBattleSettings(type).getMinPokemon();
				int max = this.focus.getBattleSettings(type).getMaxPokemon();
				int size = Math.min(1, Math.max(6, x.nextInt(max - min) + min));

				List<BU3PokemonSpec> team = Lists.newArrayList();

				List<BU3PokemonSpec> specs = this.focus.getBattleSettings(type).getPool().getTeam();
				if(specs.size() > 0) {
					for (int i = 0; i < size; i++) {
						BU3PokemonSpec spec = specs.get(x.nextInt(specs.size()));
						BU3PokemonSpec y = spec;
						while (team.stream().anyMatch(s -> s.name.equalsIgnoreCase(y.name))) {
							spec = specs.get(x.nextInt(specs.size()));
						}

						team.add(spec);
					}
				} else {
					team.add(new BU3PokemonSpec("bidoof lvl:5"));
				}

				// Set leader team
				Optional<PlayerStorage> optStorage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) leader);
				optStorage.ifPresent(storage -> {
					storage.partyPokemon = new NBTTagCompound[6];
					for(BU3PokemonSpec spec : team) {
						EntityPixelmon pokemon = spec.create((World) leader.getWorld());
						storage.addToParty(pokemon);
					}
					storage.sendUpdatedList();
				});

				challenger.sendMessage(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.MISC_CHALLENGE_BEGINNING, null, variables));
				leader.sendMessage(MessageUtils.fetchAndParseMsg(leader, MsgConfigKeys.MISC_CHALLENGE_BEGINNING_LEADER_RANDOM, null, variables));
				Sponge.getScheduler().createTaskBuilder().execute(() -> this.focus.startBattle(leader, challenger)).delay(10, TimeUnit.SECONDS).submit(BidoofUnleashed.getInstance());
			});
			lb.slot(rng, 35);
		}

		return lb.build();
	}

	private List<Icon> forge() {
		List<BU3PokemonSpec> specs = this.focus.getBattleSettings(type).getPool().getTeam();
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
			icon.addListener(clickable -> {
				if(this.chosenTeam.size() < this.focus.getBattleSettings(type).getMaxPokemon()) {
					this.display.close(leader);
					new SpeciesUI(leader, challenger, focus, chosenTeam, entry.getKey()).open(leader, 1);
				}
			});

			results.add(icon);
		}

		return results;
	}
}
