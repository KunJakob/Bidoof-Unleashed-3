package gg.psyduck.bidoofunleashed.api.events;

import gg.psyduck.bidoofunleashed.gyms.Gym;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.entity.living.player.Player;

@Getter
@RequiredArgsConstructor
public class GymBattleEndEvent {

	private final Player challenger;
	private final Player leader;
	private final Gym gym;

	private final boolean won;
}
