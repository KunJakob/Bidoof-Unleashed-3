package gg.psyduck.bidoofunleashed.commands.arguments;

import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import org.apache.commons.lang3.StringUtils;

public class GymSpec {

	public static Gym parseValue(String args) throws Exception {
		String[] split = args.split(" ");

		String name = null;
		String badgeType = null;
		String badgeName = null;

		Gym.Builder builder = Gym.builder();
		for(String mapping : split) {
			String[] map = mapping.split(":");
			if(map[0].equalsIgnoreCase("name")) {
				name = map[1];
				builder.name(map[1]);
			} else if(map[0].equalsIgnoreCase("lf")) {
				builder.levelCap(EnumBattleType.First, Integer.parseInt(map[1]));
			} else if(map[0].equalsIgnoreCase("lr")) {
				builder.levelCap(EnumBattleType.Rematch, Integer.parseInt(map[1]));
			} else if(map[0].equalsIgnoreCase("bt")) {
				if(map.length == 3) {
					badgeType = map[1] + ":" + map[2];
				} else {
					badgeType = map[1];
				}
			} else if(map[0].equalsIgnoreCase("bn")) {
				badgeName = map[1];
			} else if(map[0].equalsIgnoreCase("id")) {
				builder.id(map[1]);
			} else if(map[0].equalsIgnoreCase("c") || map[0].equalsIgnoreCase("category")) {
				builder.category(map[1]);
			} else if(map[0].equalsIgnoreCase("w") || map[0].equalsIgnoreCase("weight")) {
				builder.weight(Integer.parseInt(map[1]));
			}
		}

		if(badgeType == null) {
			badgeType = "minecraft:barrier";
		} else if(badgeName == null) {
			if(name == null) {
				throw new Exception("Gym specifications are invalid...");
			}

			badgeName = StringUtils.capitalize(name) + " Badge";
 		}

 		builder.badge(new Badge(badgeName, badgeType));
		return builder.build();
	}
}
