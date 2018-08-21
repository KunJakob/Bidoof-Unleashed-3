package gg.psyduck.bidoofunleashed.rewards;

import com.nickimpact.impactor.api.rewards.Reward;
import net.minecraft.item.ItemStack;
import org.spongepowered.api.entity.living.player.Player;

public class ItemReward implements Reward<ItemStack> {
	@Override
	public ItemStack getReward() {
		return null;
	}

	@Override
	public void give(Player player) throws Exception {

	}
}
