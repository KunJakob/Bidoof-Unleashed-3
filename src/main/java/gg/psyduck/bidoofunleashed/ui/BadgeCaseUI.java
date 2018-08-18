package gg.psyduck.bidoofunleashed.ui;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.gui.v2.Icon;
import com.nickimpact.impactor.gui.v2.Layout;
import com.nickimpact.impactor.gui.v2.Page;
import com.nickimpact.impactor.gui.v2.PageDisplayable;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public class BadgeCaseUI implements PageDisplayable {

	private Player player;
	private Page display;

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
		return Lists.newArrayList();
	}
}
