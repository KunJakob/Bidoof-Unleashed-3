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
