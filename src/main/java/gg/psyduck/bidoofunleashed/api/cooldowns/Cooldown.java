package gg.psyduck.bidoofunleashed.api.cooldowns;

import org.spongepowered.api.entity.living.player.Player;

public interface Cooldown {

	/**
	 * Simply makes a call to the player data for the specified battlable instance. States whether or not
	 * the player is able to battle the instance based on any cooldown set for this instance.
	 *
	 * @param player The player to check against
	 * @return Whether the player can challenge the implementation based on their cooldown references.
	 */
	boolean checkCooldown(Player player);

	long getCooldown();
}
