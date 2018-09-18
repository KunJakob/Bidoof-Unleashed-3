package gg.psyduck.bidoofunleashed.battles.gyms;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * The data holder for a badge received by a player.
 */
@Getter
public class Badge {

	/** The name of the badge */
	 private final String name;

	/** The item type for the badge */
	@SerializedName("item-type")
	private final String itemType;

	/** The name of the leader the badge was won from */
	private final UUID leader;

	/** If the leader is an NPC, specify the NPC's name */
	private final String npcName;

	/** The date the badge was obtained */
	private final Date obtained;

	/** The winning team for the player */
	private final List<String> team;

	public Badge(String name, String itemType) {
		this.name = name;
		this.itemType = itemType;
		this.leader = null;
		this.npcName = null;
		this.obtained = null;
		this.team = null;
	}

	private Badge(String name, String itemType, UUID leader, List<String> team) {
		this.name = name;
		this.itemType = itemType;
		this.leader = leader;
		this.npcName = null;
		this.obtained = Date.from(Instant.now());
		this.team = team;
	}

	private Badge(String name, String itemType, String npcName, List<String> team) {
		this.name = name;
		this.itemType = itemType;
		this.leader = null;
		this.npcName = npcName;
		this.obtained = Date.from(Instant.now());
		this.team = team;
	}

	public Badge fill(UUID leader, List<String> team) {
		return new Badge(this.name, this.itemType, leader, team);
	}

	public Badge fill(String npcName, List<String> team) {
		return new Badge(this.name, this.itemType, npcName, team);
	}
}
