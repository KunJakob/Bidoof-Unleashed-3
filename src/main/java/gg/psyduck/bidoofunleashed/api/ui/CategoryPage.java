package gg.psyduck.bidoofunleashed.api.ui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.gui.v2.Icon;
import com.nickimpact.impactor.gui.v2.Layout;
import com.nickimpact.impactor.gui.v2.UI;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Map;

public class CategoryPage {

	private final List<UI> pages = Lists.newArrayList();
	private final Layout layout;

	private int numPages;

	private ItemType previous;
	private ItemType current;
	private ItemType next;

	private static final UI EMPTY = UI.builder().dimension(InventoryDimension.of(9, 5))
			.build(BidoofUnleashed.getInstance())
			.define(Layout.builder().border().row(Icon.BORDER, 4).slot(Icon.from(ItemStack.builder().itemType(ItemTypes.BARRIER).build()), 22).build());

	public CategoryPage(Builder builder) {
		this.layout = builder.layout;
		this.previous = builder.previous;
		this.current = builder.current;
		this.next = builder.next;

		this.numPages = builder.pages.isEmpty() ? 1 : builder.pages.size();

		this.define(builder.pages);
	}

	public void open(Player player, int page) {
		if(this.pages.size() == 0) {
			EMPTY.open(player);
			return;
		}
		this.pages.get(page > 1 ? Math.min(page - 1, this.pages.size() - 1) : 0).open(player);
	}

	private void define(Map<Layout, List<InventoryProperty>> layouts) {
		int p = 1;

		for(Map.Entry<Layout, List<InventoryProperty>> layout : layouts.entrySet()) {
			final int pRef = p;
			UI.Builder page = UI.builder().dimension(InventoryDimension.of(9, 6));
			Layout.Builder l = Layout.builder().dimension(9, 6);
			for(Map.Entry<Integer, Icon> entry : this.layout.getElements().entrySet()) {
				l.slot(entry.getValue(), entry.getKey());
			}

			for(Map.Entry<Integer, Icon> entry : layout.getKey().getElements().entrySet()) {
				l.slot(entry.getValue(), entry.getKey());
			}

			for(InventoryProperty property : layout.getValue()) {
				page.property(property);
			}

			UI result = page.build(BidoofUnleashed.getInstance());
			Icon prev = Icon.from(ItemStack.builder().itemType(previous).add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "Previous Page ", TextColors.GRAY, "(", TextColors.YELLOW, (p == 1 ? numPages : p - 1), TextColors.GRAY, ")")).build());
			prev.addListener(clickable -> {
				clickable.getPlayer().closeInventory();
				this.open(clickable.getPlayer(), pRef == 1 ? numPages : pRef - 1);
			});

			Icon curr = Icon.from(ItemStack.builder().itemType(current).add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "Current Page ", TextColors.GRAY, "(", TextColors.YELLOW, p, TextColors.GRAY, ")")).build());

			Icon next = Icon.from(ItemStack.builder().itemType(this.next).add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "Next Page ", TextColors.GRAY, "(", TextColors.YELLOW, (p == numPages ? 1 : p + 1), TextColors.GRAY, ")")).build());
			next.addListener(clickable -> {
				clickable.getPlayer().closeInventory();
				this.open(clickable.getPlayer(), pRef == numPages ? 1 : pRef + 1);
			});

			l.slot(prev, 48);
			l.slot(curr, 49);
			l.slot(next, 50);

			this.pages.add(result.define(l.build()));
			++p;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Map<Layout, List<InventoryProperty>> pages = Maps.newHashMap();
		private Layout layout;
		private ItemType previous = ItemTypes.BARRIER;
		private ItemType current = ItemTypes.BARRIER;
		private ItemType next = ItemTypes.BARRIER;

		public Builder addPage(Layout layout, List<InventoryProperty> properties) {
			this.pages.computeIfAbsent(layout, x -> Lists.newArrayList()).addAll(properties);
			return this;
		}

		public Builder layout(Layout layout) {
			this.layout = layout;
			return this;
		}

		public Builder previous(ItemType type) {
			this.previous = type;
			return this;
		}

		public Builder current(ItemType type) {
			this.current = type;
			return this;
		}

		public Builder next(ItemType type) {
			this.next = type;
			return this;
		}

		public CategoryPage build() {
			return new CategoryPage(this);
		}
	}
}
