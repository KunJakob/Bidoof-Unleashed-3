package gg.psyduck.bidoofunleashed.players;

import com.google.common.collect.Lists;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PlayerData {

	private final UUID uuid;

	private List<Badge> badges = Lists.newArrayList();

	private Roles role = Roles.NONE;

	public void awardBadge(Badge badge) {
		this.badges.add(badge);
	}

	public void setRole(Roles role) {
		this.role = role;
	}
}
