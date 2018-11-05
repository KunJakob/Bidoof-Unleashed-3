package gg.psyduck.bidoofunleashed.api.battlables.arenas;

import com.flowpowered.math.vector.Vector3d;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocAndRot {
	private UUID world;
	private Vector3d position = new Vector3d();
	private Vector3d rotation = new Vector3d();
}
