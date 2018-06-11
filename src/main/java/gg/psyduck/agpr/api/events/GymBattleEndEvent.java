package gg.psyduck.agpr.api.events;

import gg.psyduck.agpr.data.GymData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.entity.living.player.Player;

@Getter
@RequiredArgsConstructor
public class GymBattleEndEvent {

	private final Player challenger;
	private final Player leader;
	private final GymData gym;

	private final boolean won;
}
