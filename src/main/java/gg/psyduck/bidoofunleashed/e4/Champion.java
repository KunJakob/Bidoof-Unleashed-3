package gg.psyduck.bidoofunleashed.e4;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.entities.npcs.NPCChatting;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
import gg.psyduck.bidoofunleashed.api.battlables.arenas.Arena;
import gg.psyduck.bidoofunleashed.api.battlables.battletypes.BattleType;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.events.GymBattleStartEvent;
import gg.psyduck.bidoofunleashed.api.exceptions.BattleStartException;
import gg.psyduck.bidoofunleashed.api.pixelmon.participants.ChattingNPCParticipant;
import gg.psyduck.bidoofunleashed.api.pixelmon.participants.TempTeamParticipant;
import gg.psyduck.bidoofunleashed.api.pixelmon.specs.BU3PokemonSpec;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import gg.psyduck.bidoofunleashed.gyms.temporary.BattleRegistry;
import gg.psyduck.bidoofunleashed.gyms.temporary.Challenge;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import java.io.File;
import java.util.*;
import java.util.function.Function;

import static gg.psyduck.bidoofunleashed.storage.dao.file.FileDao.pathBuilder;

public class Champion implements Stage {

	private transient Queue<UUID> queue = new LinkedList<>();
	private transient boolean open;

	@Setter
	private transient EliteFour e4;

	private final UUID uuid;

	private String name;
	private Arena arena;

	private BattleType initial;
	private BattleType rematch;

	protected List<UUID> leaders;
	protected int npcs = 0;

	public Champion(EliteFour e4) {
		this.uuid = UUID.randomUUID();
		this.e4 = e4;

		this.name = "Champion";
		this.arena = new Arena();
		this.initial = new BattleType().path(new File(pathBuilder(this), new File(this.getPath(), "initial.pool").toPath().toString()));
		this.rematch = new BattleType().path(new File(pathBuilder(this), new File(this.getPath(), "rematch.pool").toPath().toString()));
		this.leaders = Lists.newArrayList();
	}

	@Override
	public Champion initialize() {
		this.initial.init();
		this.rematch.init();

		this.queue = new LinkedList<>();

		if(this.npcs > 0) {
			this.open = true;
		}
		return this;
	}

	@Override
	public UUID getUuid() {
		return this.uuid;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Category getCategory() {
		return this.getBelonging().getCategory();
	}

	@Override
	public int getWeight() {
		return -1;
	}

	@Override
	public boolean isReady() {
		return this.name != null && this.getBadge() != null && this.arena != null && this.arena.isSetup();
	}

	@Override
	public boolean canChallenge(Player player) {
		if(!this.getBelonging().canChallenge(player)) {
			return false;
		}

		PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());
		if(!Arrays.stream(pd.getDefeatedElite4()).allMatch(stage -> stage)) {
			player.sendMessage(MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.ERRORS_CANT_CHALLENGE_CHAMPION, null, null));
			return false;
		}

