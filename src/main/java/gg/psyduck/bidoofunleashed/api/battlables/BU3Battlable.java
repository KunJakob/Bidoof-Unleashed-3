package gg.psyduck.bidoofunleashed.api.battlables;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.*;
import java.util.function.Function;

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
			PlayerStorage storage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) player).orElseThrow(() -> new Exception("Missing player storage data for " + player.getName()));
			return Arrays.stream(storage.partyPokemon).noneMatch(nbt -> nbt != null && nbt.getInteger(NbtKeys.LEVEL) > bt.getLvlCap());
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
	void startBattle(NPCTrainer leader, Player challenger, List<BU3PokemonSpec> team) throws BattleStartException;

	/**
	 * Actions to handle when a battlable instance is defeated
	 */
	void onDefeat(Challenge challenge, PlayerData data);

	EnumBattleType getBattleType(Player player);

	BattleType getBattleSettings(EnumBattleType type);

	Badge getBadge();

	boolean addLeader(UUID uuid);

	boolean removeLeader(UUID uuid);

	void addNPCLeader(NPCTrainer trainer);

	void removeNPCLeader();

	boolean isOpen();

	boolean queue(Player player);

	Queue<UUID> getQueue();

	List<UUID> getLeaders();

	default void apply(BattleParticipant participant, Player player, List<BU3PokemonSpec> team) {
		for(int i = 0; i < team.size(); i++) {
			EntityPixelmon pokemon = team.get(i).create(participant.getWorld());
			if(team.get(i).level == null) {
				pokemon.getLvl().setLevel(this.getBattleSettings(this.getBattleType(player)).getLvlCap());
			}
			pokemon.loadMoveset();
			pokemon.caughtBall = EnumPokeballs.PokeBall;
			pokemon.setOwnerId(player.getUniqueId());
			pokemon.setPokemonId(participant.getStorage().getNewPokemonID());
			participant.allPokemon[i] = new PixelmonWrapper(participant, pokemon, i);
		}

		participant.controlledPokemon = Lists.newArrayList(participant.allPokemon[0]);
	}
}
