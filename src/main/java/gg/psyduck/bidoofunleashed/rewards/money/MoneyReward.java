package gg.psyduck.bidoofunleashed.rewards.money;

import com.nickimpact.impactor.json.Typing;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.rewards.BU3Reward;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;

@Typing("money")
public class MoneyReward extends BU3Reward<MoneyWrapper> {
	private final String type = "money";

	public MoneyReward(MoneyWrapper reward) {
		super(reward);
	}

	@Override
	public void give(Player player) throws Exception {
		UniqueAccount acc = BidoofUnleashed.getInstance().getEconomy().getOrCreateAccount(player.getUniqueId()).orElseThrow(() -> new Exception("Missing account data for " + player.getName()));
		if(BidoofUnleashed.getInstance().getEconomy().getCurrencies().stream().noneMatch(currency -> currency.getName().equalsIgnoreCase(reward.getCurrency()))) {
			throw new Exception("Invalid currency: " + this.reward.getCurrency());
		}

		Currency currency = BidoofUnleashed.getInstance().getEconomy().getCurrencies().stream().filter(c -> c.getName().equalsIgnoreCase(reward.getCurrency())).findAny().get();
		acc.deposit(currency, this.reward.getAmount(), Sponge.getCauseStackManager().getCurrentCause());
	}
}
