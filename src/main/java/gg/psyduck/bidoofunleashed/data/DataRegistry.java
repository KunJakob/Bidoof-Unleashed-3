package gg.psyduck.bidoofunleashed.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.psyduck.bidoofunleashed.battles.Category;
import gg.psyduck.bidoofunleashed.api.BU3Battlable;
import gg.psyduck.bidoofunleashed.battles.e4.EliteFour;
import gg.psyduck.bidoofunleashed.battles.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class DataRegistry {

	private List<Gym> gyms = Lists.newArrayList();
	private List<EliteFour> elite4 = Lists.newArrayList();

	private Map<UUID, PlayerData> playerData = Maps.newHashMap();

	public PlayerData getPlayerData(UUID uuid) {
		return playerData.get(uuid);
	}

	public synchronized void register(UUID uuid, PlayerData data) {
		this.playerData.put(uuid, data);
	}

	public Map<Category, List<Gym>> sortedGyms() {
		Map<Category, List<Gym>> mapping = Maps.newHashMap();

		for(Gym gym : gyms) {
			mapping.computeIfAbsent(gym.getCategory(), x -> Lists.newArrayList()).add(gym);
		}

		for(Map.Entry<Category, List<Gym>> entry : mapping.entrySet()) {
			entry.getValue().sort(Comparator.comparing(BU3Battlable::getWeight));
		}

		return mapping;
	}
}
