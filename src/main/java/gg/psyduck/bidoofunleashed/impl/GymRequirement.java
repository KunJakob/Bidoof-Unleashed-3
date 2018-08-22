package gg.psyduck.bidoofunleashed.impl;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.json.Typing;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Map;

@Typing("gym")
@RequiredArgsConstructor
public class GymRequirement implements Requirement {

	private final Gym requirement;

	@Override
	public boolean passes(Gym gym, Player player) {
		return BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId()).getBadges().contains(requirement.getBadge());
	}

	@Override
	public void onInvalid(Gym gym, Player player) {
		Map<String, Object> variables = Maps.newHashMap();
		variables.put("bu3_badge", requirement.getBadge().getName());
		Text parsed = MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.REQUIREMENT_GYM, null, variables);
		player.sendMessage(parsed);
	}
}
