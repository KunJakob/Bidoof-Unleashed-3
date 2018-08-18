package gg.psyduck.bidoofunleashed.gyms;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.item.ItemType;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * The data holder for a badge received by a player.
 */
@Getter
@RequiredArgsConstructor
public class Badge {

	/** The item type for the badge */
	private final ItemType badge;

	/** The name of the leader the badge was won from */
	private final UUID leader;

	/** The date the badge was obtained */
	private final Date obtained;

	/** The winning team for the player */
	private final List<String> team;
}
