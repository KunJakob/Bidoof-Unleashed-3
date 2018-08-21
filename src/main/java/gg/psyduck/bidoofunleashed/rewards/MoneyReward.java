package gg.psyduck.bidoofunleashed.rewards;

import com.nickimpact.impactor.api.rewards.Reward;
import org.spongepowered.api.entity.living.player.Player;

import java.math.BigDecimal;

public class MoneyReward implements Reward<BigDecimal> {
	@Override
	public BigDecimal getReward() {
		return null;
	}

	@Override
	public void give(Player player) throws Exception {

	}
}
