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

	@Listener
	public void onClientDisconnect(ClientConnectionEvent.Disconnect event, @First Player player) {
		// Unload user data
		BU3Storage storage = BidoofUnleashed.getInstance().getStorage();
		if (BidoofUnleashed.getInstance().getDataRegistry().getPlayerData().containsKey(player.getUniqueId())) {
			storage.updatePlayerData(BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId()));
			BidoofUnleashed.getInstance().getDataRegistry().getPlayerData().remove(player.getUniqueId());
		}

		BidoofUnleashed.getInstance().getDataRegistry().getGyms().stream().filter(gym -> gym.getQueue().contains(player.getUniqueId())).forEach(gym -> gym.getQueue().remove(player.getUniqueId()));
	}
}
