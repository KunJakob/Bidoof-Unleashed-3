package gg.psyduck.bidoofunleashed.api.battlables;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.entities.npcs.NPCChatting;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.exceptions.BattleStartException;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.api.pixelmon.specs.BU3PokemonSpec;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.api.battlables.battletypes.BattleType;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import gg.psyduck.bidoofunleashed.gyms.temporary.Challenge;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface BU3Battlable extends BU3BattleBase {

	/** A static requirement all gyms will have. Since we don't want gyms saving this requirement, it's better to reference here */
	Requirement lvlCapRequirement = new Requirement() {
		@Override
		public String id() {
			return "lvl-cap"; // This really won't matter, but we have it here because it's required
		}

		@Override
		public boolean passes(BU3Battlable battlable, Player player) throws Exception {
			BattleType bt = battlable.getBattleSettings(battlable.getBattleType(player));
			PlayerPartyStorage storage = Pixelmon.storageManager.getParty(player.getUniqueId());
			return storage.getTeam().stream().noneMatch(pokemon -> pokemon.getLevel() > bt.getLvlCap());
		}

		@Override
		public void onInvalid(BU3Battlable battlable, Player player) {
			Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
			tokens.put("bu3_level_cap", s -> Optional.of(Text.of(battlable.getBattleSettings(battlable.getBattleType(player)).getLvlCap())));
			player.sendMessage(MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.REQUIREMENT_LEVELCAP, tokens, null));
		}

		@Override
		public Requirement supply(String... args) throws Exception {
			return this; // This also doesn't matter, but requirements are requirements
		}
	};

	/**
	 * Starts a battle between two players.
	 *
	 * @param leader The leader of the gym accepting the challenge
	 * @param challenger The challenger trying to prove their worth
	 * @param team The team the leader will be battling with
	 */
	void startBattle(Player leader, Player challenger, List<BU3PokemonSpec> team) throws BattleStartException;

	/**
	 * Starts a battle between a player and NPC.
	 *
	 * @param leader The npc representing a leader of a gym/elite four position
	 * @param challenger The challenger trying to prove their worth
	 * @param team The team the leader will be battling with
	 */
	void startBattle(NPCChatting leader, Player challenger, List<BU3PokemonSpec> team) throws BattleStartException;

	/**
	 * Actions to handle when a battlable instance is defeated
	 */
	void onDefeat(Challenge challenge, PlayerData data);

	EnumBattleType getBattleType(Player player);

	BattleType getBattleSettings(EnumBattleType type);

	Badge getBadge();

	boolean addLeader(UUID uuid);

	boolean removeLeader(UUID uuid);

	void addNPCLeader(NPCChatting trainer);

	void removeNPCLeader();

	boolean isOpen();

	boolean queue(Player player);

	Queue<UUID> getQueue();

	List<UUID> getLeaders();

	default List<Pokemon> convertFromSpecs(Entity focus, Player challenger, List<BU3PokemonSpec> specs) {
		return specs.stream().map(spec -> {
			Pokemon pokemon = spec.create();
			if(spec.level == null) {
				pokemon.setLevel(this.getBattleSettings(this.getBattleType(challenger)).getLvlCap());
			} else {
				pokemon.setLevel(Math.min(pokemon.getLevel(), this.getBattleSettings(this.getBattleType(challenger)).getLvlCap()));
			}

			pokemon.setCaughtBall(EnumPokeballs.PokeBall);
			if(focus instanceof Player) {
				pokemon.setOriginalTrainer((EntityPlayerMP) focus);
			}
			return pokemon;
		}).collect(Collectors.toList());
	}
}
