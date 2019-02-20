package gg.psyduck.bidoofunleashed.rewards;

import com.google.common.util.concurrent.AtomicDouble;
import com.nickimpact.impactor.json.Typing;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import gg.psyduck.bidoofunleashed.api.rewards.BU3Reward;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Random;
import java.util.TreeMap;

@Typing("chanced-pokemon")
public class ChancePokemonReward extends BU3Reward<TreeMap<Double, PokemonSpec>> {

	private final String type = "chanced-pokemon";
	private static final Random rng = new Random();

	public ChancePokemonReward(TreeMap<Double, PokemonSpec> reward) {
		super(reward);
	}

	@Override
	public void give(Player player) throws Exception {
		PlayerPartyStorage storage = Pixelmon.storageManager.getParty(player.getUniqueId());

		final AtomicDouble maxPercent = new AtomicDouble();
		this.reward.keySet().forEach(maxPercent::addAndGet);
		double percent = rng.nextDouble() * maxPercent.doubleValue();
		PokemonSpec spec = this.reward.lowerEntry(percent).getValue();
		Pokemon pokemon = Pixelmon.pokemonFactory.create(spec);
		storage.add(pokemon);
	}
}
