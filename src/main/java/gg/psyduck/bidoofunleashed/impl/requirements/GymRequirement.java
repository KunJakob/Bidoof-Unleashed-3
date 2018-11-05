package gg.psyduck.bidoofunleashed.impl.requirements;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.json.Typing;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import lombok.NoArgsConstructor;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Typing("gym")
@NoArgsConstructor
public class GymRequirement implements Requirement {

	private final String type = "gym";
	private Gym requirement;

	private GymRequirement(Gym requirement) {
		this.requirement = requirement;
	}

	@Override
	public String id() {
		return type;
	}

	@Override
	public boolean passes(BU3Battlable battlable, Player player) {
		return BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId()).getBadges().stream().anyMatch(badge -> badge.getName().equalsIgnoreCase(requirement.getBadge().getName()));
	}

	@Override
	public void onInvalid(BU3Battlable battlable, Player player) {
		Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
		tokens.put("bu3_badge", s -> Optional.of(Text.of(requirement.getBadge().getName())));
		Text parsed = MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.REQUIREMENT_GYM, tokens, null);
		player.sendMessage(parsed);
	}

	@Override
	public GymRequirement supply(String... args) throws Exception {
		if(args.length < 1) {
			throw new Exception("Gym must be specified...");
		}

		String gym = args[0];
		for(Gym g : BidoofUnleashed.getInstance().getDataRegistry().getBattlables().values().stream().filter(base -> base instanceof Gym).map(base -> (Gym) base).collect(Collectors.toList())) {
			if(g.getName().equalsIgnoreCase(gym)) {
				return new GymRequirement(g);
			}
		}

		throw new Exception("Invalid gym specified...");
	}
}
