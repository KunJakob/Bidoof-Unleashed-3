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

	public Badge() {
		this("Default", "minecraft:barrier");
	}

	public Badge(String name, String itemType) {
		this.name = name;
		this.itemType = itemType;
	}

	public Badge name(String name) {
		return new Badge(name, this.itemType);
	}

	public Badge itemType(String itemType) {
		return new Badge(this.name, itemType);
	}
}
