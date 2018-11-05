package gg.psyduck.bidoofunleashed.utils;

import com.google.common.collect.Lists;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.pixelmon.specs.BU3PokemonSpec;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.Random;

public class TeamSelectors {

	public static List<BU3PokemonSpec> randomized(BU3Battlable battlable, Player player) {
		// Randomly select team
		Random x = new Random();

		EnumBattleType type = battlable.getBattleType(player);
		int min = battlable.getBattleSettings(type).getMinPokemon();
		int max = battlable.getBattleSettings(type).getMaxPokemon();
		int size = Math.max(1, Math.min(6, x.nextInt(max - min) + min));

		int lvlCap = battlable.getBattleSettings(battlable.getBattleType(player)).getLvlCap();

		List<BU3PokemonSpec> team = Lists.newArrayList();

		List<BU3PokemonSpec> specs = Lists.newArrayList(battlable.getBattleSettings(type).getPool().getTeam());
		if(specs.size() > 0) {
			for (int i = 0; i < size && specs.size() > i; i++) {
				BU3PokemonSpec spec = specs.get(x.nextInt(specs.size()));
				if(spec.level != null && spec.level > lvlCap) {
					spec.level = lvlCap;
				}
				team.add(spec);
				specs.remove(spec);
			}
		} else {
			team.add(new BU3PokemonSpec("bidoof lvl:5"));
		}

		return team;
	}
}
