package gg.psyduck.bidoofunleashed.e4;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3BattleBase;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
import gg.psyduck.bidoofunleashed.api.battlables.arenas.Arena;
import gg.psyduck.bidoofunleashed.api.battlables.battletypes.BattleType;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.events.GymBattleStartEvent;
import gg.psyduck.bidoofunleashed.api.exceptions.BattleStartException;
import gg.psyduck.bidoofunleashed.api.pixelmon.participants.TempTeamParticipant;
import gg.psyduck.bidoofunleashed.api.pixelmon.participants.TempTrainerTeamParticipant;
import gg.psyduck.bidoofunleashed.api.pixelmon.specs.BU3PokemonSpec;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import gg.psyduck.bidoofunleashed.gyms.temporary.BattleRegistry;
import gg.psyduck.bidoofunleashed.gyms.temporary.Challenge;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.util.*;
import java.util.function.Function;

import static gg.psyduck.bidoofunleashed.storage.dao.file.FileDao.pathBuilder;

@Getter
public class E4Stage implements Stage {

	private transient Queue<UUID> queue = new LinkedList<>();
	private transient boolean open;

	@Setter private transient EliteFour e4;

	private Integer stage;

	private String name;
	private Arena arena;

	private BattleType initial;
	private BattleType rematch;

	protected List<UUID> leaders;
	protected int npcs = 0;

	public E4Stage(Integer stage) {
		this(null, stage);
	}

	public E4Stage(EliteFour e4, Integer stage) {
		this.e4 = e4;

		this.stage = stage;
		this.name = "Stage " + stage;
		this.arena = new Arena();
		this.initial = new BattleType().path(new File(pathBuilder(this), new File(this.getPath(), "initial.pool").toPath().toString()));
		this.rematch = new BattleType().path(new File(pathBuilder(this), new File(this.getPath(), "rematch.pool").toPath().toString()));
		this.leaders = Lists.newArrayList();
	}

	@Override
	public E4Stage initialize() {
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
		return this.getBelonging().getUuid();
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
		return this.getBelonging().canChallenge(player);
	}

	@Override
	public boolean canAccess(Player player) {
		return this.getBelonging().canAccess(player);
	}

	@Override
	public void startBattle(Player leader, Player challenger, List<BU3PokemonSpec> team) throws BattleStartException {
		PlayerStorage cs = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) challenger).orElseThrow(() -> new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.ERRORS_MISSING_PLAYER_STORAGE, null, null)));

		PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(challenger.getUniqueId());
		if(pd.getDefeatedElite4()[this.stage]) {
			throw new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.ERRORS_E4_ALREADY_DEFEATED, null, null));
		}

		if(!pd.getCurrentEliteFour().equals(this.getBelonging())) {
			throw new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.ERRORS_E4_DIFFERING, null, null));
		}

		BattleParticipant bpL = new TempTeamParticipant((EntityPlayerMP) leader);
		bpL.allPokemon = new PixelmonWrapper[team.size()];
		this.apply(bpL, leader, team);

		cs.healAllPokemon((World) challenger.getWorld());
		EntityPixelmon first = cs.getFirstAblePokemon((World) challenger.getWorld());
		GymBattleStartEvent gbse = new GymBattleStartEvent(leader, challenger, this);
		if(!gbse.isCancelled()) {
			BattleRegistry.register(new Challenge(leader, challenger, this.getBattleType(challenger)), this);
			new BattleControllerBase(bpL, new PlayerParticipant((EntityPlayerMP) challenger, first), this.getBattleSettings(this.getBattleType(challenger)).getBattleRules());
			BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(challenger.getUniqueId()).updateCooldown(this);
		}
	}

	@Override
	public void startBattle(NPCTrainer leader, Player challenger, List<BU3PokemonSpec> team) throws BattleStartException {
		PlayerStorage cs = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) challenger).orElseThrow(() -> new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.ERRORS_MISSING_PLAYER_STORAGE, null, null)));
		PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(challenger.getUniqueId());
		if(pd.getDefeatedElite4()[this.stage]) {
			throw new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.ERRORS_E4_ALREADY_DEFEATED, null, null));
		}

		if(!pd.getCurrentEliteFour().equals(this.getBelonging())) {
			throw new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.ERRORS_E4_DIFFERING, null, null));
		}

		EntityPixelmon first = cs.getFirstAblePokemon((World) challenger.getWorld());
		GymBattleStartEvent gbse = new GymBattleStartEvent((Entity) leader, challenger, this);
		if(!gbse.isCancelled()) {
			BattleRegistry.register(new Challenge((Entity) leader, challenger, this.getBattleType(challenger)), this);
			new BattleControllerBase(new TempTrainerTeamParticipant(leader, (EntityPlayerMP) challenger, team, this), new PlayerParticipant((EntityPlayerMP) challenger, first), this.getBattleSettings(this.getBattleType(challenger)).getBattleRules());
			BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(challenger.getUniqueId()).updateCooldown(this);
		}
	}

	@Override
	public void onDefeat(Challenge challenge, PlayerData data) {
		data.defeatStage(this.getStage());
		Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
		tokens.put("bu3_e4", src -> Optional.of(Text.of(this.getName())));
		challenge.getChallenger().sendMessage(MessageUtils.fetchAndParseMsg(challenge.getChallenger(), MsgConfigKeys.BATTLES_WIN_E4, tokens, null));
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
	public void addNPCLeader(NPCTrainer npc) {
		this.npcs++;
		npc.getEntityData().setString("BU3-ID", this.getUuid().toString());
	}

	@Override
	public void removeNPCLeader() {
		this.npcs--;
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
	public String getPath() {
		return String.format("e4/%s/stages/stage %d/", this.getBelonging().getName(), stage);
	}

	@Override
	public boolean queue(Player player) {
		if(this.canAccess(player) && this.canChallenge(player)) {
			this.queue.add(player.getUniqueId());
			return true;
		}

		return false;
	}

	public EliteFour getBelonging() {
		return this.e4;
	}
}
