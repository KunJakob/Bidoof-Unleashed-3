package gg.psyduck.bidoofunleashed.api.battlables.arenas;

import com.flowpowered.math.vector.Vector3d;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents the areas a player will be teleported based on their relation to the current instance
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Arena {
	private LocAndRot spectators = new LocAndRot();
	private LocAndRot challenger = new LocAndRot();
	private LocAndRot leader = new LocAndRot();

    public boolean isSetup() {
		return this.challenger != null && this.isProper(this.challenger) && this.leader != null && this.isProper(this.leader);
	}

	public boolean isProper(LocAndRot location) {
		return !location.getPosition().equals(new Vector3d(0, 0, 0));
	}
}