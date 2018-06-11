package gg.psyduck.agpr.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataRegistry {

	private List<GymData> gyms = Lists.newArrayList();
	private Map<UUID, PlayerData> playerData = Maps.newHashMap();
}
