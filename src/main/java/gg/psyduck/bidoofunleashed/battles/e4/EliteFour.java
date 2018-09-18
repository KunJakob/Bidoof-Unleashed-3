package gg.psyduck.bidoofunleashed.battles.e4;

import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.battles.Category;
import gg.psyduck.bidoofunleashed.battles.battletypes.BattleType;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import lombok.Getter;
import org.spongepowered.api.entity.living.player.Player;

@Getter
public class EliteFour implements BU3Battlable {

	private Category category;
	private int weight;

	@Override
	public boolean canChallenge(Player player) {
		return false;
	}

	@Override
	public void startBattle(Player leader, Player challenger) {

	}

	@Override
	public EnumBattleType getBattleType(Player player) {
		PlayerData data = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());

		return null;
	}

	@Override
	public BattleType getBattleSettings(EnumBattleType type) {
		return null;
	}
}
