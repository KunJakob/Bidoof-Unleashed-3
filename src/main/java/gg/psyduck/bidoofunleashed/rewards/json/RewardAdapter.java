package gg.psyduck.bidoofunleashed.rewards.json;

import com.google.gson.Gson;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import com.nickimpact.impactor.api.rewards.Reward;
import com.nickimpact.impactor.json.Adapter;
import com.nickimpact.impactor.json.Registry;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;

public class RewardAdapter extends Adapter<Reward> {

	public static Registry<Reward> rewardRegistry = new Registry<>();

	public RewardAdapter(SpongePlugin plugin) {
		super(plugin);
	}

	@Override
	public Gson getGson() {
		return BidoofUnleashed.prettyGson;
	}

	@Override
	public Registry getRegistry() {
		return rewardRegistry;
	}
}
