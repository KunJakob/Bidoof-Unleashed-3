package gg.psyduck.bidoofunleashed.api.battlables;

import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import org.spongepowered.api.entity.living.player.Player;

import java.util.UUID;

public interface BU3BattleBase<T extends BU3BattleBase> {

	default String toPermission(String prefix) {
		return String.format("bu3.%s.%s", prefix, this.getName().toLowerCase().replaceAll(" ", "_"));
	}

	T initialize();

	UUID getUuid();

	String getName();

	Category getCategory();

	int getWeight();

	/**
	 * Checks whether the implementing instance has been entirely setup. In other words, is there any detail missing
	 * that would prevent the implementation as expected?
	 *
	 * Basically, this algorithm is just a bunch of null checks along with any additional parameter checks.
	 *
	 * @return True if the implementation is ready, false otherwise
	 */
	boolean isReady();

	/**
	 * States whether or not a player can challenge the implementing instance.
	 *
	 * @param player The player in question
	 * @return Whether the player can challenge the implementing instance
	 */
	boolean canChallenge(Player player);

	/**
	 * States whether or not a player can access the battle instance. This typically relates to just simply
	 * teleporting or walking into the gym itself.
	 *
	 * @param player The player in question
	 * @return True if they can access it, false otherwise
	 */
	boolean canAccess(Player player);
}
