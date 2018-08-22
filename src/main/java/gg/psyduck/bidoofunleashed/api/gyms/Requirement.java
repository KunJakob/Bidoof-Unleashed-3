package gg.psyduck.bidoofunleashed.api.gyms;

import gg.psyduck.bidoofunleashed.gyms.Gym;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nullable;

/**
 * A requirement is a condition which the player must pass before being queued for a gym battle.
 * A gym can have a series of requirements, and it will be up to the player to pass the requirement.
 */
public interface Requirement {

	/**
	 * Specifies whether a player passes a certain requirement.
	 *
	 * @param gym The gym specifying the requirement
	 * @param player The player attempting to challenge the gym
	 * @return True if the player passes, false otherwise
	 */
	boolean passes(Gym gym, Player player);

	/**
	 * A method which should act as a informative bridge to the player, specifying
	 * why they failed to meet a requirement.
	 *
	 * @param gym The gym specifying the requirement
	 * @param player The player attempting to challenge the gym
	 */
	void onInvalid(Gym gym, Player player);
}
