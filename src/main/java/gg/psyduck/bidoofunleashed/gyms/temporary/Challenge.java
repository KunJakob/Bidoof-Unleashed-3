package gg.psyduck.bidoofunleashed.gyms.temporary;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import lombok.Getter;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.Map;
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
	 * Locks a users team from leveling up during the battle. The last thing we want is for the leader or player to level
	 * above the level cap!
	 *
	 * @param player The target player to lock
	 */
	private void cacheAndLock(Player player) {
		PlayerPartyStorage storage = Pixelmon.storageManager.getParty(player.getUniqueId());
		List<Boolean> states = Lists.newArrayList();

		storage.getTeam().forEach(pokemon -> {
			states.add(pokemon.doesLevel());
			pokemon.setDoesLevel(false);
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
		PlayerPartyStorage storage = Pixelmon.storageManager.getParty(player.getUniqueId());
		AtomicInteger ai = new AtomicInteger();
		storage.getTeam().forEach(pokemon -> pokemon.setDoesLevel(this.states.get(player).get(ai.getAndIncrement())));
	}
}
