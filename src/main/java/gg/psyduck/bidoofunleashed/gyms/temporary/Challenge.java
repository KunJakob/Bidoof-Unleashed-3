package gg.psyduck.bidoofunleashed.gyms.temporary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

@Getter
@AllArgsConstructor
public class Challenge {
	private Entity leader;
	private Player challenger;
}
