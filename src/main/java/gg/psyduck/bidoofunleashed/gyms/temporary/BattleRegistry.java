package gg.psyduck.bidoofunleashed.gyms.temporary;

import com.google.common.collect.Maps;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import lombok.Getter;

import java.util.Map;

public class BattleRegistry {

	@Getter private static Map<Challenge, BU3Battlable> battles = Maps.newHashMap();

	public static void register(Challenge challenge, BU3Battlable battlable) {
		battles.put(challenge, battlable);
	}

	@CanIgnoreReturnValue
	public static BU3Battlable deregister(Challenge challenge) {
		return battles.remove(challenge);
	}
}
