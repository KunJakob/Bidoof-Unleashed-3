package gg.psyduck.agpr.data;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.rewards.Reward;
import lombok.Builder;
import lombok.Getter;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

@Getter
@Builder
public class GymData {

	// Initial data for a Gym
	private String name;
	private String requirement;
	private String badgeID;
	private int lvlCap;
	private List<Reward> rewards;
	private Arena arena;

	// Data filled in after creation
	private List<UUID> leaders = Lists.newArrayList();
	private transient Queue<UUID> queue = new LinkedList<>();

	public GymData(String name, String requirement, String badgeID, int lvlCap, Reward... rewards) {
		this.name = name;
		this.requirement = requirement;
		this.badgeID = badgeID;
		this.lvlCap = lvlCap;
		this.rewards = Lists.newArrayList(rewards);
	}

	public void addLeader(UUID uuid) {
		this.leaders.add(uuid);
	}

	public boolean canChallenge(Player player) {
		return false;
	}

	public void queue(UUID uuid) {
		this.queue.add(uuid);
	}

	/**
	 * Represents the areas a player will be teleported based on their relation to the current instance
	 */
	private class Arena {
		private Vector3d spectators;
		private Vector3d challenger;
		private Vector3d leader;
	}
}
