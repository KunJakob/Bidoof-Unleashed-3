package gg.psyduck.bidoofunleashed.ui;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.gui.v2.Icon;
import com.nickimpact.impactor.gui.v2.Layout;
import com.nickimpact.impactor.gui.v2.Page;
import com.nickimpact.impactor.gui.v2.PageDisplayable;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import org.spongepowered.api.Sponge;
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
import org.spongepowered.api.text.serializer.TextSerializers;

import java.text.SimpleDateFormat;
import java.util.List;

public class BadgeCaseUI implements PageDisplayable {

	private Player player;
	private Page display;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy - hh:mm aaa");

	public BadgeCaseUI(Player player) {
		this.player = player;
		this.display = Page.builder()
				.property(InventoryTitle.of(Text.of(TextColors.DARK_AQUA, "Your Badge Case")))
				.layout(this.design())
				.previous(Icon.from(
						ItemStack.builder()
								.itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_left").orElse(ItemTypes.BARRIER))
								.build()
						),
						48
				)
				.current(Icon.from(
						ItemStack.builder()
								.itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_monitor").orElse(ItemTypes.BARRIER))
								.build()
						),
						49
				)
				.next(Icon.from(
						ItemStack.builder()
								.itemType(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trade_holder_right").orElse(ItemTypes.BARRIER))
								.build()
						),
						50
				)
				.build(BidoofUnleashed.getInstance())
				.define(this.badgeIcons(), InventoryDimension.of(7, 3), 1, 1);
	}

	@Override
	public Page getDisplay() {
		return this.display;
	}

	private Layout design() {
		Layout.Builder lb = Layout.builder();
		lb.row(Icon.BORDER, 0).row(Icon.BORDER, 4).column(Icon.BORDER, 0).column(Icon.BORDER, 8);
		lb.slots(Icon.BORDER, 46, 47, 51, 52);

		return lb.build();
	}

	private List<Icon> badgeIcons() {
		List<Icon> results = Lists.newArrayList();

		PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(this.player.getUniqueId());
		List<Badge> badges = pd.getBadges();
		UserStorageService service = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
		for(Badge badge : badges) {
			List<Text> lore = Lists.newArrayList(
					Text.of(TextColors.GRAY, "Earned: ", TextColors.YELLOW, sdf.format(badge.getObtained())),
					Text.of(TextColors.GRAY, "Leader: ", TextColors.YELLOW, badge.getLeader() == null ? badge.getNpcName() : service.get(badge.getLeader()).get().getName())
			);

			ItemStack rep = ItemStack.builder()
					.itemType(Sponge.getRegistry().getType(ItemType.class, badge.getItemType()).orElse(ItemTypes.BARRIER))
					.add(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_AQUA, badge.getName(), " Badge"))
					.add(Keys.ITEM_LORE, lore)
					.build();

			results.add(new Icon(rep));
		}

		return results;
	}
}
