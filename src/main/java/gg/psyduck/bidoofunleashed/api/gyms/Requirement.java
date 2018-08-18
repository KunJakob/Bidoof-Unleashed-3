package gg.psyduck.bidoofunleashed.api.gyms;

import org.spongepowered.api.entity.living.player.Player;

public interface Requirement {

	/**
	 * Specifies whether a player passes a certain requirement.
	 *
	 * @param player The player in question
	 * @return True if the player passes, false otherwise
	 */
	boolean apply(Player player);
}