		return true;
	}

	@Override
	public boolean canAccess(Player player) {
		return this.getBelonging().canAccess(player);
	}

	@Override
	public void startBattle(Player leader, Player challenger, List<BU3PokemonSpec> team) throws BattleStartException {
		if(this.getBattleSettings(this.getBattleType(challenger)).getPool().getTeam().size() == 0) {
			throw new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.COMMANDS_ACCEPT_EMPTY_TEAM_POOL, null, null));
		}

		PlayerPartyStorage cs = this.verify(challenger);
		PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(challenger.getUniqueId());

		if(!this.checkRequirements(pd)) {
			throw new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.ERRORS_E4_NOT_ALL_BADGES, null, null));
		}

		PlayerPartyStorage ls = Pixelmon.storageManager.getParty(leader.getUniqueId());
		ls.heal();
		BattleParticipant bpL = new PlayerParticipant((EntityPlayerMP) leader, ls.getAndSendOutFirstAblePokemon((EntityPlayerMP) leader));

		leader.setLocationAndRotationSafely(new Location<>(leader.getWorld(), arena.getLeader().getPosition()), arena.getLeader().getRotation());
		challenger.setLocationAndRotationSafely(new Location<>(challenger.getWorld(), arena.getChallenger().getPosition()), arena.getChallenger().getRotation());

		cs.heal();
		GymBattleStartEvent gbse = new GymBattleStartEvent(leader, challenger, this);
		if(!gbse.isCancelled()) {
			BattleRegistry.register(new Challenge(leader, challenger, this.getBattleType(challenger)), this);
			com.pixelmonmod.pixelmon.battles.BattleRegistry.startBattle(
					new BattleParticipant[] {bpL},
					new BattleParticipant[] {new PlayerParticipant((EntityPlayerMP) challenger, cs.getAndSendOutFirstAblePokemon((EntityPlayerMP) challenger))},
					this.getBattleSettings(this.getBattleType(challenger)).getBattleRules()
			);
		}
	}

	@Override
	public void startBattle(NPCChatting leader, Player challenger, List<BU3PokemonSpec> team) throws BattleStartException {
		if(this.getBattleSettings(this.getBattleType(challenger)).getPool().getTeam().size() == 0) {
			throw new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.COMMANDS_ACCEPT_EMPTY_TEAM_POOL, null, null));
		}

		PlayerPartyStorage cs = this.verify(challenger);
		PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(challenger.getUniqueId());

		if(!this.checkRequirements(pd)) {
			throw new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.ERRORS_E4_NOT_ALL_BADGES, null, null));
		}

		challenger.setLocationAndRotationSafely(new Location<>(challenger.getWorld(), arena.getChallenger().getPosition()), arena.getChallenger().getRotation());

		GymBattleStartEvent gbse = new GymBattleStartEvent((Entity) leader, challenger, this);
		if(!gbse.isCancelled()) {
			BattleRegistry.register(new Challenge((Entity) leader, challenger, this.getBattleType(challenger)), this);
			com.pixelmonmod.pixelmon.battles.BattleRegistry.startBattle(
					new BattleParticipant[] {new ChattingNPCParticipant(leader, this.convertFromSpecs((Entity) leader, challenger, team), 1)},
					new BattleParticipant[] {new PlayerParticipant((EntityPlayerMP) challenger, cs.getAndSendOutFirstAblePokemon((EntityPlayerMP) challenger))},
					this.getBattleSettings(this.getBattleType(challenger)).getBattleRules());
		}
	}

	private PlayerPartyStorage verify(Player challenger) throws BattleStartException {
		if(this.getBattleSettings(this.getBattleType(challenger)).getPool().getTeam().size() == 0) {
			throw new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.COMMANDS_ACCEPT_EMPTY_TEAM_POOL, null, null));
		}

		PlayerPartyStorage cs = Pixelmon.storageManager.getParty(challenger.getUniqueId());
		PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(challenger.getUniqueId());

		if(!this.checkRequirements(pd)) {
			throw new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.ERRORS_E4_NOT_ALL_BADGES, null, null));
		}

		if(!pd.getCurrentEliteFour().equals(this.getBelonging())) {
			throw new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.ERRORS_E4_DIFFERING, null, null));
		}

		return cs;
	}

	@Override
	public void onDefeat(Challenge challenge, PlayerData data) {
		data.resetDefeatedE4();
		Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
		tokens.put("bu3_category", src -> Optional.of(Text.of(this.getCategory().getId().substring(0, 1).toUpperCase() + this.getCategory().getId().substring(1))));
		challenge.getChallenger().sendMessage(MessageUtils.fetchAndParseMsg(challenge.getChallenger(), MsgConfigKeys.BATTLES_WIN_CHAMPION, tokens, null));
	}

	public EnumBattleType getBattleType(Player player) {
		PlayerData data = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());
		return data.hasBadge(this.getBelonging().getBadge()) ? EnumBattleType.Rematch : EnumBattleType.First;
	}

	@Override
	public BattleType getBattleSettings(EnumBattleType type) {
		if(type == EnumBattleType.First) {
			return this.initial;
		} else {
			return this.rematch;
		}
	}

	@Override
	public Badge getBadge() {
		return this.getBelonging().getBadge();
	}

	@Override
	public boolean addLeader(UUID uuid) {
		return this.leaders.add(uuid);
	}

	public boolean removeLeader(UUID uuid) {
		return this.leaders.remove(uuid);
	}

	@Override
	public void addNPCLeader(NPCChatting npc) {
		this.npcs++;
		npc.getEntityData().setString("BU3-ID", this.getUuid().toString());
	}

	@Override
	public void removeNPCLeader() {
		this.npcs--;
	}

	@Override
	public List<UUID> getLeaders() {
		return this.leaders;
	}

	@Override
	public int getNPCs() {
		return this.npcs;
	}

	@Override
	public boolean isOpen() {
		return this.open;
	}

	@Override
	public boolean open() {
		if(this.queue != null && this.arena.isSetup()) {
			this.open = true;
			return true;
		}

		return false;
	}

	@Override
	public boolean close() {
		if(!this.open) {
			return false;
		}

		this.open = false;
		return true;
	}

	@Override
	public boolean queue(Player player) {
		if(this.canAccess(player) && this.canChallenge(player)) {
			this.queue.add(player.getUniqueId());
			return true;
		}

		return false;
	}

	@Override
	public Queue<UUID> getQueue() {
		return this.queue;
	}

	@Override
	public int getStage() {
		return -1;
	}

	@Override
	public String getPath() {
		return String.format("e4/%s/stages/champion/", this.getBelonging().getName());
	}

	public EliteFour getBelonging() {
		return this.e4;
	}
}
