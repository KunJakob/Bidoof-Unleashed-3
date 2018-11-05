package gg.psyduck.bidoofunleashed.api.gyms;

import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import org.spongepowered.api.entity.living.player.Player;

/**
 * A requirement is a condition which the player must pass before being queued for a gym battle.
 * A gym can have a series of requirements, and it will be up to the player to pass the requirement.
 */
public interface Requirement {

	/**
	 * Represents the ID of the requirement (used for command additions)
	 *
	 * @return The requirement's ID
	 */
	String id();

	/**
	 * Specifies whether a player passes a certain requirement.
	 *
	 * @param battlable The gym specifying the requirement
	 * @param player The player attempting to challenge the gym
	 * @return True if the player passes, false otherwise
	 */
	boolean passes(BU3Battlable battlable, Player player) throws Exception;

	/**
	 * A method which should act as a informative bridge to the player, specifying
	 * why they failed to meet a requirement.
	 *
	 * @param battlable The gym specifying the requirement
	 * @param player The player attempting to challenge the gym
	 */
	void onInvalid(BU3Battlable battlable, Player player);

	/**
	 * Given a argument array, supply the requirement with any needed fields. Most requirements will likely
	 * have this method simply implemented as a no-op.
	 *
	 * @param args The args to parse for the requirement
	 * @throws Exception In the event anything regarding the arguments is invalid
	 */
	Requirement supply(String... args) throws Exception;
}
