package gg.psyduck.bidoofunleashed.listeners;

import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3BattleBase;
import gg.psyduck.bidoofunleashed.e4.E4Stage;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.storage.BU3Storage;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.List;

import static gg.psyduck.bidoofunleashed.utils.GeneralUtils.search;

public class ClientListener {

	@Listener
	public void onClientJoin(ClientConnectionEvent.Join event, @First Player player) {
		BU3Storage storage = BidoofUnleashed.getInstance().getStorage();
		if (!BidoofUnleashed.getInstance().getDataRegistry().getPlayerData().containsKey(player.getUniqueId())) {
			storage.getPlayerData(player.getUniqueId()).thenAccept(optData -> {
				PlayerData data = optData.orElseGet(() -> PlayerData.createFor(player.getUniqueId()));
				BidoofUnleashed.getInstance().getDataRegistry().register(player.getUniqueId(), data.init());
			});
		}

		List<BU3BattleBase> leaderOf = search(player.getUniqueId());
		if(leaderOf.size() > 1) {

		} else {
			String name = leaderOf.get(0).getName();
		}
	}

	@Listener
	public void onClientDisconnect(ClientConnectionEvent.Disconnect event, @First Player player) {
		// Unload user data
		BU3Storage storage = BidoofUnleashed.getInstance().getStorage();
		if (BidoofUnleashed.getInstance().getDataRegistry().getPlayerData().containsKey(player.getUniqueId())) {
			storage.updatePlayerData(BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId()));
			BidoofUnleashed.getInstance().getDataRegistry().getPlayerData().remove(player.getUniqueId());
		}

		for(BU3BattleBase base : BidoofUnleashed.getInstance().getDataRegistry().getBattlables().values()) {
			if(base instanceof Gym) {
				((Gym) base).getQueue().remove(player.getUniqueId());
			} else {
				for(E4Stage stage : ((EliteFour) base).getStages()) {
					stage.getQueue().remove(player.getUniqueId());
				}
			}
		}
	}
}
