package gg.psyduck.bidoofunleashed.rewards;

import com.nickimpact.impactor.json.Typing;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.InventoryTransformation;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

@Typing("item")
public class ItemReward extends BU3Reward<ItemStack> {

	private final String type = "item";

	public ItemReward(ItemStack reward) {
		super(reward);
	}

	@Override
	public void give(Player player) throws Exception {
		player.getInventory().transform(InventoryTransformation.of(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class), QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class))).offer(reward);
	}
}
