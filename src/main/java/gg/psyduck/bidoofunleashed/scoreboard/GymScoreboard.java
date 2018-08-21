package gg.psyduck.bidoofunleashed.scoreboard;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.objective.Objective;

public class GymScoreboard {

	public void create() {
		Scoreboard scoreboard = Scoreboard.builder().build();
		Objective gymStatus = Objective.builder().name("Gym Statuses").build();

		scoreboard.addObjective(gymStatus);

	}
}
