package gg.psyduck.bidoofunleashed.impl.requirements;

import com.nickimpact.impactor.json.Typing;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.BaseStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.types.LevelingEvolution;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.stream.Collectors;

@Typing("evolution")
public class EvolutionRequirement implements Requirement {

	private final String type = "evolution";

	@Override
	public String id() {
		return type;
	}

	@Override
	public boolean passes(BU3Battlable battlable, Player player) throws Exception {
		PlayerPartyStorage storage = Pixelmon.storageManager.getParty(player.getUniqueId());
		for(Pokemon pokemon : storage.getTeam()) {
			EnumSpecies species = pokemon.getSpecies();
			int level = pokemon.getLevel();

			BaseStats stats = pokemon.getBaseStats();
			if(stats.preEvolutions.length > 0) {
				stats = stats.preEvolutions[0].getBaseStats();
				List<LevelingEvolution> evolution = stats.evolutions.stream().filter(evo -> evo instanceof LevelingEvolution)
						.map(evo -> (LevelingEvolution) evo)
						.filter(evo -> evo.to.name.equalsIgnoreCase(species.name))
						.collect(Collectors.toList());
				for(LevelingEvolution evo : evolution) {
					if(evo.level > level) {
						return false;
					}
				}
			}
		}

		return true;
	}

	@Override
	public void onInvalid(BU3Battlable battlable, Player player) {
		player.sendMessage(MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.REQUIREMENT_EVOLUTION, null, null));
	}

	@Override
	public EvolutionRequirement supply(String... args) throws Exception {
		return this;
	}
}
