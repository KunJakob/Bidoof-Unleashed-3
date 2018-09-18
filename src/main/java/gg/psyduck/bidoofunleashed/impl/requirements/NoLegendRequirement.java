package gg.psyduck.bidoofunleashed.impl.requirements;

import com.nickimpact.impactor.json.Typing;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.battles.gyms.Gym;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Arrays;

@Typing("no-legend")
public class NoLegendRequirement implements Requirement {

	private final String type = "no-legend";

	@Override
	public String id() {
		return this.type;
	}

	@Override
	public boolean passes(Gym gym, Player player) throws Exception {
		PlayerStorage storage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) player).orElseThrow(() -> new Exception("Critical error accessing pixelmon data for " + player.getName()));
		return Arrays.stream(storage.partyPokemon).noneMatch(nbt -> EnumPokemon.legendaries.contains(nbt.getString(NbtKeys.NAME)));
	}

	@Override
	public void onInvalid(Gym gym, Player player) {
		player.sendMessage(MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.REQUIREMENT_LEGENDS, null, null));
	}

	@Override
	public Requirement supply(String... args) throws Exception {
		return this;
	}
}
