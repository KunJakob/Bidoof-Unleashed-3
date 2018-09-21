package gg.psyduck.bidoofunleashed.e4;

import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.pixelmon.specs.BU3PokemonSpec;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
import gg.psyduck.bidoofunleashed.api.battlables.battletypes.BattleType;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import lombok.Getter;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

@Getter
public class EliteFour implements BU3Battlable {

	private Category category;
	private int weight;

	@Override
	public boolean checkCooldown(Player player) {
		return false;
	}

	@Override
	public boolean canChallenge(Player player) {
		return false;
	}

	@Override
	public boolean canAccess(Player player) {
		return false;
	}

	@Override
	public boolean startBattle(Player leader, Player challenger, List<BU3PokemonSpec> team) {
		return false;
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
