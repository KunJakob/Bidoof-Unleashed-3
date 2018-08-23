package gg.psyduck.bidoofunleashed.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import lombok.Getter;
import lombok.Synchronized;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class DataRegistry {

	private List<Gym> gyms = Lists.newArrayList();
	private Map<UUID, PlayerData> playerData = Maps.newHashMap();

	public PlayerData getPlayerData(UUID uuid) {
		return playerData.get(uuid);
	}

	public synchronized void register(UUID uuid, PlayerData data) {
		this.playerData.put(uuid, data);
	}
}
