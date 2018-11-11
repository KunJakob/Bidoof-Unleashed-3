package gg.psyduck.bidoofunleashed.players;

import com.google.gson.annotations.SerializedName;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.api.battlables.BU3BattleBase;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
import gg.psyduck.bidoofunleashed.api.cooldowns.Cooldown;
import gg.psyduck.bidoofunleashed.api.storage.Storable;
import gg.psyduck.bidoofunleashed.config.ConfigKeys;
import gg.psyduck.bidoofunleashed.e4.E4Stage;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import gg.psyduck.bidoofunleashed.e4.Stage;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Getter
public class PlayerData implements Storable {

	private final UUID uuid;

	private List<Badge> badges = new ArrayList<>();

	// Data persisting to Elite Four challenges here
	@SerializedName("defeated-elite-four")
	private Boolean[] defeatedElite4 = new Boolean[4];
	private String eliteFour = "";
	private transient EliteFour currentEliteFour;

	private Map<String, Date> cooldowns = new HashMap<>();

	@Setter
	private transient boolean queued = false;
	private transient boolean dirty = false;

	private PlayerData(UUID uuid) {
		this.uuid = uuid;
		BidoofUnleashed.getInstance().getStorage().addPlayerData(this);
		this.resetDefeatedE4();
	}

	public static PlayerData createFor(UUID uuid) {
		return new PlayerData(uuid);
	}

	public PlayerData init() {
		if (this.eliteFour == null) {
			this.eliteFour = "";
			this.dirty = true;
		}

		this.currentEliteFour = BidoofUnleashed.getInstance().getDataRegistry()
				.getBattlables().values().stream()
				.filter(x -> x instanceof EliteFour)
				.filter(x -> x.getName().equalsIgnoreCase(this.eliteFour))
				.map(x -> (EliteFour) x)
				.findAny().orElse(null);

		return this;
	}

	public void awardBadge(Badge badge) {
		this.badges.add(badge);
		this.dirty = true;
	}

	public boolean hasBadge(Badge badge) {
		if (badge == null) {
			return false;
		}
		return this.badges.stream().anyMatch(b -> b.getName().equals(badge.getName()) && b.getItemType().equals(badge.getItemType()));
	}

	public Optional<Badge> getBadge(Gym gym) {
		return this.badges.stream().filter(b -> b.getName().equals(gym.getBadge().getName()) && b.getItemType().equals(gym.getBadge().getItemType())).findAny();
	}

	public Optional<Badge> getBadge(EliteFour e4) {
		return this.badges.stream().filter(b -> b.getName().equals(e4.getBadge().getName()) && b.getItemType().equals(e4.getBadge().getItemType())).findAny();
	}

	public Optional<Badge> getBadge(BU3Battlable battlable) {
		if (battlable instanceof Gym) {
			return this.getBadge((Gym) battlable);
		} else {
			return this.getBadge(((Stage) battlable).getBelonging());
		}
	}

	public void removeBadge(Badge badge) {
		if (this.hasBadge(badge)) {
			this.badges.remove(badge);
		}
		this.dirty = true;
	}

	/**
	 * Simply places a cooldown on a gym for this player instance. If a data entry exists, which it shouldn't, it will
	 * be overwritten.
	 *
	 * @param battlable The gym to check against
	 */
	public void updateCooldown(BU3BattleBase battlable) {
		if (this.cooldowns == null) {
			this.cooldowns = new HashMap<>();
		}

		Cooldown cooler = (Cooldown) battlable;
		this.cooldowns.put(battlable.getName(), Date.from(Instant.now().plus(cooler.getCooldown() != 0 ? cooler.getCooldown() : BidoofUnleashed.getInstance().getConfig().get(ConfigKeys.DEFAULT_COOLDOWN), ChronoUnit.MINUTES)));
		this.dirty = true;
	}

	/**
	 * Simply removes a cooldown on a gym for this player instance.
	 *
	 * @param battlable The gym to purge the cooldown for
	 */
	public void purgeCooldown(BU3Battlable battlable) {
		if (this.cooldowns == null) {
			this.cooldowns = new HashMap<>();
		}
		this.cooldowns.remove(battlable.getName());
		this.dirty = true;
	}

	/**
	 * Checks the cooldown mapping on this instance of player data for anything cross-referencing the specified gym. If
	 * the mapping returns null, we will assume there is no cooldown on this player for that gym, and return true. Otherwise,
	 * we will run a check against the current time instance versus the time set in the mapping.
	 *
	 * @param battlable The battlable we are checking against
	 * @return True if a cooldown exists, and the current time is before the overlap period. False in all other cases.
	 */
	public boolean beforeCooldownPeriod(BU3BattleBase battlable) {
		if (this.cooldowns == null) {
			this.cooldowns = new HashMap<>();
		}
		Date period = this.cooldowns.get(battlable.getName());
		if (period == null) {
			return false;
		}

		return Date.from(Instant.now()).before(period);
	}

	public void setCurrentEliteFour(EliteFour eliteFour) {
		this.currentEliteFour = eliteFour;
		this.eliteFour = eliteFour.getName();
		this.dirty = true;
	}

	public void defeatStage(int stage) {
		this.defeatedElite4[stage - 1] = true;
		this.dirty = true;
	}

	public void resetDefeatedE4() {
		this.defeatedElite4 = new Boolean[4];
		for (int i = 0; i < defeatedElite4.length; i++) {
			this.defeatedElite4[i] = false;
		}
		this.dirty = true;
	}

	public void clean() {
		this.dirty = false;
	}

	@Override
	public boolean isDirty() {
		return this.dirty;
	}
}
