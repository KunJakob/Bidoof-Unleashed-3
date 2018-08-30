package gg.psyduck.bidoofunleashed.impl.requirements;


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
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Typing("level")
public class LevelRequirement implements Requirement {

	private final String type = "level";

	@Override
	public String id() {
		return type;
	}

	@Override
	public boolean passes(Gym gym, Player player) throws Exception {
		PlayerStorage storage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) player).orElseThrow(() -> new Exception("Missing player storage data for " + player.getName()));
		for(NBTTagCompound nbt : storage.partyPokemon) {
			if(nbt.getInteger(NbtKeys.LEVEL) > gym.getBattleRules().levelCap) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onInvalid(Gym gym, Player player) {
		Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
		tokens.put("bu3_level_cap", s -> Optional.of(Text.of(gym.getBattleRules().levelCap)));
		player.sendMessage(MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.REQUIREMENT_LEVELCAP, tokens, null));
	}

	@Override
	public LevelRequirement supply(String... args) throws Exception {
		return this;
	}
}
