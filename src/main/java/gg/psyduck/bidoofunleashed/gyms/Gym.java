package gg.psyduck.bidoofunleashed.gyms;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.time.Time;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.storage.PlayerStorage;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BattlableBuilder;
import gg.psyduck.bidoofunleashed.api.battlables.arenas.Arena;
import gg.psyduck.bidoofunleashed.api.cooldowns.Cooldown;
import gg.psyduck.bidoofunleashed.api.events.GymBattleStartEvent;
import gg.psyduck.bidoofunleashed.api.exceptions.BattleStartException;
import gg.psyduck.bidoofunleashed.api.pixelmon.participants.TempTeamParticipant;
import gg.psyduck.bidoofunleashed.api.pixelmon.participants.TempTrainerTeamParticipant;
import gg.psyduck.bidoofunleashed.api.pixelmon.specs.BU3PokemonSpec;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.PixelmonStorage;
import gg.psyduck.bidoofunleashed.api.enums.EnumBattleType;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;
import gg.psyduck.bidoofunleashed.api.battlables.battletypes.BattleType;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
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
import org.spongepowered.api.world.Location;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

import static gg.psyduck.bidoofunleashed.storage.dao.file.FileDao.pathBuilder;

@Getter
public class Gym implements BU3Battlable, Cooldown {

	private UUID uuid;

	@Setter private String name;
	@Setter private Badge badge;
	private Arena arena;
	@Setter private Category category;
	@Setter private int weight;
	@Setter private long cooldown;

	private BattleType initial;
	private BattleType rematch;

	private List<UUID> leaders;
	private int npcs = 0;

	private transient Queue<UUID> queue = new LinkedList<>();
	private transient boolean open = false;

	private Gym(BattlableBuilder<Gym> builder) {
		this.uuid = UUID.randomUUID();
		this.name = builder.name;
		this.badge = builder.badge;
		this.arena = builder.arena != null ? builder.arena : new Arena();
		this.leaders = builder.leaders;
		this.category = new Category(builder.category);

		List<Gym> mappings = BidoofUnleashed.getInstance().getDataRegistry().sortedGyms().get(this.category);

		this.cooldown = builder.cooldown;
		this.weight = builder.weight >= 0 ? builder.weight : mappings != null ? mappings.size() : 0;
		this.initial = builder.initial.path(new File(pathBuilder(this), "gyms/" + this.name + "/initial.pool"));
		this.rematch = builder.rematch.path(new File(pathBuilder(this), "gyms/" + this.name + "/rematch.pool"));

		this.initialize();
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
	public boolean checkCooldown(Player player) {
		PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());
		if(pd.beforeCooldownPeriod(this)) {
			LocalDateTime till = pd.getCooldowns().get(this.getName()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			Duration duration = Duration.between(LocalDateTime.now(), till);

			Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
			tokens.put("bu3_gym", src -> Optional.of(Text.of(this.getName())));
			tokens.put("bu3_cooldown_time", src -> Optional.of(Text.of(new Time(duration.getSeconds()).toString())));

			player.sendMessages(MessageUtils.fetchAndParseMsgs(player, MsgConfigKeys.MISC_CHALLENGE_COOLDOWN_GYM, tokens, null));
			return false;
		}
		return true;
	}

	public boolean justArenaWaiting() {
		return this.name != null && this.badge != null && this.arena != null && !this.arena.isSetup();
	}

	@Override
	public boolean isReady() {
		return this.name != null && this.badge != null && this.arena != null && this.arena.isSetup();
	}

	public boolean canChallenge(Player player) {
		return this.open && player.hasPermission(this.toPermission("gyms") + ".contest") && this.checkCooldown(player);
	}

	@Override
	public boolean canAccess(Player player) {
		return this.isReady() && this.arena.isProper(this.arena.getSpectators()) && player.hasPermission(this.toPermission("gyms") + ".access");
	}

	public boolean queue(Player player) {
		if(this.canAccess(player) && this.canChallenge(player)) {
			this.queue.add(player.getUniqueId());
			return true;
		}

		return false;
	}

	/**
	 * Starts a battle between two players. Since battles with NPCs are essentially pre-initialized, we really just need to do
	 * the work this method does inside the battle listener to avoid circular references.
	 *
	 * @param leader The leader of the gym accepting the challenge
	 * @param challenger The challenger trying to prove their worth
	 */
	@Override
	public void startBattle(Player leader, Player challenger, List<BU3PokemonSpec> team) throws BattleStartException {
		if(this.getBattleSettings(this.getBattleType(challenger)).getPool().getTeam().size() == 0) {
			throw new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.COMMANDS_ACCEPT_EMPTY_TEAM_POOL, null, null));
		}

