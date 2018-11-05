package gg.psyduck.bidoofunleashed.api.pixelmon.custom;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.BaseStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CustomSpec {

	// Base Fields
	private EnumPokemon species;
	private BaseStats stats;

	// Extension Fields
	private String nameBase = "&e{{pokemon}}";
	private int rarity = 0;
	private String spawnMsg = "";
	private int maxIVs = -1; // -1 equals just create random set of perfect IVs
	private boolean canBreed = true;
	private Map<StatsType, Integer> statBonus = new HashMap<>();
	private String texture = "";

	public CustomSpec(EnumPokemon species) {
		this.species = species;
		this.stats = new BaseStats();
		this.stats.form = 0;
	}

	public EntityPixelmon create(EntityPixelmon pokemon) throws Exception {
		if(this.stats.form == 0) {
			throw new Exception("Form must be specified for custom spec to initialize properly");
		}

		pokemon.setNickname(this.nameBase.replaceAll("\\{\\{pokemon}}", species.name).replaceAll("&", "§"));
		NBTTagCompound nbt = pokemon.writeToNBT(new NBTTagCompound());
		nbt.setString("CustomTexture", this.texture);
		if(!this.canBreed) {
			nbt.setBoolean("unbreedable", true);
		}
		pokemon.readFromNBT(nbt);
		pokemon.setForm(stats.form);

		if(!this.canBreed) {
			pokemon.getEntityData().setBoolean("unbreedable", true);
		}

		if(!this.stats.levelUpMoves.isEmpty()) {
			pokemon.loadMoveset();
		}

		return pokemon;
	}
}
