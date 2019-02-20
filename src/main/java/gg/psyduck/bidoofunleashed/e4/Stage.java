package gg.psyduck.bidoofunleashed.e4;

import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.api.exceptions.BattleStartException;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.entity.living.player.Player;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public interface Stage extends BU3Battlable {

	List<UUID> getLeaders();

	int getNPCs();

	boolean isOpen();

	boolean open();

	boolean close();

	int getStage();

	String getPath();

	EliteFour getBelonging();

	default boolean checkRequirements(PlayerData pd) {
		List<Gym> inCategory = BidoofUnleashed.getInstance().getDataRegistry().sortedGyms().get(this.getBelonging().getCategory());
		for(Gym gym : inCategory) {
			if(pd.getBadges().stream().noneMatch(badge -> gym.getBadge().getName().equals(badge.getIdentifier()))) {
				return false;
			}
		}
		return true;
	}
}
