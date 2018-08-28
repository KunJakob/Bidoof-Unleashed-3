package gg.psyduck.bidoofunleashed.storage.dao;

import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBU3Dao {
	protected final SpongePlugin plugin;
	private final String name;

	public abstract void init() throws Exception;

	public abstract void shutdown() throws Exception;

	public abstract void addOrUpdatePlayerData(PlayerData data) throws Exception;

	public abstract Optional<PlayerData> getPlayerData(UUID uuid) throws Exception;

	public abstract void addOrUpdateGym(Gym gym) throws Exception;

	public abstract List<Gym> fetchGyms() throws Exception;

	public abstract void removeGym(Gym gym) throws Exception;
}
