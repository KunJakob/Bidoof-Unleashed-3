package gg.psyduck.bidoofunleashed.api.pixelmon.participants;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.ai.MoveChoice;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.ParticipantType;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.entities.npcs.NPCChatting;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleAIMode;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;

import java.util.List;
import java.util.UUID;

public class ChattingNPCParticipant extends BattleParticipant {

	private NPCChatting npc;
	private List<Pokemon> team;

	public ChattingNPCParticipant(NPCChatting npc, List<Pokemon> team, int numControlledPokemon) {
		super(numControlledPokemon);
		this.npc = npc;
		this.team = team;
		this.loadParty(team);
	}

	@Override
	public boolean hasMorePokemonReserve() {
		return this.countAblePokemon() > this.getActiveUnfaintedPokemon().size() + this.switchingOut.size();
	}

	@Override
	public boolean canGainXP() {
		return false;
	}

	@Override
	public void endBattle() {
		for(PixelmonWrapper pw : this.controlledPokemon) {
			pw.getBattleStats().clearBattleStats();
			pw.clearStatus();
			if(pw.entity != null) {
				pw.entity.endBattle();
				pw.entity.setDead();
				pw.entity.unloadEntity();
			}
		}
	}

	@Override
	public TextComponentBase getName() {
		return new TextComponentString(npc.getName());
	}

	@Override
	public MoveChoice getMove(PixelmonWrapper wrapper) {
		return this.getBattleAI().getNextMove(wrapper);
	}

	@Override
	public PixelmonWrapper switchPokemon(PixelmonWrapper old, UUID uuid) {
		int index = old.getControlledIndex();
		if (index == -1 && this.bc.simulateMode)
			index = 0;

		String beforeName = old.getNickname();
		old.beforeSwitch();
		if (!old.isFainted() && !old.nextSwitchIsMove) {
			this.bc.sendToOthers("playerparticipant.withdrew", this, "", beforeName);
		}

		PixelmonWrapper newWrapper = this.getPokemonFromParty(uuid);
		if (!bc.simulateMode) {
			old.entity.retrieve();
			old.entity = null;
			newWrapper.entity = releasePokemon(uuid);
		}

		controlledPokemon.set(index, newWrapper);
		newWrapper.getBattleAbility().beforeSwitch(newWrapper);
		if (!this.bc.simulateMode) {
			this.bc.sendToOthers("pixelmon.battletext.sentout", this, npc.getName(), newWrapper.getNickname());
			this.bc.participants.forEach(BattleParticipant::updateOtherPokemon);
		}

		newWrapper.afterSwitch();
		return newWrapper;
	}

	private EntityPixelmon releasePokemon(UUID newPokemonID) {
		for (PixelmonWrapper wrapper : this.allPokemon)
			if (wrapper.getInnerLink().getUUID().equals(newPokemonID))
				return wrapper.getInnerLink().getOrSpawnPixelmon(npc);
		return null;
	}

	@Override
	public boolean checkPokemon() {
		for(Pokemon pokemon : this.team) {
			if(pokemon.getMoveset().size() == 0) {
				if (PixelmonConfig.printErrors) {
					Pixelmon.LOGGER.info("Couldn't load Pokémon's moves.");
				}
				return false;
			}
		}

		return true;
	}

	@Override
	public void updatePokemon(PixelmonWrapper pixelmonWrapper) {}

	@Override
	public EntityLivingBase getEntity() {
		return this.npc;
	}

	@Override
	public void updateOtherPokemon() {}

	@Override
	public ParticipantType getType() {
		return ParticipantType.Trainer;
	}

	@Override
	public void getNextPokemon(int pos) {
		for(PixelmonWrapper wrapper : this.controlledPokemon) {
			if(wrapper.battlePosition == pos) {
				this.bc.switchPokemon(wrapper.getPokemonUUID(), this.getBattleAI().getNextSwitch(wrapper), true);
				return;
			}
		}
	}

	@Override
	public UUID getNextPokemonUUID() {
		return null;
	}

	@Override
	public void startBattle(BattleControllerBase bc) {
		for(int i = 0; i < this.numControlledPokemon; i++) {
			if(this.allPokemon.length > i) {
				EntityPixelmon pokemon = this.releasePokemon(this.allPokemon[i].getPokemonUUID());
				if(pokemon != null) {
					PixelmonWrapper pw = this.getPokemonFromParty(pokemon);
					pw.battlePosition = i;
					this.controlledPokemon.add(pw);
				}
			}
		}

		super.startBattle(bc);

		for(PixelmonWrapper pw : allPokemon) {
			pw.setHealth(pw.getMaxHealth());
		}

		this.setBattleAI(EnumBattleAIMode.Advanced.createAI(this));
	}
}
