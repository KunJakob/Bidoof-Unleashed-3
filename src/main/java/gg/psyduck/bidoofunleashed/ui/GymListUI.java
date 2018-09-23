package gg.psyduck.bidoofunleashed.ui;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.gui.v2.Icon;
import com.nickimpact.impactor.gui.v2.Layout;
import com.nickimpact.impactor.gui.v2.Page;
import com.nickimpact.impactor.gui.v2.PageDisplayable;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GymListUI implements PageDisplayable {

	private Page page;
	private Player player;

	public GymListUI(Player player) {
		this.player = player;
		this.page = this.build().define(this.gymIcons(), InventoryDimension.of(7, 3), 1, 1);
	}

	@Override
	public Page getDisplay() {
		return this.page;
	}

	private Page build() {
		Page.Builder pb = Page.builder();
		pb.property(InventoryTitle.of(Text.of(TextColors.DARK_AQUA, "Gym List")));
		pb.layout(this.design());
		pb.previous(Icon.from(
				ItemStack.builder()
						.itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_left").orElse(ItemTypes.BARRIER))
						.build()
				),
				48
		);
		pb.current(Icon.from(
				ItemStack.builder()
						.itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_monitor").orElse(ItemTypes.BARRIER))
						.build()
				),
				49
		);
		pb.next(Icon.from(
				ItemStack.builder()
						.itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_right").orElse(ItemTypes.BARRIER))
						.build()
				),
				50
		);

		return pb.build(BidoofUnleashed.getInstance());
	}

	private Layout design() {
		Layout.Builder lb = Layout.builder();
		lb.row(Icon.BORDER, 0).row(Icon.BORDER, 4);
		lb.column(Icon.BORDER, 0).column(Icon.BORDER, 8);

		return lb.build();
	}

	private List<Icon> gymIcons() {
		return BidoofUnleashed.getInstance().getDataRegistry().getGyms().stream().filter(BU3Battlable::isReady).map(gym -> {
			Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
			tokens.put("bu3_gym", src -> Optional.of(Text.of(gym.getName())));
			tokens.put("bu3_gym_stage", src -> Optional.of(Text.of(gym.getBattleType((Player) src).name())));
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
			int npcs = gym.getNpcs();
			if(npcs > 0) {
				lore.add(Text.of(TextColors.YELLOW, "NPCs x", npcs));
			}

			UserStorageService service = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
			for(UUID uuid : gym.getLeaders()) {
				service.get(uuid).ifPresent(user -> lore.add(Text.of(TextColors.AQUA, user.getName())));
			}

			ItemStack rep = ItemStack.builder()
					.itemType(Sponge.getRegistry().getType(ItemType.class, gym.getBadge().getItemType()).orElse(ItemTypes.BARRIER))
					.add(Keys.DISPLAY_NAME, MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.UI_GYM_ICON_TITLE, tokens, null))
					.add(Keys.ITEM_LORE, lore)
					.build();
			Icon icon = Icon.from(rep);
			icon.addListener(clickable -> {
				if(gym.canAccess(player)) {
					this.page.close(player);
					this.player.setLocationAndRotationSafely(
							new Location<>(
									Sponge.getServer().getWorld(gym.getArena().getSpectators().getWorld()).orElseThrow(() -> new RuntimeException("Unable to find the target world")),
									gym.getArena().getSpectators().getPosition()
							),
							gym.getArena().getSpectators().getRotation());
				}
			});

			return icon;
		}).collect(Collectors.toList());
	}
}
