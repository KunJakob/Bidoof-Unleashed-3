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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

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
				.layout(Layout.builder().border().build())
				.build(BidoofUnleashed.getInstance())
				.define(this.badgeIcons(), InventoryDimension.of(7, 4), 1, 1);
	}

	@Override
	public Page getDisplay() {
		return this.display;
	}

	private List<Icon> badgeIcons() {
		List<Icon> results = Lists.newArrayList();

		PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(this.player.getUniqueId());
		List<Badge> badges = pd.getBadges();
		for(Badge badge : badges) {
			List<Text> lore = Lists.newArrayList(
					Text.of(TextColors.GRAY, "Earned: ", TextColors.YELLOW, sdf.format(badge.getObtained())),
					Text.of(TextColors.GRAY, "Leader: ", TextColors.YELLOW, badge.getLeader()),
					Text.EMPTY,
					Text.of(TextColors.GRAY, "Team:")
			);
			for(String member : badge.getTeam()) {
				lore.add(Text.of(TextColors.YELLOW, member));
			}

			ItemStack rep = ItemStack.builder()
					.itemType(Sponge.getRegistry().getType(ItemType.class, badge.getBadge()).orElse(ItemTypes.BARRIER))
					.add(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_AQUA, badge.getName()))
					.add(Keys.ITEM_LORE, lore)
					.build();

			results.add(new Icon(rep));
		}

		return results;
	}
}
