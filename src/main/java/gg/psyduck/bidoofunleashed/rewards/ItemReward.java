package gg.psyduck.bidoofunleashed.rewards;

import com.nickimpact.impactor.api.rewards.Reward;
import com.nickimpact.impactor.json.Typing;
import net.minecraft.item.ItemStack;
import org.spongepowered.api.entity.living.player.Player;

@Typing("item")
public class ItemReward implements Reward<ItemStack> {

	private final String type = "item";

	@Override
	public ItemStack getReward() {
		return null;
	}

	@Override
	public void give(Player player) throws Exception {

	}
}
