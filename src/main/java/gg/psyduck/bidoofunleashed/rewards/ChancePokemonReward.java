package gg.psyduck.bidoofunleashed.rewards;

import com.google.common.util.concurrent.AtomicDouble;
import com.nickimpact.impactor.json.Typing;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import gg.psyduck.bidoofunleashed.api.rewards.BU3Reward;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
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
		PlayerStorage storage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) player).orElseThrow(() -> new Exception("Unable to locate player storage for " + player.getName()));

		final AtomicDouble maxPercent = new AtomicDouble();
		this.reward.keySet().forEach(maxPercent::addAndGet);
		double percent = rng.nextDouble() * maxPercent.doubleValue();
		PokemonSpec spec = this.reward.lowerEntry(percent).getValue();
		EntityPixelmon poke = spec.create((World) player.getWorld());
		storage.addToParty(poke);
		storage.sendUpdatedList();
	}
}
