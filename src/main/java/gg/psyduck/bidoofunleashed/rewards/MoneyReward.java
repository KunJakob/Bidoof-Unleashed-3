package gg.psyduck.bidoofunleashed.rewards;

import com.nickimpact.impactor.api.rewards.Reward;
import com.nickimpact.impactor.json.Typing;
import org.spongepowered.api.entity.living.player.Player;

import java.math.BigDecimal;

@Typing("money")
public class MoneyReward implements Reward<BigDecimal> {
	private final String type = "money";

	@Override
	public BigDecimal getReward() {
		return null;
	}

	@Override
	public void give(Player player) throws Exception {

	}
}
