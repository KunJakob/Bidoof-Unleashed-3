package gg.psyduck.bidoofunleashed.battles.gyms.temporary;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class Challenge {
	private Entity leader;
	private Player challenger;
	private EnumBattleType battleType;

	private Map<Player, List<Boolean>> states = Maps.newHashMap();

	public Challenge(Entity leader, Player challenger, EnumBattleType type) {
		this.leader = leader;
		this.challenger = challenger;

		if(this.leader instanceof Player) {
			this.cacheAndLock((Player) this.leader);
		}

		this.cacheAndLock(this.challenger);

		this.battleType = type;
	}

	/**
	 * Locks a users team from leveling up during the battle. The last thing we want is for the leader of player to level
	 * above the level cap!
	 *
	 * @param player The target player to lock
	 */
	private void cacheAndLock(Player player) {
		PlayerStorage storage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) player).orElseThrow(() -> new RuntimeException("Critical error accessing pixelmon data for " + player.getName()));
		List<Boolean> states = Lists.newArrayList();

		Arrays.stream(storage.partyPokemon).filter(Objects::nonNull).forEach(nbt -> {
			states.add(nbt.getBoolean(NbtKeys.DOES_LEVEL));
			nbt.setBoolean(NbtKeys.DOES_LEVEL, false);
		});

		this.states.put(player, states);
	}

	public void restore() {
		if(this.leader instanceof Player) {
			this.restore((Player) this.leader);
		}
		this.restore(this.challenger);
	}

	/**
	 * Unlocks a users team, allowing them to once again gain experience points!
	 *
	 * @param player The target player to unlock
	 */
	private void restore(Player player) {
		PlayerStorage storage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) player).orElseThrow(() -> new RuntimeException("Critical error accessing pixelmon data for " + player.getName()));
		AtomicInteger ai = new AtomicInteger();
		Arrays.stream(storage.partyPokemon).filter(Objects::nonNull).forEach(nbt -> nbt.setBoolean(NbtKeys.DOES_LEVEL, this.states.get(player).get(ai.getAndIncrement())));
	}
}
