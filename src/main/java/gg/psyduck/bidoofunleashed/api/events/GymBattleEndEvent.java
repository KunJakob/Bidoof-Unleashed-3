package gg.psyduck.bidoofunleashed.api.events;

import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

import javax.annotation.Nonnull;

/**
 * Represents the event triggered when a gym battle concludes. The leader can be either a NPC Trainer, or a player.
 * The event will also include a field stating whether or not the challenger was victorious in the battle.
 */
@Getter
@RequiredArgsConstructor
public class GymBattleEndEvent implements Event {

	private final Entity leader;
	private final Player challenger;
	private final BU3Battlable battlable;

	private final boolean won;

	@Nonnull
	@Override
	public Cause getCause() {
		return Sponge.getCauseStackManager().getCurrentCause();
	}
}
