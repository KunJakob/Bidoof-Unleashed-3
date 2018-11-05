package gg.psyduck.bidoofunleashed.api.battlables;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.battles.rules.clauses.BattleClauseRegistry;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.arenas.Arena;
import gg.psyduck.bidoofunleashed.api.battlables.battletypes.BattleType;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.api.rewards.BU3Reward;
import gg.psyduck.bidoofunleashed.config.ConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Badge;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class BattlableBuilder<T> {
	public String name;
	public Badge badge = new Badge();
	public Arena arena;
	public int weight = -1;
	public String category = "default";
	public int cooldown = BidoofUnleashed.getInstance().getConfig().get(ConfigKeys.DEFAULT_COOLDOWN);

	public BattleType initial = new BattleType();
	public BattleType rematch = new BattleType();

	public List<UUID> leaders = Lists.newArrayList();

	public BattlableBuilder<T> name(String name) {
		Preconditions.checkArgument(BidoofUnleashed.getInstance().getDataRegistry().getBattlables().values().stream().noneMatch(gym -> gym.getName().equalsIgnoreCase(name)), "Can't have two gyms with the same name");
		this.name = name;
		return this;
	}

	public BattlableBuilder<T> category(String category) {
		this.category = category;
		return this;
	}

	public BattlableBuilder<T> weight(int weight) {
		Preconditions.checkArgument(weight > -1, "Weights must be >= 0");
		this.weight = weight;
		return this;
	}

	public BattlableBuilder<T> cooldown(int minutes) {
		this.cooldown = minutes;
		return this;
	}

	public BattlableBuilder<T> badgeName(String name) {
		this.badge = badge.name(name);
		return this;
	}

	public BattlableBuilder<T> badgeType(String itemType) {
		this.badge = badge.itemType(itemType);
		return this;
	}

	public BattlableBuilder<T> arena(Arena arena) {
		this.arena = arena;
		return this;
	}

	public BattlableBuilder<T> clause(EnumBattleType type, String clause) {
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

	public BattlableBuilder<T> clauses(EnumBattleType type, String... clauses) {
		Arrays.stream(clauses).forEach(clause -> {
			Preconditions.checkArgument(BattleClauseRegistry.getClauseRegistry().hasClause(clause), "Invalid clause: " + clause);
			this.clause(type, clause);
		});
		return this;
	}

	public BattlableBuilder<T> rule(EnumBattleType type, String rule) {
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

	public BattlableBuilder<T> rules(EnumBattleType type, String... rules) {
		Arrays.stream(rules).forEach(rule -> {
			this.clause(type, rule);
		});
		return this;
	}

	public BattlableBuilder<T> levelCap(EnumBattleType type, int cap) {
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

	public BattlableBuilder<T> rewards(EnumBattleType type, EnumLeaderType leaderType, BU3Reward... rewards) {
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

	public BattlableBuilder<T> leader(UUID uuid, EnumLeaderType type) {
		leaders.add(uuid);
		return this;
	}

	public BattlableBuilder<T> requirements(EnumBattleType type, Requirement... requirements) {
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

	public BattlableBuilder<T> min(EnumBattleType type, int min) {
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

	public BattlableBuilder<T> max(EnumBattleType type, int max) {
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

	public BattlableBuilder<T> minAndMax(EnumBattleType type, int min, int max) {
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

	public abstract T build();
}
