package gg.psyduck.bidoofunleashed.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import gg.psyduck.bidoofunleashed.api.battlables.BU3BattleBase;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import lombok.Getter;
import sun.util.resources.cldr.teo.CalendarData_teo_KE;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class DataRegistry {

	private Multimap<Category, BU3BattleBase> battlables = ArrayListMultimap.create();

	private Map<UUID, PlayerData> playerData = Maps.newHashMap();

	public PlayerData getPlayerData(UUID uuid) {
		return playerData.get(uuid);
	}

	public synchronized void register(UUID uuid, PlayerData data) {
		this.playerData.put(uuid, data);
	}

	public Map<Category, List<Gym>> sortedGyms() {
		Map<Category, List<Gym>> mapping = Maps.newHashMap();
		battlables.entries().stream()
				.filter(entry -> entry.getValue() instanceof Gym)
				.forEach(entry -> mapping.computeIfAbsent(entry.getKey(), x -> Lists.newArrayList()).add((Gym) entry.getValue()));

		for(Map.Entry<Category, List<Gym>> entry : mapping.entrySet()) {
			entry.getValue().sort(Comparator.comparing(BU3Battlable::getWeight));
		}

		return mapping;
	}
}
