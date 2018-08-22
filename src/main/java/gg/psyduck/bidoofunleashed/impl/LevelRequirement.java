package gg.psyduck.bidoofunleashed.impl;


import com.google.common.collect.Maps;
import com.nickimpact.impactor.json.Typing;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;

@Typing("level")
public class LevelRequirement implements Requirement {

	private final String type = "level";

	@Override
	public boolean passes(Gym gym, Player player) {
		PlayerStorage storage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) player).get();
		for(NBTTagCompound nbt : storage.partyPokemon) {
			if(nbt.getInteger(NbtKeys.LEVEL) > gym.getBattleRules().levelCap) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onInvalid(Gym gym, Player player) {
		Map<String, Object> variables = Maps.newHashMap();
		variables.put("bu3_level_cap", gym.getBattleRules().levelCap);
		player.sendMessage(MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.REQUIREMENT_LEVELCAP, null, variables));
	}
}
