package gg.psyduck.bidoofunleashed.utils;

import com.google.common.collect.Lists;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3BattleBase;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import gg.psyduck.bidoofunleashed.e4.Stage;
import gg.psyduck.bidoofunleashed.gyms.Gym;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GeneralUtils {

	@SuppressWarnings("unchecked")
	public static List<BU3BattleBase> search(UUID uuid) {
		List<BU3BattleBase> results = Lists.newArrayList();
		results.addAll(searchGyms(uuid));
		results.addAll(searchEliteFour(uuid));
		return results;
	}

	public static List<Gym> searchGyms(UUID uuid) {
		return BidoofUnleashed.getInstance()
				.getDataRegistry()
				.getBattlables()
				.values()
				.stream()
				.filter(x -> x instanceof Gym)
				.map(x -> (Gym) x)
				.filter(gym -> gym.getLeaders().contains(uuid))
				.collect(Collectors.toList());
	}

	public static List<Stage> searchEliteFour(UUID uuid) {
		List<Stage> stages = Lists.newArrayList();
		BidoofUnleashed.getInstance()
				.getDataRegistry()
				.getBattlables()
				.values()
				.stream()
				.filter(x -> x instanceof EliteFour)
				.map(x -> (EliteFour) x)
				.forEach(e4 -> {
					stages.addAll(e4.getStages());
					stages.add(e4.getChampion());
				});

		return stages.stream().filter(stage -> stage.getLeaders().contains(uuid)).collect(Collectors.toList());
	}

}
