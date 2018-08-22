import com.flowpowered.math.vector.Vector3d;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nickimpact.impactor.api.rewards.Reward;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.api.gyms.json.RequirementAdapter;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.impl.EvolutionRequirement;
import gg.psyduck.bidoofunleashed.impl.GymRequirement;
import gg.psyduck.bidoofunleashed.impl.LevelRequirement;
import gg.psyduck.bidoofunleashed.rewards.ItemReward;
import gg.psyduck.bidoofunleashed.rewards.MoneyReward;
import gg.psyduck.bidoofunleashed.rewards.PokemonReward;
import gg.psyduck.bidoofunleashed.rewards.json.RewardAdapter;
import org.junit.Test;

import java.util.UUID;

public class GymTest {

	@Test
	public void gymBuilder() {
		Gym gym = Gym.builder()
				.name("Bidoof Gym")
				.badge(new Badge("Test Badge", "air", null, null, null))
				.arena(new Gym.Arena(
						new Gym.LocAndRot(new Vector3d(), new Vector3d()),
						new Gym.LocAndRot(new Vector3d(), new Vector3d()),
						new Gym.LocAndRot(new Vector3d(), new Vector3d())
				))
				.levelCap(50)
				.rule("RaiseToCap")
				.rule("NumPokemon: 3")
				.clause("bag")
				.rewards(new PokemonReward(), new PokemonReward())
				.leader(UUID.randomUUID(), EnumLeaderType.PLAYER)
				.leader(UUID.randomUUID(), EnumLeaderType.NPC)
				.requirements(new EvolutionRequirement(), new GymRequirement("XXX"))
				.build();

		Gson gson = new GsonBuilder().setPrettyPrinting()
				.registerTypeAdapter(Requirement.class, new RequirementAdapter(null))
				.registerTypeAdapter(Reward.class, new RewardAdapter(null))
				.create();
		try {
			RequirementAdapter.requirementRegistry.register(EvolutionRequirement.class);
			RequirementAdapter.requirementRegistry.register(GymRequirement.class);
			RequirementAdapter.requirementRegistry.register(LevelRequirement.class);

			RewardAdapter.rewardRegistry.register(PokemonReward.class);
			RewardAdapter.rewardRegistry.register(ItemReward.class);
			RewardAdapter.rewardRegistry.register(MoneyReward.class);

			System.out.println(gson.toJson(gym));
			gson.fromJson(gson.toJson(gym), Gym.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
