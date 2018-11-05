package gg.psyduck.bidoofunleashed.e4;

import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.temporary.Challenge;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class Champion extends E4Stage {

	public Champion() {
		super(null);
	}

	public Champion(EliteFour e4) {
		super(e4, null);
	}

	@Override
	public boolean canChallenge(Player player) {
		if(!super.canChallenge(player)) {
			PlayerData data = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());
			return Arrays.stream(data.getDefeatedElite4()).allMatch(val -> val);
		}

		return false;
	}

	@Override
	public void onDefeat(Challenge challenge, PlayerData data) {
		data.defeatStage(this.getStage());
		Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
		tokens.put("bu3_e4", src -> Optional.of(Text.of(this.getName())));
		challenge.getChallenger().sendMessage(MessageUtils.fetchAndParseMsg(challenge.getChallenger(), MsgConfigKeys.BATTLES_WIN_CHAMPION, tokens, null));
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
	public String getPath() {
		return String.format("e4/%s/stages/champion/", this.getBelonging().getName());
	}
}
