package gg.psyduck.bidoofunleashed.api.events;

import com.nickimpact.impactor.api.events.CancellableEvent;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

@Getter
@RequiredArgsConstructor
public class GymBattleEndEvent extends CancellableEvent {

	private final Player challenger;
	private final Entity leader;
	private final Gym gym;

	private final boolean won;
}
