package gg.psyduck.bidoofunleashed.gyms.temporary;

import com.google.common.collect.Maps;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import lombok.Getter;

import java.util.Map;

public class BattleRegistry {

	@Getter private static Map<Challenge, Gym> battles = Maps.newHashMap();

	public static void register(Challenge challenge, Gym gym) {
		battles.put(challenge, gym);
	}

	public static Gym deregister(Challenge challenge) {
		return battles.remove(challenge);
	}
}
