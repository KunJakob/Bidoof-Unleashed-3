package gg.psyduck.bidoofunleashed.api.pixelmon.specs;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.comm.EnumUpdateType;
import com.pixelmonmod.pixelmon.config.PixelmonItemsHeld;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.items.ItemHeld;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * An extension of Pixelmon's provided PokemonSpec. Simply adds a few extra fields in quick
 * access for future usage.
 */
public class BU3PokemonSpec extends PokemonSpec {

	public String nickname;
	public String item;
	public List<String> attacks;

	public BU3PokemonSpec(String... args) {
		super(args);

		for (String arg : args) {
			switch (arg) {
				case "nickname":
					this.nickname = arg;
					break;
				case "item":
					this.item = arg;
					break;
				case "attacks":
					this.attacks = Arrays.asList(arg.split(","));
					break;
			}
		}
	}

	@Override
	public void apply(Pokemon pokemon) {
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
			Moveset moveset = new Moveset(pokemon.getMoveset().pokemonLink);
			for(String attack : attacks) {
				AttackBase.getAttackBase(attack).map(Attack::new).ifPresent(moveset::add);
			}
			if(!moveset.isEmpty()) {
				for(int i = 0; i < moveset.attacks.length; i++) {
					pokemon.getMoveset().set(i, moveset.get(i));
				}
			}
		}
	}
}
