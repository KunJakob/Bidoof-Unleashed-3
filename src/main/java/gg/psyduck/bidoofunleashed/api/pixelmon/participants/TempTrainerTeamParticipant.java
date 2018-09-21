package gg.psyduck.bidoofunleashed.api.pixelmon.participants;

import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.TrainerParticipant;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumBossMode;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import gg.psyduck.bidoofunleashed.api.pixelmon.specs.BU3PokemonSpec;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TempTrainerTeamParticipant extends TrainerParticipant {

	public TempTrainerTeamParticipant(NPCTrainer trainer, EntityPlayer opponent, List<BU3PokemonSpec> team, Gym gym) throws IllegalStateException {
		super(trainer, opponent, team.size());
		this.allPokemon = new PixelmonWrapper[team.size()];

		PlayerStorage storage = trainer.getPokemonStorage();
		for(int i = 0; i < team.size(); i++) {
			EntityPixelmon pokemon = team.get(i).create(trainer.world);
			if(team.get(i).level == null) {
				pokemon.getLvl().setLevel(gym.getBattleSettings(gym.getBattleType((Player) opponent)).getLvlCap());
			}
			if(pokemon.getMoveset() == null || pokemon.getMoveset().isEmpty()) {
				pokemon.loadMoveset();
			}
			pokemon.caughtBall = EnumPokeballs.PokeBall;
			pokemon.setOwnerId(trainer.getUniqueID());
			pokemon.setPokemonId(storage.getNewPokemonID());
			this.allPokemon[i] = new PixelmonWrapper(this, pokemon, i);
		}

		this.controlledPokemon = Collections.singletonList(this.allPokemon[0]);
	}

	@Override
	@SuppressWarnings("all")
	public PixelmonWrapper switchPokemon(PixelmonWrapper oldPokemon, int[] newPixelmonId) {
		int index = oldPokemon.getControlledIndex();
		if (index == -1 && this.bc.simulateMode) {
			index = 0;
		}

		String beforeName = oldPokemon.getNickname();
		oldPokemon.beforeSwitch();
		if (!oldPokemon.isFainted() && !oldPokemon.nextSwitchIsMove) {
			this.bc.sendToOthers("playerparticipant.withdrew", this, getTranslatedName(), beforeName);
		}

		PixelmonWrapper newWrapper = null;
		for (PixelmonWrapper pixelmonWrapper : allPokemon) {
			if (Arrays.equals(pixelmonWrapper.getPokemonID(), newPixelmonId)) {
				newWrapper = pixelmonWrapper;
				break;
			}
		}

		if (!this.bc.simulateMode) {
			oldPokemon.pokemon.catchInPokeball();

			oldPokemon.pokemon = null;
			EntityPixelmon pixelmon = this.trainer.releasePokemon(newPixelmonId);
			newWrapper.pokemon = pixelmon;
		}

		if (this.trainer.getBossMode() != EnumBossMode.NotBoss) {
			int lvl = 1;
			for (BattleParticipant p : this.bc.participants) {
				if (p.team != this.team && p instanceof PlayerParticipant) {
					lvl = Math.max(lvl, ((PlayerParticipant) p).getHighestLevel());
				}
			}
			lvl += this.trainer.getBossMode().extraLevels;
			newWrapper.getLevel().setLevel(lvl);
		}

		this.controlledPokemon.set(index, newWrapper);
		newWrapper.getBattleAbility().beforeSwitch(newWrapper);

		if (!this.bc.simulateMode) {
			this.bc.sendToOthers("pixelmon.battletext.sentout", this, getTranslatedName(), newWrapper.getNickname());
			this.bc.participants.forEach(BattleParticipant::updateOtherPokemon);
		}

		newWrapper.afterSwitch();
		return newWrapper;
	}

	private String getTranslatedName() {
		String loc = null;
		for (BattleParticipant p : this.bc.getOpponents(this)) {
			if (p instanceof PlayerParticipant) {
				loc = ((EntityPlayerMP) p.getEntity()).language;
			}
		}

		if (loc == null) {
			return this.trainer.getName();
		}

		return this.trainer.getName(loc);
	}
}
