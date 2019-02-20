package gg.psyduck.bidoofunleashed.listeners;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.entities.npcs.NPCChatting;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.enums.battle.BattleResults;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleEndCause;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.api.battlables.BU3BattleBase;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.api.events.GymBattleEndEvent;
import gg.psyduck.bidoofunleashed.api.exceptions.BattleStartException;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.e4.Champion;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import gg.psyduck.bidoofunleashed.e4.Stage;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.gyms.temporary.BattleRegistry;
import gg.psyduck.bidoofunleashed.gyms.temporary.Challenge;
import gg.psyduck.bidoofunleashed.players.BadgeReference;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import gg.psyduck.bidoofunleashed.utils.TeamSelectors;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;

import java.time.Instant;
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
		e4s.forEach(e4 -> {
			results.addAll(e4.getStages());
			results.add(e4.getChampion());
		});
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
		if(entity instanceof NPCChatting && entity.getEntityData().hasKey("BU3-ID")) {
			if(player.getItemInHand(HandTypes.MAIN_HAND).isPresent() && player.getItemInHand(HandTypes.MAIN_HAND).get().getType().equals(Sponge.getRegistry().getType(ItemType.class, "pixelmon:trainer_editor").get())) {
				return;
			}

			event.setCancelled(true);

			NPCChatting trainer = (NPCChatting) entity;
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

				if(g.getBattleSettings(g.getBattleType(player)).getPool().getTeam().isEmpty()) {
					player.sendMessage(Text.of(BidoofUnleashed.getInstance().getPluginInfo().error(), "Unfortunately, no configured team exists for this stage, please inform an admin!"));
					return;
				}

				PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());
				if (pd.getCurrentEliteFour() == null && g instanceof Stage) {
					((Stage)g).getBelonging().startChallenge(player);
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
				if(c.getKey().getLeader() instanceof Player) {
					Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
					tokens.put("bu3_challenger", src -> Optional.of(Text.of(c.getKey().getChallenger().getName())));

					((Player) c.getKey().getLeader()).sendMessage(MessageUtils.fetchAndParseMsg(c.getKey().getChallenger(), MsgConfigKeys.LEADER_LOSE_BATTLE, tokens, null));
				}
				this.onVictory(c.getKey(), c.getValue());
			} else if(event.cause == EnumBattleEndCause.FORFEIT) {
				Sponge.getEventManager().post(new GymBattleEndEvent(c.getKey().getLeader(), c.getKey().getChallenger(), c.getValue(), false));
				Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
				tokens.put("bu3_gym", src -> Optional.of(Text.of(c.getValue().getName())));
				c.getKey().getChallenger().sendMessage(MessageUtils.fetchAndParseMsg(c.getKey().getChallenger(), MsgConfigKeys.BATTLES_FORFEIT, tokens, null));
				if(c.getValue() instanceof Stage) {
					data.resetDefeatedE4();
					data.updateCooldown(((Stage)c.getValue()).getBelonging());
				}

				if(c.getKey().getLeader() instanceof Player) {
					tokens.put("bu3_challenger", src -> Optional.of(Text.of(c.getKey().getChallenger().getName())));

					((Player) c.getKey().getLeader()).sendMessage(MessageUtils.fetchAndParseMsg(c.getKey().getChallenger(), MsgConfigKeys.BATTLES_FORFEIT, tokens, null));
				}
			} else {
				Sponge.getEventManager().post(new GymBattleEndEvent(c.getKey().getLeader(), c.getKey().getChallenger(), c.getValue(), false));
				Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
				tokens.put("bu3_gym", src -> Optional.of(Text.of(c.getValue().getName())));
				c.getKey().getChallenger().sendMessage(MessageUtils.fetchAndParseMsg(c.getKey().getChallenger(), MsgConfigKeys.BATTLES_LOSE_GYM, tokens, null));
				if(c.getValue() instanceof Stage) {
					data.resetDefeatedE4();
					data.updateCooldown(((Stage)c.getValue()).getBelonging());
				}

				if(c.getKey().getLeader() instanceof Player) {
					tokens.put("bu3_challenger", src -> Optional.of(Text.of(c.getKey().getChallenger().getName())));

					((Player) c.getKey().getLeader()).sendMessage(MessageUtils.fetchAndParseMsg(c.getKey().getChallenger(), MsgConfigKeys.LEADER_WIN_BATTLE, tokens, null));
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

			BadgeReference reference;
			if(challenge.getLeader() instanceof Player) {
				reference = new BadgeReference(battlable.getBadge().getName(), Date.from(Instant.now()), challenge.getLeader().getUniqueId());
			} else {
				reference = new BadgeReference(battlable.getBadge().getName(), Date.from(Instant.now()));
			}

			data.awardBadge(reference);
			reference.incrementTimesDefeated();
		}

		challenge.getChallenger().stopSounds();

		battlable.getBattleSettings(challenge.getBattleType()).getRewards().get(challenge.getLeader() instanceof Player ? EnumLeaderType.PLAYER : EnumLeaderType.NPC).forEach(reward -> {
			try {
				reward.give(challenge.getChallenger());
			} catch (Exception e) {
				BidoofUnleashed.getInstance().getLogger().error("Unable to process a reward for " + challenge.getChallenger().getName() + " due to an error...");
			}
		});

	}
}
