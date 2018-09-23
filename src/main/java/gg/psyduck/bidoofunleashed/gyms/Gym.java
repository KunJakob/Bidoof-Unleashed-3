package gg.psyduck.bidoofunleashed.gyms;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.time.Time;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.pixelmon.participants.TempTeamParticipant;
import gg.psyduck.bidoofunleashed.api.pixelmon.specs.BU3PokemonSpec;
import gg.psyduck.bidoofunleashed.api.rewards.BU3Reward;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.rules.clauses.BattleClauseRegistry;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.api.battlables.battletypes.BattleType;
import gg.psyduck.bidoofunleashed.config.ConfigKeys;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.temporary.BattleRegistry;
import gg.psyduck.bidoofunleashed.gyms.temporary.Challenge;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

@Getter
public class Gym implements BU3Battlable {

	private static final File BASE_PATH_GYMS = new File("bidoof-unleashed-3/json/gyms/");

	private UUID uuid;
	private String id;
	private String name;
	private Badge badge;
	private Arena arena;
	private Category category;
	private int weight;
	private int cooldown;

	private BattleType initial;
	private BattleType rematch;

	private List<UUID> leaders;
	private int npcs = 0;

	private transient Queue<UUID> queue = new LinkedList<>();
	private transient boolean open = false;

	private Gym(Builder builder) {
		this.uuid = UUID.randomUUID();
		this.id = builder.id;
		this.name = builder.name;
		this.badge = builder.badge;
		this.arena = builder.arena != null ? builder.arena : new Arena();
		this.leaders = builder.leaders;
		this.category = new Category(builder.category);

		List<Gym> mappings = BidoofUnleashed.getInstance().getDataRegistry().sortedGyms().get(this.category);

		this.cooldown = builder.cooldown;
		this.weight = builder.weight >= 0 ? builder.weight : mappings != null ? mappings.size() : 0;
		this.initial = builder.initial.path(new File(BASE_PATH_GYMS, this.name + "/initial.pool"));
		this.rematch = builder.rematch.path(new File(BASE_PATH_GYMS, this.name + "/rematch.pool"));

		this.initialize();
	}

	public void addPlayerLeader(UUID uuid) {
		this.leaders.add(uuid);
	}

	public void removePlayerLeader(UUID uuid) {
		this.leaders.remove(uuid);
	}

	public void addNPCLeader(NPCTrainer npc) {
		this.npcs++;
		npc.getEntityData().setString("BU3-Gym", this.getUuid().toString());
	}

	public void removeNPCLeader(NPCTrainer npc) {
		this.npcs--;
	}

