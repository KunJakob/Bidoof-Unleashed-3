package gg.psyduck.bidoofunleashed.e4;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.time.Time;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3BattleBase;
import gg.psyduck.bidoofunleashed.api.battlables.BattlableBuilder;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
import gg.psyduck.bidoofunleashed.api.cooldowns.Cooldown;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

@Getter
@Setter
public class EliteFour implements BU3BattleBase<EliteFour>, Cooldown {

	private UUID uuid;
	private String name;
	private Category category;
	private long cooldown;
	private Badge badge;

	private List<E4Stage> stages;
	private Champion champion;

	public EliteFour(E4Builder builder) {
		this.uuid = UUID.randomUUID();
		this.name = builder.name;
		this.category = new Category(builder.category);
		this.cooldown = builder.cooldown;
		this.badge = builder.badge;

		this.stages = builder.stages;
		this.champion = builder.champion;
	}

	public EliteFour initialize() {
		for(E4Stage stage : stages) {
			stage.initialize();
		}

		champion.initialize();
		return this;
	}

	@Override
	public UUID getUuid() {
		return this.uuid;
	}

	@Override
	public int getWeight() {
		List<Gym> inCategory = BidoofUnleashed.getInstance().getDataRegistry().sortedGyms().get(this.category);
		inCategory.sort(Comparator.comparing(Gym::getWeight));
		return inCategory.get(inCategory.size() - 1).getWeight() + 1;
	}

	public boolean isReady() {
		return this.stages.size() == 4 && this.stages.stream().allMatch(E4Stage::isReady) && this.champion != null && this.champion.isReady();
	}

	public boolean isOpen() {
		return this.champion.isOpen() || this.stages.stream().anyMatch(Stage::isOpen);
	}

	@Override
	public boolean checkCooldown(Player player) {
		PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());
		if(pd.beforeCooldownPeriod(this)) {
			LocalDateTime till = pd.getCooldowns().get(this.getName()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			Duration duration = Duration.between(LocalDateTime.now(), till);

			Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
			tokens.put("bu3_e4", src -> Optional.of(Text.of(this.getName())));
			tokens.put("bu3_cooldown_time", src -> Optional.of(Text.of(new Time(duration.getSeconds()).toString())));

			player.sendMessages(MessageUtils.fetchAndParseMsgs(player, MsgConfigKeys.MISC_CHALLENGE_COOLDOWN_E4, tokens, null));
			return false;
		}
		return true;
	}

	@Override
	public boolean canChallenge(Player player) {
		List<Gym> inCategory = BidoofUnleashed.getInstance().getDataRegistry().sortedGyms().get(this.category);
		PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());
		for(Gym gym : inCategory) {
			if(pd.getBadges().stream().noneMatch(badge -> gym.getBadge().getName().equals(badge.getName()))) {
				player.sendMessage(MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.ERRORS_E4_NOT_ALL_BADGES, null, null));
				return false;
			}
		}

		if(pd.getCurrentEliteFour() != null && pd.getCurrentEliteFour() != this) {
			if(!pd.getCurrentEliteFour().getName().equalsIgnoreCase(this.getName())) {
				player.sendMessage(MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.ERRORS_E4_DIFFERING, null, null));
				return false;
			}
		}

		return this.isOpen() && player.hasPermission(this.toPermission("e4") + ".contest") && this.checkCooldown(player);
	}

	@Override
	public boolean canAccess(Player player) {
		return this.isReady() && player.hasPermission(this.toPermission("e4") + ".access");
	}

	public void startChallenge(Player player) {
		PlayerData pd = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());
		pd.setCurrentEliteFour(this);
		pd.resetDefeatedE4();
	}

	public static E4Builder builder() {
		return new E4Builder();
	}

	public static class E4Builder extends BattlableBuilder<EliteFour> {

		private List<E4Stage> stages = Lists.newArrayList();
		private Champion champion;

		public E4Builder stage(E4Stage stage) {
			this.stages.add(stage);
			return this;
		}

		public E4Builder champion(Champion champion) {
			this.champion = champion;
			return this;
		}

		@Override
		public EliteFour build() {
			return new EliteFour(this);
		}
	}
}
