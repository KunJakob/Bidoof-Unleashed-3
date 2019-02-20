package gg.psyduck.bidoofunleashed.players;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.Date;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class BadgeReference {

	private String identifier;
	private UUID leader;
	private Boolean npc;
	private Date obtained;
	private int timesDefeated;

	public BadgeReference(String identifier, Date obtained, UUID leader) {
		this.identifier = identifier;
		this.leader = leader;
		this.obtained = obtained;
	}

	public BadgeReference(String identifier, Date obtained) {
		this.identifier = identifier;
		this.obtained = obtained;
		this.npc = true;
	}

	public String getLeaderName() {
		if(this.leader != null) {
			return Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(this.leader).map(User::getName).orElse("Unknown");
		}

		if(this.npc != null) {
			return "NPC";
		}

		return "UNKNOWN";
	}

	public void incrementTimesDefeated() {
		++this.timesDefeated;
	}
}
