package gg.psyduck.bidoofunleashed.listeners;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.configuration.ConfigBase;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3BattleBase;
import gg.psyduck.bidoofunleashed.config.ConfigKeys;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.e4.E4Stage;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import gg.psyduck.bidoofunleashed.e4.Stage;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.storage.BU3Storage;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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

		ConfigBase config = BidoofUnleashed.getInstance().getConfig();
		if(config.get(ConfigKeys.LOGIN_MESSAGES) || config.get(ConfigKeys.OPEN_GYM_ON_LOGIN)) {
			List<BU3BattleBase> leaderOf = search(player.getUniqueId());
			if(leaderOf.stream().noneMatch(base -> base instanceof Stage)) {
				if (leaderOf.size() >= 2) {
					for(Text line : MessageUtils.fetchAndParseMsgs(player, MsgConfigKeys.MISC_LEADER_JOIN_MULTIPLE, null, null)) {
						Sponge.getServer().getBroadcastChannel().send(line);
					}
				} else {
					if (!leaderOf.isEmpty()) {
						Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
						tokens.put("bu3_gym", src -> Optional.of(Text.of(leaderOf.get(0).getName())));
						for(Text line : MessageUtils.fetchAndParseMsgs(player, MsgConfigKeys.MISC_LEADER_JOIN_SINGLE, tokens, null)) {
							Sponge.getServer().getBroadcastChannel().send(line);
						}
					}
				}
			} else {
				for(Text line : MessageUtils.fetchAndParseMsgs(player, MsgConfigKeys.MISC_LEADER_JOIN_E4, null, null)) {
					Sponge.getServer().getBroadcastChannel().send(line);
				}
			}

			if(config.get(ConfigKeys.OPEN_GYM_ON_LOGIN)) {
				for(BU3BattleBase base : leaderOf) {
					if(base instanceof Gym) {
						((Gym) base).open();
					} else {
						((Stage) base).open();
					}
				}
			}
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

		List<BU3BattleBase> leaderOf = search(player.getUniqueId());
		outside:
		for(BU3BattleBase base : leaderOf) {
			if(base instanceof Gym) {
				if(((Gym) base).getNpcs() == 0) {
					List<UUID> others = ((Gym) base).getLeaders().stream().filter(leader -> !leader.equals(player.getUniqueId())).collect(Collectors.toList());
					for(UUID other : others) {
						if(Sponge.getServer().getPlayer(other).isPresent()) {
							continue outside;
						}
					}

					((Gym) base).close();
				}
			} else {
				if(((Stage) base).getNPCs() == 0) {
					List<UUID> others = ((Stage) base).getLeaders().stream().filter(leader -> !leader.equals(player.getUniqueId())).collect(Collectors.toList());
					for(UUID other : others) {
						if(Sponge.getServer().getPlayer(other).isPresent()) {
							continue outside;
						}
					}

					((Stage) base).close();
				}
			}
		}
	}
}
