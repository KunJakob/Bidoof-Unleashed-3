package gg.psyduck.bidoofunleashed.rewards;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.rewards.Reward;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

public class PokemonReward implements Reward<List<PokemonSpec>> {

	private List<PokemonSpec> pokemon = Lists.newArrayList();

	@Override
	public List<PokemonSpec> getReward() {
		return pokemon;
	}

	@Override
	public void give(Player player) throws Exception {
		PlayerStorage storage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) player).orElseThrow(() -> new Exception("Unable to locate player storage for " + player.getName()));
		for(PokemonSpec spec : pokemon) {
			EntityPixelmon poke = spec.create((World) player.getWorld());
			storage.addToParty(poke);
		}
		storage.sendUpdatedList();
	}
}
