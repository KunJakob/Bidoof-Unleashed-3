package gg.psyduck.bidoofunleashed.gyms;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * The data holder for a badge received by a player.
 */
@Getter
@RequiredArgsConstructor
public class Badge {

	/** The name of the badge */
	 private final String name;

	/** The item type for the badge */
	@SerializedName("item-type")
	private final String itemType;

	/** The name of the leader the badge was won from */
	private final UUID leader;

	/** The date the badge was obtained */
	private final Date obtained;

	/** The winning team for the player */
	private final List<String> team;

	public Badge fill(UUID leader, Date obtained, List<String> team) {
		return new Badge(this.name, this.itemType, leader, obtained, team);
	}
}
