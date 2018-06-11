package gg.psyduck.agpr.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * The data holder for a badge received by a player.
 */
@Getter
@RequiredArgsConstructor
public class Badge {

	/** The name of the gym */
	private final String gym;

	/** The item ID for the badge */
	private final String badge;

	/** The name of the leader the badge was won from */
	private final String leader;

	/** The date the badge was obtained */
	private final Date obtained;

	/** The winning team for the player */
	private final List<String> team;
}
