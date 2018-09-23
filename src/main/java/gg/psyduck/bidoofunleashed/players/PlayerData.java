package gg.psyduck.bidoofunleashed.players;

import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.config.ConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Getter
public class PlayerData {

	private final UUID uuid;

	private List<Badge> badges = new ArrayList<>();

	@Setter private Roles role = Roles.NONE;

	private Map<String, Date> cooldowns = new HashMap<>();

	private PlayerData(UUID uuid) {
		this.uuid = uuid;
		BidoofUnleashed.getInstance().getStorage().addPlayerData(this);
	}

	public static PlayerData createFor(UUID uuid) {
		return new PlayerData(uuid);
	}

	public void awardBadge(Badge badge) {
		this.badges.add(badge);
	}

	public boolean hasBadge(Badge badge) {
		if(badge == null) {
			return false;
		}
	    return this.badges.stream().anyMatch(b -> b.getName().equals(badge.getName()) && b.getItemType().equals(badge.getItemType()));
    }

	public void removeBadge(Badge badge) {
	    if (this.hasBadge(badge)) {
	        this.badges.remove(badge);
        }
    }

	/**
	 * Simply places a cooldown on a gym for this player instance. If a data entry exists, which it shouldn't, it will
	 * be overwritten.
	 *
	 * @param gym The gym to check against
	 */
	public void updateCooldown(Gym gym) {
		if(this.cooldowns == null) {
			this.cooldowns = new HashMap<>();
		}
		this.cooldowns.put(gym.getName(), Date.from(Instant.now().plus(gym.getCooldown() != 0 ? gym.getCooldown() : BidoofUnleashed.getInstance().getConfig().get(ConfigKeys.DEFAULT_COOLDOWN), ChronoUnit.MINUTES)));
    }

	/**
	 * Simply removes a cooldown on a gym for this player instance.
	 *
	 * @param gym The gym to purge the cooldown for
	 */
	public void purgeCooldown(Gym gym) {
		if(this.cooldowns == null) {
			this.cooldowns = new HashMap<>();
		}
		this.cooldowns.remove(gym.getName());
    }

	/**
	 * Checks the cooldown mapping on this instance of player data for anything cross-referencing the specified gym. If
	 * the mapping returns null, we will assume there is no cooldown on this player for that gym, and return true. Otherwise,
	 * we will run a check against the current time instance versus the time set in the mapping.
	 *
	 * @param gym The gym we are checking against
	 * @return True if no cooldown exists, or the returned cooldown date is before the current system time.
	 */
	public boolean afterCooldownPeriod(Gym gym) {
		if(this.cooldowns == null) {
			this.cooldowns = new HashMap<>();
		}
		Date period = this.cooldowns.get(gym.getName());
		if(period == null) {
			return true;
		}

		return period.before(Date.from(Instant.now()));
    }
}
