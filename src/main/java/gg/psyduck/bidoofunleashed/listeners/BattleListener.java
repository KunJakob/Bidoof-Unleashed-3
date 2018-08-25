package gg.psyduck.bidoofunleashed.listeners;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.events.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.BeatTrainerEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.enums.battle.BattleResults;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.gyms.temporary.BattleRegistry;
import gg.psyduck.bidoofunleashed.gyms.temporary.Challenge;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BattleListener {

	@SubscribeEvent
	public void onBattleStart(BattleStartedEvent event) {
		NPCTrainer trainer;
		EntityPlayerMP player;
		if (event.participant1[0].getEntity() instanceof NPCTrainer) {
			trainer = (NPCTrainer)event.participant1[0].getEntity();
			player = (EntityPlayerMP)(event.participant2[0].getEntity());
			this.battleStartHelper(event, trainer, (Player) player);
		} else if (event.participant2[0].getEntity() instanceof NPCTrainer) {
			trainer = (NPCTrainer)event.participant2[0].getEntity();
			player = (EntityPlayerMP)(event.participant1[0].getEntity());
			this.battleStartHelper(event, trainer, (Player) player);
		}
	}

	private void battleStartHelper(BattleStartedEvent event, NPCTrainer trainer, Player player) {
		Optional<Gym> gym = BidoofUnleashed.getInstance().getDataRegistry().getGyms().stream().filter(g -> g.getLeaders().entrySet().stream().anyMatch(entry -> entry.getValue() == EnumLeaderType.NPC && entry.getKey() == trainer.getUniqueID())).findAny();
		gym.ifPresent(g -> {
			for(Requirement requirement : g.getRequirements()) {
				try {
					if(!requirement.passes(g, player)) {
						requirement.onInvalid(g, player);
						event.setCanceled(true);
						break;
					}
				} catch (Exception e) {
					player.sendMessage(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), "An error occurred whilst processing your information, please inform a staff member..."));
					event.setCanceled(true);
					break;
				}
			}

			g.startBattle((Entity) trainer, player);
		});
	}

	@SubscribeEvent
	public void onBattleEndPlayer(BattleEndEvent event) {
		Optional<Map.Entry<Challenge, Gym>> challenge = BattleRegistry.getBattles().entrySet().stream().filter(c -> {
			for(EntityPlayerMP pmp : event.getPlayers()) {
				if(pmp.getName().equalsIgnoreCase(c.getKey().getChallenger().getName())) {
					return true;
				}
			}

			return false;
		}).findAny();

		challenge.ifPresent(c -> {
			ImmutableMap<BattleParticipant, BattleResults> resultMapping = event.results;
			BattleResults results = resultMapping.entrySet()
					.stream()
					.filter(entry -> entry.getKey().getEntity().getName().equalsIgnoreCase(c.getKey().getChallenger().getName()))
					.map(Map.Entry::getValue)
					.findAny()
					.orElse(null);

			if(results == BattleResults.VICTORY) {
				this.onVictory(c.getKey(), c.getValue());
			}

			BattleRegistry.deregister(c.getKey());
		});
	}

	@SubscribeEvent
	public void onBattleEndNPC(BeatTrainerEvent event) {
		Optional<Map.Entry<Challenge, Gym>> challenge = BattleRegistry.getBattles().entrySet().stream().filter(entry -> event.player.getName().equalsIgnoreCase(entry.getKey().getChallenger().getName())).findAny();

		challenge.ifPresent(c -> {
			this.onVictory(c.getKey(), c.getValue());
			BattleRegistry.deregister(c.getKey());
		});
	}

	private void onVictory(Challenge challenge, Gym gym) {
		PlayerData data = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(challenge.getChallenger().getUniqueId());
		if(!data.hasBadge(gym.getBadge())) {
			// First battle (before earning badge)
			PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) challenge.getChallenger()).ifPresent(storage -> {
				List<String> team = Lists.newArrayList();
				for(NBTTagCompound nbt : storage.partyPokemon) {
					String name = nbt.getString(NbtKeys.NAME);
					int level = nbt.getInteger(NbtKeys.LEVEL);

					team.add("&e" + name + " &7(&a" + level + "&7)");
				}

				if(challenge.getLeader() instanceof Player) {
					data.awardBadge(gym.getBadge().fill(challenge.getLeader().getUniqueId(), team));
				} else {
					data.awardBadge(gym.getBadge().fill(((NPCTrainer) challenge.getLeader()).getName(), team));
				}
				gym.getRewards().get(EnumBattleType.First).forEach(reward -> {
					try {
						reward.give(challenge.getChallenger());
					} catch (Exception e) {
						BidoofUnleashed.getInstance().getLogger().error("Unable to process a reward for " + challenge.getChallenger().getName() + " due to an error...");
					}
				});
			});
		} else {
			// Rematch battle
			gym.getRewards().get(EnumBattleType.Rematch).forEach(reward -> {
				try {
					reward.give(challenge.getChallenger());
				} catch (Exception e) {
					BidoofUnleashed.getInstance().getLogger().error("Unable to process a reward for " + challenge.getChallenger().getName() + " due to an error...");
				}
			});
		}
	}
}
