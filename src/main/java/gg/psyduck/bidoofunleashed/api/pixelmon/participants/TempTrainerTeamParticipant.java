package gg.psyduck.bidoofunleashed.api.pixelmon.participants;

import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.TrainerParticipant;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.config.PixelmonEntityList;
import com.pixelmonmod.pixelmon.config.PixelmonServerConfig;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumBossMode;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleAIMode;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleEndCause;
import com.pixelmonmod.pixelmon.enums.forms.EnumForms;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.api.pixelmon.specs.BU3PokemonSpec;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TempTrainerTeamParticipant extends TrainerParticipant {

	public TempTrainerTeamParticipant(NPCTrainer trainer, EntityPlayer opponent, List<BU3PokemonSpec> team, BU3Battlable battlable) throws IllegalStateException {
		super(trainer, opponent, 1);
		this.allPokemon = new PixelmonWrapper[team.size()];

		PlayerStorage storage = trainer.getPokemonStorage();
		for (int i = 0; i < team.size(); i++) {
			EntityPixelmon pokemon = team.get(i).create(trainer.world);
			if (team.get(i).level == null) {
				pokemon.getLvl().setLevel(battlable.getBattleSettings(battlable.getBattleType((Player) opponent)).getLvlCap());
			}
			if (pokemon.getMoveset() == null || pokemon.getMoveset().isEmpty()) {
				pokemon.loadMoveset();
			}
			pokemon.caughtBall = EnumPokeballs.PokeBall;
			pokemon.setOwnerId(trainer.getUniqueID());
			pokemon.setPokemonId(storage.getNewPokemonID());
			this.allPokemon[i] = new PixelmonWrapper(this, pokemon.writeToNBT(new NBTTagCompound()), i);
			if(pokemon.isMega || pokemon.getFormEnum() == EnumForms.Mega || pokemon.getFormEnum() == EnumForms.MegaX || pokemon.getFormEnum() == EnumForms.MegaY) {
				this.allPokemon[i].isMega = true;
			}
		}
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

		if (newWrapper == null) {
			this.bc.endBattle(EnumBattleEndCause.FORCE);
			BidoofUnleashed.getInstance().getLogger().error("We were unable to find a pokemon to continue the battle...");
			BidoofUnleashed.getInstance().getLogger().debug("  Target ID: " + Arrays.toString(newPixelmonId));
			BidoofUnleashed.getInstance().getLogger().debug("Team Information:");
			for (PixelmonWrapper pw : this.allPokemon) {
				if (pw == null) {
					BidoofUnleashed.getInstance().getLogger().debug("  - Null Encountered");
				}
				BidoofUnleashed.getInstance().getLogger().debug("  - " + pw.pokemon.getName() + " (" + Arrays.toString(pw.getPokemonID()) + ")");
			}
			return null;
		}

		if (!this.bc.simulateMode) {
			oldPokemon.pokemon.catchInPokeball();

			oldPokemon.pokemon = null;
			EntityPixelmon pixelmon = (EntityPixelmon) PixelmonEntityList.createEntityFromNBT(
					Arrays.stream(this.allPokemon)
							.filter(pw -> Arrays.equals(pw.getPokemonID(), newPixelmonId))
							.findAny().orElseThrow(() -> new RuntimeException("Unable to get pokemon somehow...")).nbt,
					trainer.world
			);
			newWrapper.pokemon = pixelmon;
			if(newWrapper.isMega) {
				newWrapper.pokemon.isMega = true;
				this.evolution = newWrapper.getPokemonID();
			}
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

	@Override
	public void startBattle(BattleControllerBase bc) {
		this.controlledPokemon.clear();
		if (this.trainer.getBossMode() != EnumBossMode.NotBoss) {
			int lvl = 1;
			for (BattleParticipant p : bc.participants) {
				if (p.team != this.team && p instanceof PlayerParticipant) {
					lvl = Math.max(lvl, ((PlayerParticipant) p).getHighestLevel());
				}
			}

			lvl += this.trainer.getBossMode().extraLevels;
			PixelmonWrapper[] team = this.allPokemon;

			for (PixelmonWrapper pw : team) {
				pw.bc = bc;
				pw.setTempLevel(lvl);
			}
		}

		this.allPokemon[0].pokemon = (EntityPixelmon) PixelmonEntityList.createEntityFromNBT(this.allPokemon[0].nbt, this.trainer.world);
		this.controlledPokemon.add(this.allPokemon[0]);
		if(this.controlledPokemon.get(0).isMega) {
			this.controlledPokemon.get(0).pokemon.isMega = true;
			this.evolution = this.controlledPokemon.get(0).getPokemonID();
		}

		this.bpStartBattle(bc);
		PixelmonWrapper[] team = this.allPokemon;

		for (PixelmonWrapper pw : team) {
			pw.setHealth(pw.getMaxHealth());
		}

		EnumBattleAIMode battleAIMode = this.trainer.getBattleAIMode();
		if (battleAIMode == EnumBattleAIMode.Default) {
			battleAIMode = PixelmonConfig.battleAITrainer;
		}

		this.setBattleAI(battleAIMode.createAI(this));
		this.trainer.setBattleController(bc);
		this.trainer.startBattle(bc.getOpponents(this).get(0));
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

	private void bpStartBattle(BattleControllerBase bc) {
		this.bc = bc;
		PixelmonWrapper[] team = this.allPokemon;

		for (PixelmonWrapper p : team) {
			p.bc = bc;
			int levelCap = bc.rules.levelCap;
			if ((levelCap < PixelmonServerConfig.maxLevel || bc.rules.raiseToCap) && (p.getLevelNum() > levelCap || bc.rules.raiseToCap)) {
				p.setTempLevel(levelCap);
			}

			int maxHealth = p.getMaxHealth();
			int startHealth = p.getHealth();
			if (startHealth > maxHealth) {
				p.setHealth(maxHealth);
			}

			if (bc.rules.fullHeal) {
				p.setHealth(maxHealth);
				if (p.pokemon != null) {
					p.pokemon.func_70606_j((float) maxHealth);
				}

				p.clearStatus();

				for (Attack attack : p.getMoveset()) {
					if (attack != null) {
						attack.pp = attack.ppBase;
					}
				}
			}
		}

		PixelmonWrapper p;
		for (Iterator var11 = this.controlledPokemon.iterator(); var11.hasNext(); p.pokemon.battleController = bc) {
			p = (PixelmonWrapper) var11.next();
		}
	}
}
