package gg.psyduck.bidoofunleashed.api.battlables.battletypes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.api.rewards.BU3Reward;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.gyms.GymPool;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
public class BattleType {

	private int lvlCap = PixelmonConfig.maxLevel;
	@Getter(AccessLevel.PRIVATE)
	@SerializedName("requirements")
	private List<Requirement> baseRequirements = Lists.newArrayList();
	private Map<EnumLeaderType, List<BU3Reward>> rewards = Maps.newHashMap();
	private List<String> rules = Lists.newArrayList();
	private List<String> clauses = Lists.newArrayList();

	private GymPool pool;
	private int minPokemon = 1;
	private int maxPokemon = 6;

	private transient BattleRules battleRules;

	public BattleType() {
		if(this.rewards.isEmpty()) {
			this.rewards.put(EnumLeaderType.PLAYER, Lists.newArrayList());
			this.rewards.put(EnumLeaderType.NPC, Lists.newArrayList());
		}
	}

	public void init() {
		String impTxt = repImportText(this.rules);
		impTxt += repImportText(this.clauses);

		this.battleRules = new BattleRules(impTxt);
		this.pool.init();
	}

	public BattleType path(File path) {
		this.pool = new GymPool(path);
		return this;
	}

	public void addClause(String clause) {
		this.clauses.add(clause);
	}

	public void addRule(String rule) {
		this.rules.add(rule);
	}

	public void addRewards(EnumLeaderType type, BU3Reward... rewards) {
		this.rewards.put(type, Arrays.asList(rewards));
	}

	public void addRequirements(Requirement... requirements) {
		this.baseRequirements.addAll(Arrays.asList(requirements));
	}

	public void lvlCap(int level) {
		this.lvlCap = level;
	}

	public void min(int minPokemon) {
		this.minPokemon = minPokemon;
	}

	public void max(int maxPokemon) {
		this.maxPokemon = maxPokemon;
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
}
