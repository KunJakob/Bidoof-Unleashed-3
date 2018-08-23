package gg.psyduck.bidoofunleashed.listeners;

import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.storage.BU3Storage;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class ClientListener {

	@Listener
	public void onClientJoin(ClientConnectionEvent.Join event, @First Player player) {
		BU3Storage storage = BidoofUnleashed.getInstance().getStorage();
		if (!BidoofUnleashed.getInstance().getDataRegistry().getPlayerData().containsKey(player.getUniqueId())) {
			storage.getPlayerData(player.getUniqueId()).thenAccept(optData -> {
				PlayerData data = optData.orElseGet(() -> PlayerData.createFor(player.getUniqueId()));
				BidoofUnleashed.getInstance().getDataRegistry().register(player.getUniqueId(), data);
			});
		}
	}
}
