package gg.psyduck.bidoofunleashed.listeners;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.pixelmonmod.pixelmon.api.events.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.TrainerParticipant;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.battle.BattleResults;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleEndCause;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.api.battlables.BU3BattleBase;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.api.events.GymBattleEndEvent;
import gg.psyduck.bidoofunleashed.api.events.GymBattleStartEvent;
import gg.psyduck.bidoofunleashed.api.exceptions.BattleStartException;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.api.pixelmon.participants.TempTrainerTeamParticipant;
import gg.psyduck.bidoofunleashed.api.pixelmon.specs.BU3PokemonSpec;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.e4.Champion;
import gg.psyduck.bidoofunleashed.e4.E4Stage;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import gg.psyduck.bidoofunleashed.gyms.Badge;
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
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BattleListener {

	//private static List<UUID> queued = Lists.newArrayList();

	private static final BiFunction<List<BU3Battlable>, List<BU3Battlable>, List<BU3Battlable>> combine = (l1, l2) -> {
		List<BU3Battlable> result = Lists.newArrayList();
		result.addAll(l1);
		result.addAll(l2);

		return result;
	};

	private static final Function<List<EliteFour>, List<BU3Battlable>> fetch = (e4s) -> {
		List<BU3Battlable> results = Lists.newArrayList();
		e4s.stream().map(EliteFour::getStages).forEach(results::addAll);
		return results;
	};

	private static final Function<List<Gym>, List<BU3Battlable>> convert = (gyms) -> {
		List<BU3Battlable> results = Lists.newArrayList();
		results.addAll(gyms);
		return results;
	};

	@Listener
	public void onInteractGymNPC(InteractEntityEvent.Secondary.MainHand event, @First Player player) {
		net.minecraft.entity.Entity entity = (net.minecraft.entity.Entity) event.getTargetEntity();
		if(entity instanceof NPCTrainer && entity.getEntityData().hasKey("BU3-ID")) {
			if(player.getItemInHand(HandTypes.MAIN_HAND).isPresent() && player.getItemInHand(HandTypes.MAIN_HAND).get().getType().equals(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trainer_editor").get())) {
				return;
			}

			NPCTrainer trainer = (NPCTrainer) entity;
			Multimap<Category, BU3BattleBase> bases = BidoofUnleashed.getInstance().getDataRegistry().getBattlables();
			Optional<BU3Battlable> battlable = combine.apply(
					convert.apply(bases.entries()
							.stream()
							.filter(entry -> entry.getValue() instanceof Gym)
							.map(entry -> (Gym) entry.getValue())
							.collect(Collectors.toList())),
					fetch.apply(bases.entries()
							.stream()
							.filter(entry -> entry.getValue() instanceof EliteFour)
							.map(entry -> (EliteFour) entry.getValue())
							.collect(Collectors.toList())
					))
					.stream()
					.filter(b -> b.getUuid().equals(UUID.fromString(trainer.getEntityData().getString("BU3-ID"))))
					.findAny();

			battlable.ifPresent(g -> {
				if (!g.canChallenge(player)) {
					return;
				}

				for (Requirement requirement : g.getBattleSettings(g.getBattleType(player)).getRequirements()) {
					try {
						if (!requirement.passes(g, player)) {
							requirement.onInvalid(g, player);
							return;
						}
					} catch (Exception e) {
						player.sendMessage(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), "An error occurred whilst processing your information, please inform a staff member..."));
						return;
					}
				}

				PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());
				if (pd.getCurrentEliteFour() == null && g instanceof E4Stage) {
					((E4Stage)g).getBelonging().startChallenge(player);
				}

				try {
					g.startBattle(trainer, player, TeamSelectors.randomized(g, player));
				} catch (BattleStartException e) {
					player.sendMessage(e.getReason());
				}
			});
		}
	}

	@SubscribeEvent
	public void onBattleEnd(BattleEndEvent event) {
		Optional<Map.Entry<Challenge, BU3Battlable>> challenge = BattleRegistry.getBattles().entrySet().stream().filter(c -> {
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
				Sponge.getEventManager().post(new GymBattleEndEvent(c.getKey().getLeader(), c.getKey().getChallenger(), c.getValue(), true));
				this.onVictory(c.getKey(), c.getValue());
			} else if(event.cause == EnumBattleEndCause.FORFEIT) {
				Sponge.getEventManager().post(new GymBattleEndEvent(c.getKey().getLeader(), c.getKey().getChallenger(), c.getValue(), false));
				Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
				tokens.put("bu3_gym", src -> Optional.of(Text.of(c.getValue().getName())));
				c.getKey().getChallenger().sendMessage(MessageUtils.fetchAndParseMsg(c.getKey().getChallenger(), MsgConfigKeys.BATTLES_FORFEIT, tokens, null));
				if(c.getValue() instanceof E4Stage) {
					data.resetDefeatedE4();
				}
			} else {
				Sponge.getEventManager().post(new GymBattleEndEvent(c.getKey().getLeader(), c.getKey().getChallenger(), c.getValue(), false));
				Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
				tokens.put("bu3_gym", src -> Optional.of(Text.of(c.getValue().getName())));
				c.getKey().getChallenger().sendMessage(MessageUtils.fetchAndParseMsg(c.getKey().getChallenger(), MsgConfigKeys.BATTLES_LOSE_GYM, tokens, null));
				if(c.getValue() instanceof E4Stage) {
					data.resetDefeatedE4();
				}
			}

			BattleRegistry.deregister(c.getKey());
		});
	}

	private void onVictory(Challenge challenge, BU3Battlable battlable) {
		PlayerData data = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(challenge.getChallenger().getUniqueId());
		battlable.onDefeat(challenge, data);
		if(challenge.getBattleType() == EnumBattleType.First) {
			// First battle (before earning badge)
			PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) challenge.getChallenger()).ifPresent(storage -> {
				if(battlable instanceof Gym) {
					Gym gym = (Gym) battlable;
					if (challenge.getLeader() instanceof Player) {
						data.awardBadge(gym.getBadge().fill(challenge.getLeader().getUniqueId()));
					} else {
						data.awardBadge(gym.getBadge().fill("NPC"));
					}
				} else if(battlable instanceof Champion){
					Champion champion = (Champion) battlable;
					if (challenge.getLeader() instanceof Player) {
						data.awardBadge(champion.getBadge().fill(challenge.getLeader().getUniqueId()));
					} else {
						data.awardBadge(champion.getBadge().fill("NPC"));
					}
				}
			});
		}

		data.getBadge(battlable).ifPresent(Badge::incWins);

		battlable.getBattleSettings(challenge.getBattleType()).getRewards().get(challenge.getChallenger() instanceof Player ? EnumLeaderType.PLAYER : EnumLeaderType.NPC).forEach(reward -> {
			try {
				reward.give(challenge.getChallenger());
			} catch (Exception e) {
				BidoofUnleashed.getInstance().getLogger().error("Unable to process a reward for " + challenge.getChallenger().getName() + " due to an error...");
			}
		});

	}
}
