package gg.psyduck.bidoofunleashed.api.spec;

import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;

import java.util.List;

public class BU3PokemonSpec extends PokemonSpec {

	public String nickname;
	public String item;
	public List<String> attacks;

	@Override
	public void apply(EntityPixelmon pixelmon) {
		super.apply(pixelmon);
		pixelmon.setNickname(nickname);
	}
}
