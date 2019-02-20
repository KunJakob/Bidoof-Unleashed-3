package gg.psyduck.bidoofunleashed.rewards;

import com.nickimpact.impactor.json.Typing;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import gg.psyduck.bidoofunleashed.api.rewards.BU3Reward;
import org.spongepowered.api.entity.living.player.Player;

@Typing("pokemon")
public class PokemonReward extends BU3Reward<PokemonSpec> {

	private final String type = "pokemon";

	public PokemonReward(PokemonSpec reward) {
		super(reward);
	}

	@Override
	public void give(Player player) throws Exception {
		PlayerPartyStorage storage = Pixelmon.storageManager.getParty(player.getUniqueId());
		Pokemon pokemon = Pixelmon.pokemonFactory.create(this.reward);
		storage.add(pokemon);
	}
}
