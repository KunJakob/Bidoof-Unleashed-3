package gg.psyduck.bidoofunleashed.storage.dao;

import com.google.common.collect.Multimap;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBU3Dao {
	protected final SpongePlugin plugin;
	private final String name;

	public abstract void init() throws Exception;

	public abstract void shutdown() throws Exception;

	public abstract void addPlayerData(PlayerData data) throws Exception;

	public abstract void updatePlayerData(PlayerData data) throws Exception;

	public abstract Optional<PlayerData> getPlayerData(UUID uuid) throws Exception;

	public abstract void addGym(Gym gym) throws Exception;

	public abstract void updateGym(Gym gym) throws Exception;

	public abstract Multimap<Category, Gym> fetchGyms() throws Exception;

	public abstract void removeGym(Gym gym) throws Exception;

	public abstract void addE4(EliteFour e4) throws Exception;

	public abstract void updateE4(EliteFour e4) throws Exception;

	public abstract Map<Category, EliteFour> fetchE4() throws Exception;

	public abstract void removeE4(EliteFour e4) throws Exception;
}
