package gg.psyduck.bidoofunleashed.api.pixelmon.participants;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.TrainerParticipant;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.enums.forms.EnumMega;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.api.pixelmon.specs.BU3PokemonSpec;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

public class TempTrainerTeamParticipant extends TrainerParticipant {
	public TempTrainerTeamParticipant(NPCTrainer trainer, EntityPlayer opponent, List<BU3PokemonSpec> team, BU3Battlable battlable) throws IllegalStateException {
		super(trainer, opponent, 1);
		this.allPokemon = new PixelmonWrapper[team.size()];

		for (int i = 0; i < team.size(); i++) {
			Pokemon pokemon = team.get(i).create();
			if (team.get(i).level == null) {
				pokemon.setLevel(battlable.getBattleSettings(battlable.getBattleType((Player) opponent)).getLvlCap());
			}

			pokemon.setCaughtBall(EnumPokeballs.PokeBall);
			this.allPokemon[i] = new PixelmonWrapper(this, pokemon, i);
			if(pokemon.getFormEnum() == EnumMega.Mega || pokemon.getFormEnum() ==  EnumMega.MegaX || pokemon.getFormEnum() == EnumMega.MegaY) {
				this.allPokemon[i].isMega = true;
			}
		}
	}
}
