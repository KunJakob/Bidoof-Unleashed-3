package gg.psyduck.bidoofunleashed.api.events;

import com.nickimpact.impactor.api.events.CancellableEvent;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Represents the event triggered when a player challenges a gym and is accepted. The leader for this
 * event can be either a NPC Trainer, or a player, considering both are supported by the plugin.
 *
 * This event can be cancelled, so if set to cancelled, the battle will not start.
 */
@Getter
@RequiredArgsConstructor
public class GymBattleStartEvent extends CancellableEvent {

	/** The leader of the gym. This can be either a NPC Trainer, or a Player */
	private final Entity leader;

	/** The challenger of the gym */
	private final Player challenger;

	/** The battlable being challenged */
	private final BU3Battlable battlable;
}
