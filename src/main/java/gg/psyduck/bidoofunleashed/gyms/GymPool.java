package gg.psyduck.bidoofunleashed.gyms;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.comm.PixelmonData;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import com.pixelmonmod.pixelmon.enums.forms.EnumCastform;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.spec.BU3PokemonSpec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.swing.text.html.parser.Entity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class GymPool {

	private final File pool;
    private transient List<BU3PokemonSpec> team = Lists.newArrayList();

    GymPool(File pool) {
    	this.pool = pool;
    	this.createFiles(pool);
    }

    public GymPool init() {
    	this.team = ShowdownImporter.importFromFile(pool);
    	return this;
    }

    private void createFiles(File path) {
	    if(!path.exists()) {
		    path.getParentFile().mkdirs();
		    try {
			    path.createNewFile();
		    } catch (IOException ignored) {}
	    }
    }
}
