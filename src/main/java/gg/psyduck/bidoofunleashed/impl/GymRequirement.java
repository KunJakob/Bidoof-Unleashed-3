package gg.psyduck.bidoofunleashed.impl;

import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.entity.living.player.Player;

@RequiredArgsConstructor
public class GymRequirement implements Requirement {

	private final Gym requirement;

	@Override
	public boolean apply(Player player, Object... additional) {
		return BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId()).getBadges().contains(requirement.getBadge());
	}
}
