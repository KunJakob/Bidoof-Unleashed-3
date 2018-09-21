package gg.psyduck.bidoofunleashed.utils;

import com.google.common.collect.Lists;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.pixelmon.specs.BU3PokemonSpec;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.Random;

public class TeamSelectors {

	public static List<BU3PokemonSpec> randomized(Gym gym, Player player) {
		// Randomly select team
		Random x = new Random();

		EnumBattleType type = gym.getBattleType(player);
		int min = gym.getBattleSettings(type).getMinPokemon();
		int max = gym.getBattleSettings(type).getMaxPokemon();
		int size = Math.min(1, Math.max(6, x.nextInt(max - min) + min));

		List<BU3PokemonSpec> team = Lists.newArrayList();

		List<BU3PokemonSpec> specs = gym.getBattleSettings(type).getPool().getTeam();
		if(specs.size() > 0) {
			for (int i = 0; i < size; i++) {
				BU3PokemonSpec spec = specs.get(x.nextInt(specs.size()));
				BU3PokemonSpec y = spec;
				while (team.stream().anyMatch(s -> s.name.equalsIgnoreCase(y.name))) {
					spec = specs.get(x.nextInt(specs.size()));
				}

				team.add(spec);
			}
		} else {
			team.add(new BU3PokemonSpec("bidoof lvl:5"));
		}

		return team;
	}
}
