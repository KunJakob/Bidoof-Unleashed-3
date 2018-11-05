package gg.psyduck.bidoofunleashed.gyms;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	private Integer timesDefeated;

	public Badge() {
		this("Default", "minecraft:barrier");
	}

	public Badge(String name, String itemType) {
		this.name = name;
		this.itemType = itemType;
		this.leader = null;
		this.npcName = null;
		this.obtained = null;
		this.timesDefeated = null;
	}

	private Badge(String name, String itemType, UUID leader) {
		this.name = name;
		this.itemType = itemType;
		this.leader = leader;
		this.npcName = null;
		this.obtained = Date.from(Instant.now());
		this.timesDefeated = 0;
	}

	private Badge(String name, String itemType, String npcName) {
		this.name = name;
		this.itemType = itemType;
		this.leader = null;
		this.npcName = npcName;
		this.obtained = Date.from(Instant.now());
		this.timesDefeated = 0;
	}

	public Badge name(String name) {
		return new Badge(name, this.itemType);
	}

	public Badge itemType(String itemType) {
		return new Badge(this.name, itemType);
	}

	public Badge fill(UUID leader) {
		return new Badge(this.name, this.itemType, leader);
	}

	public Badge fill(String npcName) {
		return new Badge(this.name, this.itemType, npcName);
	}

	public void incWins() {
		++this.timesDefeated;
	}
}
