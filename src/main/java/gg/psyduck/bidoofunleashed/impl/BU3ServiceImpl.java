package gg.psyduck.bidoofunleashed.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.BU3Service;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.api.gyms.json.RequirementAdapter;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import lombok.Getter;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Getter
public class BU3ServiceImpl implements BU3Service {

	private List<Requirement> requirements = Lists.newArrayList();

	@Override
	public Optional<PlayerData> getPlayerData(UUID uuid) {
		PlayerData data = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(uuid);
		if(data == null) {
			try {
				return BidoofUnleashed.getInstance().getStorage().getPlayerData(uuid).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return Optional.empty();
			}
		} else {
			return Optional.of(data);
		}
	}

	@Override
	public List<Gym> getAllGyms() {
		return BidoofUnleashed.getInstance().getDataRegistry().getBattlables().values().stream().filter(base -> base instanceof Gym).map(base -> (Gym) base).collect(Collectors.toList());
	}

	@Override
	public Optional<Gym> getGym(String id) {
		return this.getAllGyms().stream().filter(gym -> gym.getName().equalsIgnoreCase(id)).findAny();
	}

	@Override
	public Map<Gym, List<UUID>> getAllLeaders() {
		Map<Gym, List<UUID>> leaders = Maps.newHashMap();
		for(Gym gym : this.getAllGyms()) {
			leaders.put(gym, gym.getLeaders());
		}

		return leaders;
	}

	@Override
	public Optional<List<UUID>> getLeadersForGym(String name) {
		Optional<Gym> target = this.getAllGyms().stream().filter(gym -> gym.getName().equalsIgnoreCase(name)).findAny();
		return target.map(Gym::getLeaders);
	}

	@Override
	public List<UUID> getLeadersForGym(Gym gym) {
		return gym.getLeaders();
	}

	@Override
	public void addGym(Gym gym) {
		BidoofUnleashed.getInstance().getDataRegistry().getBattlables().put(gym.getCategory(), gym);
		BidoofUnleashed.getInstance().getStorage().addGym(gym);
	}

	@Override
	public boolean purgeGym(Gym gym) {
		if(BidoofUnleashed.getInstance().getDataRegistry().getBattlables().get(gym.getCategory()).contains(gym)) {
			BidoofUnleashed.getInstance().getDataRegistry().getBattlables().get(gym.getCategory()).remove(gym);
			BidoofUnleashed.getInstance().getStorage().removeGym(gym);
			return true;
		}
		return false;
	}

	@Override
	public List<EliteFour> getAllEliteFour() {
		return BidoofUnleashed.getInstance().getDataRegistry().getBattlables().values().stream().filter(base -> base instanceof EliteFour).map(base -> (EliteFour) base).collect(Collectors.toList());
	}

	@Override
	public void addE4(EliteFour eliteFour) {
		BidoofUnleashed.getInstance().getDataRegistry().getBattlables().put(eliteFour.getCategory(), eliteFour);
		BidoofUnleashed.getInstance().getStorage().addE4(eliteFour);
	}

	@Override
	public void registerRequirement(Class<? extends Requirement> requirement) {
		Preconditions.checkArgument(Sponge.getGame().getState().ordinal() < GameState.SERVER_STARTED.ordinal(), "Attempt to register requirement during or after GameServerStartedEvent");
		try {
			RequirementAdapter.requirementRegistry.register(requirement);
			this.requirements.add(requirement.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Requirement> getLoadedRequirements() {
		return this.requirements;
	}
}