		BattleParticipant bpL = new TempTeamParticipant((EntityPlayerMP) leader);
		bpL.allPokemon = new PixelmonWrapper[team.size()];

		this.apply(bpL, leader, team);

		leader.setLocationAndRotationSafely(new Location<>(leader.getWorld(), arena.getLeader().getPosition()), arena.getLeader().getRotation());

		challenger.setLocationAndRotationSafely(new Location<>(challenger.getWorld(), arena.getChallenger().getPosition()), arena.getChallenger().getRotation());
		PlayerStorage storage = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) challenger).orElse(null);
		if(storage == null) {
			throw new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.ERRORS_MISSING_PLAYER_STORAGE, null, null));
		}

		storage.healAllPokemon((World) challenger.getWorld());
		EntityPixelmon cs = storage.getFirstAblePokemon((World) challenger.getWorld());
		GymBattleStartEvent gbse = new GymBattleStartEvent(leader, challenger, this);
		if(!gbse.isCancelled()) {
			BattleRegistry.register(new Challenge(leader, challenger, this.getBattleType(challenger)), this);
			new BattleControllerBase(bpL, new PlayerParticipant((EntityPlayerMP) challenger, cs), this.getBattleSettings(this.getBattleType(challenger)).getBattleRules());
			BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(challenger.getUniqueId()).updateCooldown(this);
		}
	}

	@Override
	public void startBattle(NPCTrainer leader, Player challenger, List<BU3PokemonSpec> team) throws BattleStartException {
		if(this.getBattleSettings(this.getBattleType(challenger)).getPool().getTeam().size() == 0) {
			throw new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.COMMANDS_ACCEPT_EMPTY_TEAM_POOL, null, null));
		}

		EnumBattleType type = this.getBattleType(challenger);
		EntityPixelmon first = PixelmonStorage.pokeBallManager.getPlayerStorage((EntityPlayerMP) challenger).orElseThrow(() -> new BattleStartException(MessageUtils.fetchAndParseMsg(challenger, MsgConfigKeys.ERRORS_MISSING_PLAYER_STORAGE, null, null))).getFirstAblePokemon((World) challenger.getWorld());
		GymBattleStartEvent gbse = new GymBattleStartEvent((Entity) leader, challenger, this);
		if(!gbse.isCancelled()) {
			BattleRegistry.register(new Challenge((Entity) leader, challenger, type), this);
			new BattleControllerBase(new TempTrainerTeamParticipant(leader, (EntityPlayerMP) challenger, team, this), new PlayerParticipant((EntityPlayerMP) challenger, first), this.getBattleSettings(this.getBattleType(challenger)).getBattleRules());
			BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(challenger.getUniqueId()).updateCooldown(this);
		}
	}

	@Override
	public void onDefeat(Challenge challenge, PlayerData data) {
		Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
		tokens.put("bu3_gym", src -> Optional.of(Text.of(this.getName())));
		challenge.getChallenger().sendMessage(MessageUtils.fetchAndParseMsg(challenge.getChallenger(), MsgConfigKeys.BATTLES_WIN_GYM, tokens, null));
	}

	public EnumBattleType getBattleType(Player player) {
		PlayerData data = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());
		return data.hasBadge(this.badge) ? EnumBattleType.Rematch : EnumBattleType.First;
	}

	public Gym initialize() {
		this.initial.init();
		this.rematch.init();

		this.queue = new LinkedList<>();

		if(this.npcs > 0) {
			this.open = true;
		}

		return this;
	}

	public boolean open() {
		if(this.queue != null && this.arena.isSetup()) {
			this.open = true;
			return true;
		}

		return false;
    }

    public boolean close() {
		if(!this.open) {
			return false;
		}

		this.open = false;
		return true;
    }

    public BattleType getBattleSettings(EnumBattleType type) {
		if(type == EnumBattleType.First) {
			return this.initial;
		} else {
			return this.rematch;
		}
    }

	public static BattlableBuilder<Gym> builder() {
		return new BattlableBuilder<Gym>() {
			@Override
			public Gym build() {
				return new Gym(this);
			}
		};
	}
}
