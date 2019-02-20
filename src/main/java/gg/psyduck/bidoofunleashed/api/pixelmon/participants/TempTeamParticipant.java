package gg.psyduck.bidoofunleashed.api.pixelmon.participants;

import com.pixelmonmod.pixelmon.PixelmonMethods;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.comm.ChatHandler;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TempTeamParticipant extends PlayerParticipant {

	public TempTeamParticipant(EntityPlayerMP p, EntityPixelmon... startingPixelmon) {
		super(p, startingPixelmon);
	}

	public TempTeamParticipant(EntityPlayerMP player, List<Pokemon> team, int starting) {
		super(player, team, starting);
		BidoofUnleashed.getInstance().getLogger().debug(Arrays.toString(this.allPokemon));
		BidoofUnleashed.getInstance().getLogger().debug(this.controlledPokemon.toString());
	}

	@Override
	public PixelmonWrapper switchPokemon(PixelmonWrapper pw, UUID newID) {
		double x = this.player.posX;
		double y = this.player.posY;
		double z = this.player.posZ;
		String before = pw.getNickname();
		pw.beforeSwitch();
		if(!pw.isFainted() && !pw.nextSwitchIsMove) {
			ChatHandler.sendBattleMessage(this.player, "playerparticipant.enough", pw.getNickname());
			this.bc.sendToOthers("playerparticipant.withdrew", this, this.player.getDisplayName().getUnformattedText(), before);
		}

		PixelmonWrapper newWrapper = this.getPokemonFromParty(newID);
		if(!this.bc.simulateMode) {
			pw.entity.retrieve();
			pw.entity = null;

			EntityPixelmon pixelmon = newWrapper.pokemon.getOrSpawnPixelmon(this.player);
			PixelmonMethods.displacePokemonIfShouldered(this.player, newID);
			pixelmon.motionX = pixelmon.motionY = pixelmon.motionZ = 0.0D;
			pixelmon.setLocationAndAngles(x, y, z, this.player.getRotationYawHead(), 0.0F);
			newWrapper.entity = pixelmon;
		}

		newWrapper.battlePosition = pw.battlePosition;
		newWrapper.getBattleAbility().beforeSwitch(newWrapper);
		String newNickname = newWrapper.getNickname();
		ChatHandler.sendBattleMessage(this.player, "playerparticipant.go", newNickname);
		this.bc.sendToOthers("battlecontroller.sendout", this, this.player.getDisplayName().getUnformattedText(), newNickname);
		int index = this.controlledPokemon.indexOf(pw);
		this.controlledPokemon.set(index, newWrapper);
		this.bc.participants.forEach(BattleParticipant::updateOtherPokemon);
		newWrapper.afterSwitch();
		return newWrapper;
	}

	@Override
	public UUID getNextPokemonUUID() {
		Optional<Pokemon> first = Arrays.stream(this.allPokemon).filter(pokemon ->
			pokemon.pokemon.getHealth() > 0 && !pokemon.pokemon.isEgg() && pokemon.pokemon.getPixelmonIfExists() == null
		).map(pw -> pw.pokemon).findAny();
		return first.map(p -> p.getOrSpawnPixelmon(this.player).getPokemonData().getUUID()).orElse(null);
	}

	@Override
	public boolean checkPokemon() {
		for(Pokemon pokemon : Arrays.stream(this.allPokemon).map(pw -> pw.pokemon).collect(Collectors.toList())) {
			if(pokemon.getMoveset().isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