	@Override
	public boolean checkCooldown(Player player) {
		PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());
		if(!pd.afterCooldownPeriod(this)) {
			LocalDate till = pd.getCooldowns().get(this.getName()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			Duration duration = Duration.between(LocalDate.now(), till);

			Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
			tokens.put("bu3_gym", src -> Optional.of(Text.of(this.getName())));
			tokens.put("bu3_cooldown_time", src -> Optional.of(Text.of(new Time(duration.getSeconds()).toString())));

			player.sendMessage(MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.MISC_CHALLENGE_COOLDOWN, tokens, null));
			return false;
		}
		return true;
	}

	@Override
	public boolean isReady() {
		return this.id != null && this.name != null && this.badge != null && this.arena.isSetup();
	}

	public boolean canChallenge(Player player) {
		return this.open && player.hasPermission("bu3.gyms." + this.id.toLowerCase() + ".contest") && this.checkCooldown(player);
	}

	@Override
	public boolean canAccess(Player player) {
		return this.isReady() && player.hasPermission("bu3.gyms." + this.id.toLowerCase() + ".access");
	}

	public boolean queue(Player player) {
		if(this.canAccess(player) && this.canChallenge(player)) {
			this.queue.add(player.getUniqueId());
			return true;
		}

		return false;
	}

	/**
	 * Starts a battle between two players. Since battles with NPCs are essentially pre-initialized, we really just need to do
	 * the work this method does inside the battle listener to avoid circular references.
	 *
	 * @param leader The leader of the gym accepting the challenge
	 * @param challenger The challenger trying to prove their worth
	 */
	public boolean startBattle(Player leader, Player challenger, List<BU3PokemonSpec> team) {
		BattleParticipant bpL = new TempTeamParticipant((EntityPlayerMP) leader);
		bpL.allPokemon = new PixelmonWrapper[team.size()];

		PlayerStorage lStorage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) leader).orElse(null);
		if(lStorage == null) {
			return false;
		}

		for(int i = 0; i < team.size(); i++) {
			EntityPixelmon pokemon = team.get(i).create((World) leader.getWorld());
			if(team.get(i).level == null) {
				pokemon.getLvl().setLevel(this.getBattleSettings(this.getBattleType(challenger)).getLvlCap());
			}
			pokemon.loadMoveset();
			pokemon.caughtBall = EnumPokeballs.PokeBall;
			pokemon.setOwnerId(leader.getUniqueId());
			pokemon.setPokemonId(lStorage.getNewPokemonID());
			bpL.allPokemon[i] = new PixelmonWrapper(bpL, pokemon, i);
		}

		bpL.controlledPokemon = Collections.singletonList(bpL.allPokemon[0]);

		leader.setLocationAndRotationSafely(new Location<>(leader.getWorld(), arena.leader.position), arena.leader.rotation);

		challenger.setLocationAndRotationSafely(new Location<>(challenger.getWorld(), arena.challenger.position), arena.challenger.rotation);
		PlayerStorage storage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) challenger).orElse(null);
		if(storage == null) {
			return false;
		}

		EntityPixelmon cs = storage.getFirstAblePokemon((World) challenger.getWorld());
		if(cs == null) {
			return false;
		}

		BattleRegistry.register(new Challenge(leader, challenger, this.getBattleType(challenger)), this);
		new BattleControllerBase(bpL, new PlayerParticipant((EntityPlayerMP) challenger, cs), this.initial.getBattleRules()); // Need to ensure we are checking what type of battle it is
		BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(challenger.getUniqueId()).updateCooldown(this);
		return true;
	}

	public EnumBattleType getBattleType(Player player) {
		PlayerData data = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());
		return data.hasBadge(this.badge) ? EnumBattleType.Rematch : EnumBattleType.First;
	}

	public Gym initialize() {
		this.initial.init();
		this.rematch.init();

		if(this.queue == null) {
			this.queue = new LinkedList<>();
		}

		if(this.npcs > 0) {
			this.open = true;
		}

		return this;
	}

	public boolean open() {
		if(this.queue != null && this.arena.isSetup()) {
			this.open = true;
			return true;
		}

		return false;
    }

    public boolean close() {
		if(!this.open) {
			return false;
		}

		this.open = false;
		return true;
    }

    public BattleType getBattleSettings(EnumBattleType type) {
		if(type == EnumBattleType.First) {
			return this.initial;
		} else {
			return this.rematch;
		}
    }

    public void copy(Gym edit) {
		this.id = edit.id;
		this.name = edit.name;
		this.badge = edit.badge;
		this.arena = edit.arena;
		this.cooldown = edit.cooldown;
		this.weight = edit.weight;
		this.category = edit.category;
		this.initial = edit.initial;
		this.rematch = edit.rematch;
		this.leaders = edit.leaders;
    }

	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {
		Gym.Builder builder = new Builder();
		builder.name = this.name;
		builder.id = this.id;
		builder.badge = this.badge;
		builder.arena = this.arena;
		builder.category = this.category.getId();
		builder.weight = this.weight;
		builder.cooldown = this.cooldown;

		builder.initial = this.initial;
		builder.rematch = this.rematch;

		builder.leaders = this.leaders;
		return builder;
	}

	/**
	 * Represents the areas a player will be teleported based on their relation to the current instance
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Arena {
		private LocAndRot spectators = new LocAndRot();
		private LocAndRot challenger = new LocAndRot();
		private LocAndRot leader = new LocAndRot();

		boolean isSetup() {
			return this.challenger != null && this.isProper(this.challenger) && this.leader != null && this.isProper(this.leader);
		}

		private boolean isProper(LocAndRot location) {
			return !location.position.equals(new Vector3d(0, 0, 0));
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LocAndRot {
		private UUID world;
		private Vector3d position = new Vector3d();
		private Vector3d rotation = new Vector3d();
	}

	public static class Builder {
		private String id;
		public String name;
		private Badge badge = new Badge();
		private Arena arena;
		private String category = "default";
		private int weight = -1;
		private int cooldown = BidoofUnleashed.getInstance().getConfig().get(ConfigKeys.DEFAULT_COOLDOWN);

		private BattleType initial = new BattleType();
		private BattleType rematch = new BattleType();

		private List<UUID> leaders = Lists.newArrayList();

		public Builder id(String id) {
			Preconditions.checkArgument(BidoofUnleashed.getInstance().getDataRegistry().getGyms().stream().noneMatch(gym -> gym.getId().equalsIgnoreCase(id)), "Can't have two gyms with the same ID");
			this.id = id.toLowerCase().replaceAll(" ", "_");
			if(this.name == null) {
				this.name = id;
			}
			return this;
		}

		public Builder name(String name) {
			Preconditions.checkArgument(BidoofUnleashed.getInstance().getDataRegistry().getGyms().stream().noneMatch(gym -> gym.getName().equalsIgnoreCase(name)), "Can't have two gyms with the same name");
			this.name = name;
			if(this.id == null) {
				this.id = name.toLowerCase().replaceAll(" ", "_");
			}
			return this;
		}

		public Builder category(String category) {
			this.category = category;
			return this;
		}

		public Builder weight(int weight) {
			Preconditions.checkArgument(weight > -1, "Weights must be >= 0");
			this.weight = weight;
			return this;
		}

		public Builder cooldown(int minutes) {
			this.cooldown = minutes;
			return this;
		}

		public Builder badgeName(String name) {
			this.badge = badge.name(name);
			return this;
		}

		public Builder badgeType(String itemType) {
			this.badge = badge.itemType(itemType);
			return this;
		}

		public Builder arena(Arena arena) {
			this.arena = arena;
			return this;
		}

		public Builder clause(EnumBattleType type, String clause) {
			Preconditions.checkArgument(BattleClauseRegistry.getClauseRegistry().hasClause(clause), "Invalid clause: " + clause);
			switch (type) {
				case First:
					initial.addClause(clause);
					break;
				case Rematch:
					rematch.addClause(clause);
					break;
			}
			return this;
		}

		public Builder clauses(EnumBattleType type, String... clauses) {
			Arrays.stream(clauses).forEach(clause -> {
				Preconditions.checkArgument(BattleClauseRegistry.getClauseRegistry().hasClause(clause), "Invalid clause: " + clause);
				this.clause(type, clause);
			});
			return this;
		}

		public Builder rule(EnumBattleType type, String rule) {
			switch (type) {
				case First:
					initial.addRule(rule);
					break;
				case Rematch:
					rematch.addRule(rule);
					break;
			}
			return this;
		}

		public Builder rules(EnumBattleType type, String... rules) {
			Arrays.stream(rules).forEach(rule -> {
				this.clause(type, rule);
			});
			return this;
		}

		public Builder levelCap(EnumBattleType type, int cap) {
			Preconditions.checkArgument(cap > 0 && cap <= PixelmonConfig.maxLevel);
			switch (type) {
				case First:
					initial.lvlCap(cap);
					break;
				case Rematch:
					rematch.lvlCap(cap);
					break;
			}
			return this;
		}

		public Builder rewards(EnumBattleType type, EnumLeaderType leaderType, BU3Reward... rewards) {
			switch (type) {
				case First:
					for(BU3Reward reward : rewards) {
						initial.addRewards(leaderType, reward);
					}
					break;
				case Rematch:
					for(BU3Reward reward : rewards) {
						rematch.addRewards(leaderType, reward);
					}
					break;
			}
			return this;
		}

		public Builder leader(UUID uuid, EnumLeaderType type) {
			leaders.add(uuid);
			return this;
		}

		public Builder requirements(EnumBattleType type, Requirement... requirements) {
			switch (type) {
				case First:
					for(Requirement requirement : requirements) {
						initial.addRequirements(requirement);
					}
					break;
				case Rematch:
					for(Requirement requirement : requirements) {
						rematch.addRequirements(requirement);
					}
					break;
			}
			return this;
		}

		public Builder min(EnumBattleType type, int min) {
			switch(type) {
				case First:
					initial.min(min);
					break;
				case Rematch:
					rematch.min(min);
					break;
			}
			return this;
		}

		public Builder max(EnumBattleType type, int max) {
			switch(type) {
				case First:
					initial.max(max);
					break;
				case Rematch:
					rematch.max(max);
					break;
			}
			return this;
		}

		public Builder minAndMax(EnumBattleType type, int min, int max) {
			switch(type) {
				case First:
					initial.min(min);
					initial.max(max);
					break;
				case Rematch:
					rematch.min(min);
					rematch.max(max);
					break;
			}
			return this;
		}

		public Gym build() {
			return new Gym(this);
		}
	}
}
