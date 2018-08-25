package gg.psyduck.bidoofunleashed.impl;

import com.nickimpact.impactor.json.Typing;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.BaseStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.types.LevelingEvolution;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.stream.Collectors;

@Typing("evolution")
public class EvolutionRequirement implements Requirement {

	private final String type = "evolution";

	@Override
	public boolean passes(Gym gym, Player player) throws Exception {
		PlayerStorage storage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) player).orElseThrow(() -> new Exception("Missing player storage data for " + player.getName()));
		for(NBTTagCompound nbt : storage.partyPokemon) {
			EnumPokemon species = EnumPokemon.getFromNameAnyCase(nbt.getString(NbtKeys.NAME));
			int level = nbt.getInteger(NbtKeys.LEVEL);

			BaseStats stats = EntityPixelmon.allBaseStats.get(species);
			if(stats.preEvolutions.length > 0) {
				stats = EntityPixelmon.allBaseStats.get(stats.preEvolutions[0]);
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
	public void onInvalid(Gym gym, Player player) {
		player.sendMessage(MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.REQUIREMENT_EVOLUTION, null, null));
	}
}
