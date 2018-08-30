package gg.psyduck.bidoofunleashed.impl.requirements;

import com.google.common.collect.Maps;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ChampionRequirement implements Requirement {

    @Override
    public boolean passes(Gym gym, Player player) throws Exception {
        PlayerData data = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(player.getUniqueId());
        boolean passes = true;
        for (Boolean beaten : data.getBeatenE4().values()) {
            if (!beaten) {
                passes = false;
            }
        }
        return passes;
    }

    @Override
    public void onInvalid(Gym gym, Player player) {
        Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
        //tokens.put("bu3_badge", s -> Optional.of(Text.of(badgeRequirment)));
        Text parsed = MessageUtils.fetchAndParseMsg(player, MsgConfigKeys.REQUIREMENT_GYM, tokens, null);
        player.sendMessage(parsed);
    }
}
