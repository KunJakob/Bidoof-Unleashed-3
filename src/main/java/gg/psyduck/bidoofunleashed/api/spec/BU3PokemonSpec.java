package gg.psyduck.bidoofunleashed.api.spec;

import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.comm.EnumUpdateType;
import com.pixelmonmod.pixelmon.config.PixelmonItemsHeld;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.items.ItemHeld;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import net.minecraft.item.ItemStack;

import java.util.List;

public class BU3PokemonSpec extends PokemonSpec {

	public String nickname = null;
	public String item = null;
	public List<String> attacks = null;

	public BU3PokemonSpec(String... args) {
		super(args);
	}

	@Override
	public void apply(EntityPixelmon pokemon) {
		super.apply(pokemon);

		if(nickname != null) {
			pokemon.setNickname(nickname);
		}

		if(item != null) {
			ItemHeld heldItem = PixelmonItemsHeld.getHeldItem(item);
			if(heldItem != null) {
				pokemon.setHeldItem(new ItemStack(heldItem));
			}
		}

		if(attacks != null) {
			Moveset moveset = new Moveset(pokemon);
			for(String attack : attacks) {
				AttackBase.getAttackBase(attack).map(Attack::new).ifPresent(moveset::add);
			}
			if(!moveset.isEmpty()) {
				pokemon.setMoveset(moveset);
				pokemon.update(EnumUpdateType.Moveset);
			}
		}

		pokemon.update(EnumUpdateType.Stats, EnumUpdateType.Nickname);
	}
}
