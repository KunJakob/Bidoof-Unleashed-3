package gg.psyduck.bidoofunleashed.listeners;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.api.events.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.BeatTrainerEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.battle.BattleResults;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.api.events.GymBattleEndEvent;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.api.pixelmon.participants.TempTeamParticipant;
import gg.psyduck.bidoofunleashed.api.pixelmon.participants.TempTrainerTeamParticipant;
import gg.psyduck.bidoofunleashed.api.pixelmon.specs.BU3PokemonSpec;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.gyms.temporary.BattleRegistry;
import gg.psyduck.bidoofunleashed.gyms.temporary.Challenge;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import gg.psyduck.bidoofunleashed.utils.TeamSelectors;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.*;
import java.util.function.Function;

public class BattleListener {

	private static List<UUID> queued = new ArrayList<>();

	@SubscribeEvent
	public void onBattleStart(BattleStartedEvent event) {
		NPCTrainer trainer;
		EntityPlayerMP player;
		if (event.participant1[0].getEntity() instanceof NPCTrainer) {
			trainer = (NPCTrainer)event.participant1[0].getEntity();
			player = (EntityPlayerMP)(event.participant2[0].getEntity());
			this.battleStartHelper(event, trainer, (Player) player, event.participant1);
		} else if (event.participant2[0].getEntity() instanceof NPCTrainer) {
			trainer = (NPCTrainer)event.participant2[0].getEntity();
			player = (EntityPlayerMP)(event.participant1[0].getEntity());
			this.battleStartHelper(event, trainer, (Player) player, event.participant2);
		}
	}

	private void battleStartHelper(BattleStartedEvent event, NPCTrainer trainer, Player player, BattleParticipant[] participants) {
		if(queued.contains(trainer.getUniqueID())) {
			return; // We don't want to redo this entire process, so return here to end it all
		}

		Optional<Gym> gym = BidoofUnleashed.getInstance().getDataRegistry().getGyms().stream().filter(g -> g.getLeaders().entrySet().stream().anyMatch(entry -> entry.getKey() == trainer.getUniqueID() && entry.getValue() == EnumLeaderType.NPC)).findAny();
		gym.ifPresent(g -> {
			for(Requirement requirement : g.getBattleSettings(g.getBattleType(player)).getRequirements()) {
				try {
					if(!requirement.passes(g, player)) {
						requirement.onInvalid(g, player);
						event.setCanceled(true);
						return;
					}
				} catch (Exception e) {
					player.sendMessage(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), "An error occurred whilst processing your information, please inform a staff member..."));
					event.setCanceled(true);
					return;
				}
			}

			List<BU3PokemonSpec> team = TeamSelectors.randomized(g, player);
			TempTrainerTeamParticipant tttp = new TempTrainerTeamParticipant(trainer, (EntityPlayerMP) player, team.size());
			tttp.allPokemon = new PixelmonWrapper[team.size()];

			PlayerStorage storage = trainer.getPokemonStorage();
			for(int i = 0; i < team.size(); i++) {
				EntityPixelmon pokemon = team.get(i).create(trainer.world);
				if(team.get(i).level == null) {
					pokemon.getLvl().setLevel(g.getBattleSettings(g.getBattleType(player)).getLvlCap());
				}
				pokemon.loadMoveset();
				pokemon.caughtBall = EnumPokeballs.PokeBall;
				pokemon.setOwnerId(trainer.getUniqueID());
				pokemon.setPokemonId(storage.getNewPokemonID());
				tttp.allPokemon[i] = new PixelmonWrapper(tttp, pokemon, i);
			}

			tttp.controlledPokemon = Collections.singletonList(tttp.allPokemon[0]);
			participants[0] = tttp;

			BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId()).updateCooldown(g);
			EnumBattleType type = g.getBattleType(player);
			BattleRegistry.register(new Challenge((Entity) trainer, player, type), g);
			queued.add(trainer.getUniqueID());
			event.setCanceled(true);
			new BattleControllerBase(tttp, new PlayerParticipant((EntityPlayerMP) player), g.getBattleSettings(g.getBattleType(player)).getBattleRules());
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

			c.getKey().restore();
			PlayerData data = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(c.getKey().getChallenger().getUniqueId());
			data.purgeCooldown(c.getValue());

			if(results == BattleResults.VICTORY) {
				Sponge.getEventManager().post(new GymBattleEndEvent(c.getKey().getChallenger(), c.getKey().getLeader(), c.getValue(), true));
				this.onVictory(c.getKey(), c.getValue());
			} else {
				Sponge.getEventManager().post(new GymBattleEndEvent(c.getKey().getChallenger(), c.getKey().getLeader(), c.getValue(), false));
			}

			BattleRegistry.deregister(c.getKey());
		});
	}

	@SubscribeEvent
	public void onBattleEndNPC(BeatTrainerEvent event) {
		Optional<Map.Entry<Challenge, Gym>> challenge = BattleRegistry.getBattles().entrySet().stream().filter(entry -> event.player.getName().equalsIgnoreCase(entry.getKey().getChallenger().getName())).findAny();

		challenge.ifPresent(c -> {
			Sponge.getEventManager().post(new GymBattleEndEvent(c.getKey().getChallenger(), c.getKey().getLeader(), c.getValue(), true));
			c.getKey().restore();
			this.onVictory(c.getKey(), c.getValue());
			BattleRegistry.deregister(c.getKey());
		});
	}

	private void onVictory(Challenge challenge, Gym gym) {
		PlayerData data = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(challenge.getChallenger().getUniqueId());
		if(challenge.getBattleType() == EnumBattleType.First) {
			// First battle (before earning badge)
			PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) challenge.getChallenger()).ifPresent(storage -> {
				List<String> team = Lists.newArrayList();
				for(NBTTagCompound nbt : storage.partyPokemon) {
					if(nbt != null) {
						String name = nbt.getString(NbtKeys.NAME);
						int level = nbt.getInteger(NbtKeys.LEVEL);

						team.add("&e" + name + " &7(&aLvl " + level + "&7)");
					}
				}

				if(challenge.getLeader() instanceof Player) {
					data.awardBadge(gym.getBadge().fill(challenge.getLeader().getUniqueId(), team));
				} else {
					data.awardBadge(gym.getBadge().fill("NPC", team));
				}
			});
		}

		gym.getBattleSettings(challenge.getBattleType()).getRewards().get(challenge.getChallenger() instanceof Player ? EnumLeaderType.PLAYER : EnumLeaderType.NPC).forEach(reward -> {
			try {
				reward.give(challenge.getChallenger());
			} catch (Exception e) {
				BidoofUnleashed.getInstance().getLogger().error("Unable to process a reward for " + challenge.getChallenger().getName() + " due to an error...");
			}
		});

		Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
		tokens.put("bu3_gym", src -> Optional.of(Text.of(gym.getName())));
		challenge.getChallenger().sendMessage(MessageUtils.fetchAndParseMsg(challenge.getChallenger(), MsgConfigKeys.BATTLES_WIN, tokens, null));
	}
}
