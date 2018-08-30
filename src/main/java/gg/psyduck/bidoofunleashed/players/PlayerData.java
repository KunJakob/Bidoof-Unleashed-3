package gg.psyduck.bidoofunleashed.players;

import com.google.common.collect.Lists;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.gyms.e4.E4;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerData {

	private final UUID uuid;

	private List<Badge> badges = Lists.newArrayList();

	@Setter private Roles role = Roles.NONE;

	@Setter private transient NBTTagCompound[] team;

	private Map<E4, Boolean> beatenE4;

	private PlayerData(UUID uuid) {
		this.uuid = uuid;
		BidoofUnleashed.getInstance().getStorage().addOrUpdatePlayerData(this);
	}

	public static PlayerData createFor(UUID uuid) {
		return new PlayerData(uuid);
	}

	public void awardBadge(Badge badge) {
		this.badges.add(badge);
	}

	public boolean hasBadge(Badge badge) {
	    return badge != null && this.badges.stream().anyMatch(b -> b.getName().equals(badge.getName()) && b.getItemType().equals(badge.getItemType()));
    }

	public void removeBadge(Badge badge) {
	    if (this.hasBadge(badge)) {
	        this.badges.remove(badge);
        }
    }
}
