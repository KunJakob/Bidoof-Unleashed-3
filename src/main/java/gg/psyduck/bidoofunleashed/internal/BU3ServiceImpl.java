package gg.psyduck.bidoofunleashed.internal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.psyduck.bidoofunleashed.api.BU3Service;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public class BU3ServiceImpl implements BU3Service {

	private List<Gym> gyms = Lists.newArrayList();

	@Override
	public List<Gym> getAllGyms() {
		return this.gyms;
	}

	@Override
	public Optional<Gym> getGym(String id) {
		return this.gyms.stream().filter(gym -> gym.getName().equalsIgnoreCase(id)).findAny();
	}

	@Override
	public Map<Gym, Map<UUID, EnumLeaderType>> getAllLeaders() {
		Map<Gym, Map<UUID, EnumLeaderType>> leaders = Maps.newHashMap();
		for(Gym gym : this.gyms) {
			leaders.put(gym, gym.getLeaders());
		}

		return leaders;
	}

	@Override
	public Optional<Map<UUID, EnumLeaderType>> getLeadersForGym(String name) {
		Optional<Gym> target = this.gyms.stream().filter(gym -> gym.getName().equalsIgnoreCase(name)).findAny();
		return target.map(Gym::getLeaders);
	}

	@Override
	public Map<UUID, EnumLeaderType> getLeadersForGym(Gym gym) {
		return gym.getLeaders();
	}
}
