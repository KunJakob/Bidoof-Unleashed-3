import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nickimpact.impactor.api.rewards.Reward;
import com.pixelmonmod.pixelmon.comm.PixelmonData;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.api.gyms.json.RequirementAdapter;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.impl.EvolutionRequirement;
import gg.psyduck.bidoofunleashed.impl.GymRequirement;
import gg.psyduck.bidoofunleashed.impl.LevelRequirement;
import gg.psyduck.bidoofunleashed.rewards.ItemReward;
import gg.psyduck.bidoofunleashed.rewards.money.MoneyReward;
import gg.psyduck.bidoofunleashed.rewards.PokemonReward;
import gg.psyduck.bidoofunleashed.rewards.json.RewardAdapter;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

public class GymTest {

//	@Test
//	public void gymBuilder() {
//		Gym gym = Gym.builder()
//				.name("Bidoof Gym")
//				.badge(new Badge("Test Badge", "air"))
//				.arena(new Gym.Arena(
//						new Gym.LocAndRot(new Vector3d(), new Vector3d()),
//						new Gym.LocAndRot(new Vector3d(), new Vector3d()),
//						new Gym.LocAndRot(new Vector3d(), new Vector3d())
//				))
//				.levelCap(50)
//				.rule("RaiseToCap")
//				.rule("NumPokemon: 3")
//				.clause("bag")
//				.rewards(EnumBattleType.First, new PokemonReward(Lists.newArrayList()), new PokemonReward(Lists.newArrayList()))
//				.leader(UUID.randomUUID(), EnumLeaderType.PLAYER)
//				.leader(UUID.randomUUID(), EnumLeaderType.NPC)
//				.requirements(new EvolutionRequirement(), new GymRequirement("XXX"))
//				.build();
//
//		List<PixelmonData> data = gym.getPool().getTeam();
//
//		Gson gson = new GsonBuilder().setPrettyPrinting()
//				.registerTypeAdapter(Requirement.class, new RequirementAdapter(null))
//				.registerTypeAdapter(Reward.class, new RewardAdapter(null))
//				.create();
//
//		data.forEach(member -> System.out.println(gson.toJson(member)));
//
//		try {
//			RequirementAdapter.requirementRegistry.register(EvolutionRequirement.class);
//			RequirementAdapter.requirementRegistry.register(GymRequirement.class);
//			RequirementAdapter.requirementRegistry.register(LevelRequirement.class);
//
//			RewardAdapter.rewardRegistry.register(PokemonReward.class);
//			RewardAdapter.rewardRegistry.register(ItemReward.class);
//			RewardAdapter.rewardRegistry.register(MoneyReward.class);
//
//			System.out.println(gson.toJson(gym));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
