package gg.psyduck.bidoofunleashed.api.gyms.json;

import com.google.gson.Gson;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import com.nickimpact.impactor.json.Adapter;
import com.nickimpact.impactor.json.Registry;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;

public class RequirementAdapter extends Adapter<Requirement> {

	public static Registry<Requirement> requirementRegistry = new Registry<>();

	public RequirementAdapter(SpongePlugin plugin) {
		super(plugin);
	}

	@Override
	public Gson getGson() {
		return BidoofUnleashed.prettyGson;
	}

	@Override
	public Registry getRegistry() {
		return requirementRegistry;
	}
}
