package gg.psyduck.bidoofunleashed.gyms;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.rewards.Reward;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.TrainerParticipant;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import lombok.Builder;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

@Getter
@Builder
public class Gym {

	// Initial data for a Gym
	private String name;
	private List<Requirement> requirements;
	private Badge badge;
	private int lvlCap;
	private List<Reward> rewards;
	private Arena arena;

	// Data filled in after creation
	private Map<UUID, EnumLeaderType> leaders = Maps.newHashMap();
	private List<BattleRules> battleRules;
	private transient Queue<UUID> queue = new LinkedList<>();

	public Gym(String name, Badge badge, int lvlCap, List<Requirement> requirements, Reward... rewards) {
		this.name = name;
		this.badge = badge;
		this.lvlCap = lvlCap;
		this.requirements = requirements;
		this.rewards = Lists.newArrayList(rewards);
	}

	public void addLeader(UUID uuid, EnumLeaderType type) {
		this.leaders.put(uuid, type);
	}

	public boolean canChallenge(Player player) {
		return false;
	}

	public void queue(UUID uuid) {
		this.queue.add(uuid);
	}

	public void startBattle(Entity leader, Player challenger) {
		BattleParticipant bpL;
		if(leader instanceof NPCTrainer) {
			bpL = new TrainerParticipant((NPCTrainer) leader, (EntityPlayerMP) challenger, ((NPCTrainer) leader).getPokemonStorage().count());
		} else {
			EntityPixelmon starter = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) leader).get().getFirstAblePokemon((World)leader.getWorld());
			bpL = new PlayerParticipant((EntityPlayerMP) leader, starter);
		}

		EntityPixelmon starter = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) challenger).get().getFirstAblePokemon((World)challenger.getWorld());
		BattleControllerBase bcb = new BattleControllerBase(bpL, new PlayerParticipant((EntityPlayerMP) challenger, starter), new BattleRules());
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
