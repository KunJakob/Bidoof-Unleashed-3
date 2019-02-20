package gg.psyduck.bidoofunleashed.ui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.nickimpact.impactor.gui.v2.Icon;
import com.nickimpact.impactor.gui.v2.Layout;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.api.battlables.BU3BattleBase;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.api.ui.CategoryPage;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.e4.E4Stage;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.BadgeReference;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GymListUI {

	private CategoryPage page;
	private Player player;

	public GymListUI(Player player) {
		this.player = player;
		this.page = this.build();
	}

	public void open(Player player) {
		page.open(player, 1);
	}

	private CategoryPage build() {
		CategoryPage.Builder builder = CategoryPage.builder();
		builder.layout(this.design());
		builder.previous(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_left").orElse(ItemTypes.BARRIER));
		builder.current(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_monitor").orElse(ItemTypes.BARRIER));
		builder.next(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_right").orElse(ItemTypes.BARRIER));

		Multimap<Category, BU3BattleBase> mapping = BidoofUnleashed.getInstance().getDataRegistry().getBattlables();
		for(Category category : mapping.keySet()) {
			builder = this.compose(builder, category, mapping.get(category));
		}

		return builder.build();
	}

	private Layout design() {
		Layout.Builder lb = Layout.builder();
		lb.row(Icon.BORDER, 0).row(Icon.BORDER, 4);
		lb.column(Icon.BORDER, 0).column(Icon.BORDER, 8);
		return lb.build();
	}

	private CategoryPage.Builder compose(CategoryPage.Builder builder, Category category, Collection<BU3BattleBase> mapping) {
		InventoryTitle title = InventoryTitle.of(Text.of(TextColors.DARK_AQUA, "Gym List ", TextColors.GRAY, "(", TextColors.RED, category.getId(), TextColors.GRAY, ")"));
		List<Gym> gyms = mapping.stream().filter(x -> x instanceof Gym).map(x -> (Gym) x).sorted(Comparator.comparing(BU3Battlable::getWeight)).collect(Collectors.toList());
		Positions positions = Positions.getFromAmount(gyms.size());
		Layout.Builder layout = Layout.builder().dimension(9, 6);

		Iterator<Gym> itr = gyms.iterator();
		for (int slot : positions.slots) {
			if (!itr.hasNext()) {
				break;
			}

			layout.slot(this.gymToIcon(itr.next()), slot);
		}

		Optional<EliteFour> optE4 = mapping.stream().filter(x -> x instanceof EliteFour).map(x -> (EliteFour) x).findAny();
		if(optE4.isPresent()) {
			layout = layout.slot(e4ToIcon(optE4.get()), 31);
		}
		return builder.addPage(layout.build(), Lists.newArrayList(title));
	}

	private Icon gymToIcon(Gym gym) {
		PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());

		Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
		tokens.put("bu3_gym", src -> Optional.of(Text.of(gym.getName())));
		tokens.put("bu3_gym_defeated", src -> Optional.of(Text.of(pd.getBadge(gym).map(BadgeReference::getTimesDefeated).orElse(0))));
		tokens.put("bu3_gym_status", src -> Optional.of(gym.isOpen() ? Text.of(TextColors.GREEN, "Open") : Text.of(TextColors.RED, "Closed")));
		tokens.put("bu3_level_cap", src -> Optional.of(gym.getBattleSettings(gym.getBattleType((Player) src)).getLvlCap() == PixelmonConfig.maxLevel ? Text.of("None") : Text.of(gym.getBattleSettings(gym.getBattleType((Player) src)).getLvlCap())));
		tokens.put("bu3_can_battle", src -> {
			try {
				for (Requirement requirement : gym.getBattleSettings(gym.getBattleType((Player) src)).getRequirements()) {
					if (!requirement.passes(gym, (Player) src)) {
						return Optional.of(Text.of(TextColors.RED, "No"));
					}
				}
			} catch (Exception e) {
				return Optional.of(Text.of(TextColors.RED, "Unknown"));
			}

			return Optional.of(Text.of(TextColors.GREEN, "Yes"));
		});

		List<Text> lore = MessageUtils.fetchAndParseMsgs(player, MsgConfigKeys.UI_GYM_ICON_LORE, tokens, null);
		ItemStack rep = ItemStack.builder()
				.itemType(Sponge.getRegistry().getType(ItemType.class, gym.getBadge().getItemType()).orElse(ItemTypes.BARRIER))
				.add(Keys.DISPLAY_NAME, MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.UI_GYM_ICON_TITLE, tokens, null))
				.add(Keys.ITEM_LORE, lore)
				.build();
		Icon icon = Icon.from(rep);
		icon.addListener(clickable -> {
			if(gym.canAccess(player)) {
				clickable.getPlayer().closeInventory();
				this.player.setLocationAndRotationSafely(
						new Location<>(
								Sponge.getServer().getWorld(gym.getArena().getSpectators().getWorld()).orElseThrow(() -> new RuntimeException("Unable to find the target world")),
								gym.getArena().getSpectators().getPosition()
						),
						gym.getArena().getSpectators().getRotation());
			}
		});

		return icon;
	}

	private Icon e4ToIcon(EliteFour e4) {
		PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());

		Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
		tokens.put("bu3_e4", src -> Optional.of(Text.of(e4.getName())));
		tokens.put("bu3_e4_defeated", src -> Optional.of(Text.of(pd.getBadge(e4).map(BadgeReference::getTimesDefeated).orElse(0))));
		tokens.put("bu3_e4_status", src -> Optional.of(e4.isOpen() ? Text.of(TextColors.GREEN, "Open") : Text.of(TextColors.RED, "Closed")));
		tokens.put("bu3_level_cap", src -> {
			int low = 0;
			int high = 0;

			for(E4Stage stage : e4.getStages()) {
				if(low == 0) {
					low = stage.getBattleSettings(stage.getBattleType((Player) src)).getLvlCap();
				} else {
					low = Math.min(low, stage.getBattleSettings(stage.getBattleType((Player) src)).getLvlCap());
				}

				if(high == 0) {
					high = stage.getBattleSettings(stage.getBattleType((Player) src)).getLvlCap();
				} else {
					high = Math.max(high, stage.getBattleSettings(stage.getBattleType((Player) src)).getLvlCap());
				}
			}

			return Optional.of(low == high ? Text.of(high) : Text.of(String.format("%d - %d", low, high)));
		});
		tokens.put("bu3_can_battle", src -> Optional.of(e4.canChallenge(player) ? Text.of(TextColors.GREEN, "Yes") : Text.of(TextColors.RED, "No")));
		tokens.put("bu3_e4_1", src -> Optional.of(Text.of(e4.getStages().get(0).getName())));
		tokens.put("bu3_e4_1_state", src -> Optional.of(pd.getDefeatedElite4()[0] ? Text.of(TextColors.GREEN, "\u2713") : Text.of(TextColors.RED, "\u2718")));
		tokens.put("bu3_e4_2", src -> Optional.of(Text.of(e4.getStages().get(1).getName())));
		tokens.put("bu3_e4_2_state", src -> Optional.of(pd.getDefeatedElite4()[1] ? Text.of(TextColors.GREEN, "\u2713") : Text.of(TextColors.RED, "\u2718")));
		tokens.put("bu3_e4_3", src -> Optional.of(Text.of(e4.getStages().get(2).getName())));
		tokens.put("bu3_e4_3_state", src -> Optional.of(pd.getDefeatedElite4()[2] ? Text.of(TextColors.GREEN, "\u2713") : Text.of(TextColors.RED, "\u2718")));
		tokens.put("bu3_e4_4", src -> Optional.of(Text.of(e4.getStages().get(3).getName())));
		tokens.put("bu3_e4_4_state", src -> Optional.of(pd.getDefeatedElite4()[3] ? Text.of(TextColors.GREEN, "\u2713") : Text.of(TextColors.RED, "\u2718")));
		tokens.put("bu3_e4_champion", src -> Optional.of(Text.of("Champion")));
		tokens.put("bu3_e4_champion_state", src -> Optional.of(Arrays.stream(pd.getDefeatedElite4()).allMatch(x -> x) ? Text.of(TextColors.GREEN, "Available") : Text.of(TextColors.RED, "Unavailable")));

		List<Text> lore = MessageUtils.fetchAndParseMsgs(player, MsgConfigKeys.UI_E4_ICON_LORE, tokens, null);
		ItemStack rep = ItemStack.builder()
				.itemType(Sponge.getRegistry().getType(ItemType.class, e4.getBadge().getItemType()).orElse(ItemTypes.BARRIER))
				.add(Keys.DISPLAY_NAME, MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.UI_E4_ICON_TITLE, tokens, null))
				.add(Keys.ITEM_LORE, lore)
				.build();
		Icon icon = Icon.from(rep);
		icon.addListener(clickable -> {
			if(e4.canAccess(player)) {
				clickable.getPlayer().closeInventory();
				this.player.setLocationAndRotationSafely(
						new Location<>(
								Sponge.getServer().getWorld(e4.getStages().get(0).getArena().getSpectators().getWorld()).orElseThrow(() -> new RuntimeException("Unable to find the target world")),
								e4.getStages().get(0).getArena().getSpectators().getPosition()
						),
						e4.getStages().get(0).getArena().getSpectators().getRotation());
			}
		});

		return icon;
	}

	private enum Positions {
		ZERO        ( 0),
		ONE         ( 1, 13),
		TWO         ( 2, 12, 14),
		THREE       ( 3, 12, 13, 14),
		FOUR        ( 4, 11, 12, 14, 15),
		FIVE        ( 5, 11, 12, 13, 14, 15),
		SIX         ( 6, 12, 13, 14, 21, 22, 23),
		SEVEN       ( 7, 11, 12, 13, 14, 15, 21, 23),
		EIGHT       ( 8, 11, 12, 13, 14, 21, 22, 23, 24),
		NINE        ( 9, 11, 12, 13, 14, 15, 20, 21, 23, 24),
		TEN         (10, 11, 12, 13, 14, 15, 20, 21, 22, 23, 24),
		ELEVEN      (11, 10, 11, 12, 13, 14, 15, 16, 20, 21, 23, 24),
		TWELVE      (12, 10, 11, 12, 13, 14, 15, 16, 20, 21, 22, 23, 24),
		THIRTEEN    (13, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 23, 24, 25),
		FOURTEEN    (14, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25),
		FIFTEEN     (15,  9, 10, 11, 12, 13, 14, 15, 16, 17, 19, 20, 21, 23, 24, 25),
		SIXTEEN     (16,  9, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 26),
		;

		private int amount;
		private int[] slots;

		private Positions(int amount, int... slots) {
			this.amount = amount;
			this.slots = slots;
		}

		public static Positions getFromAmount(int amount) {
			if(amount > 16) {
				return SIXTEEN;
			}

			for(Positions position : Positions.values()) {
				if(amount == position.amount) {
					return position;
				}
			}

			return ZERO;
		}
	}
}
