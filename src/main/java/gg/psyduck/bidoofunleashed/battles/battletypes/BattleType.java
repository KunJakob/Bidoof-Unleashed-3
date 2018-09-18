package gg.psyduck.bidoofunleashed.battles.battletypes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.api.rewards.BU3Reward;
import gg.psyduck.bidoofunleashed.api.BU3Battlable;
import gg.psyduck.bidoofunleashed.battles.gyms.GymPool;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
public class BattleType {

	private int lvlCap;
	@Getter(AccessLevel.PRIVATE)
	@SerializedName("requirements")
	private List<Requirement> baseRequirements;
	private Map<EnumLeaderType, List<BU3Reward>> rewards;
	private List<String> rules;
	private List<String> clauses;

	private GymPool pool;
	private int minPokemon;
	private int maxPokemon;

	private transient BattleRules battleRules;

	public BattleType(Builder builder) {
		this.pool = new GymPool(builder.poolPath);
		this.maxPokemon = builder.maxPokemon;
		this.minPokemon = builder.minPokemon;
		this.baseRequirements = builder.requirements;
		this.rewards = builder.rewards;
		if(this.rewards.isEmpty()) {
			this.rewards.put(EnumLeaderType.PLAYER, Lists.newArrayList());
			this.rewards.put(EnumLeaderType.NPC, Lists.newArrayList());
		}
		this.rules = builder.rules;
		this.clauses = builder.clauses;
		this.lvlCap = builder.lvlCap;
	}

	public void init() {
		String impTxt = repImportText(this.rules);
		impTxt += repImportText(this.clauses);

		this.battleRules = new BattleRules(impTxt);
		this.pool.init();
	}

	public void addClause(String clause) {
		this.clauses.add(clause);
	}

	public void addClauses(String... clauses) {
		Arrays.stream(clauses).forEach(this::addClause);
	}

	public void addRule(String rule) {
		this.rules.add(rule);
	}

	public void addRules(String... rules) {
		Arrays.stream(rules).forEach(this::addRule);
	}

	public void addRewards(EnumLeaderType type, BU3Reward... rewards) {
		this.rewards.put(type, Arrays.asList(rewards));
	}

	public void addRequirements(Requirement... requirements) {
		this.baseRequirements.addAll(Arrays.asList(requirements));
	}

	private String repImportText(List<String> content) {
		StringBuilder builder = new StringBuilder();
		for(String str : content) {
			builder.append(str).append("\n");
		}

		return builder.toString();
	}

	public List<Requirement> getRequirements() {
		List<Requirement> requirements = Lists.newArrayList(this.getBaseRequirements());
		requirements.add(BU3Battlable.lvlCapRequirement);
		return requirements;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private File poolPath;
		private int minPokemon = 1;
		private int maxPokemon = 6;

		private int lvlCap = PixelmonConfig.maxLevel;
		private List<Requirement> requirements = Lists.newArrayList();
		private Map<EnumLeaderType, List<BU3Reward>> rewards = Maps.newHashMap();
		private List<String> rules = Lists.newArrayList();
		private List<String> clauses = Lists.newArrayList();

		public Builder path(File path) {
			this.poolPath = path;
			return this;
		}

		public Builder min(int min) {
			this.minPokemon = min;
			return this;
		}

		public Builder max(int max) {
			this.maxPokemon = max;
			return this;
		}

		public Builder requirement(Requirement requirement) {
			this.requirements.add(requirement);
			return this;
		}

		public Builder reward(EnumLeaderType type, BU3Reward reward) {
			if(this.rewards.containsKey(type)) {
				this.rewards.get(type).add(reward);
			} else {
				this.rewards.put(type, Lists.newArrayList(reward));
			}
			return this;
		}

		public Builder lvlCap(int lvlCap) {
			this.lvlCap = lvlCap;
			return this;
		}

		public Builder rule(String rule) {
			this.rules.add(rule);
			return this;
		}

		public Builder clause(String clause) {
			this.clauses.add(clause);
			return this;
		}

		public BattleType build() {
			Preconditions.checkNotNull(poolPath, "Pool path must be set!");
			return new BattleType(this);
		}
	}
}
