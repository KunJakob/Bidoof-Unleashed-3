package gg.psyduck.bidoofunleashed.internal.nucleus;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.exceptions.PluginAlreadyRegisteredException;
import io.github.nucleuspowered.nucleus.api.service.NucleusMessageTokenService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TokenService implements NucleusMessageTokenService.TokenParser {

	private final Map<String, Translator> tokens = Maps.newHashMap();

	public TokenService() {
		tokens.put("bu3_prefix", (p, v, m) -> Optional.of(Text.of(TextColors.YELLOW, "Bidoof Unleashed", TextColors.GRAY, " \u00bb")));
		tokens.put("bu3_error", (p, v, m) -> Optional.of(Text.of(TextColors.YELLOW, "Bidoof Unleashed", TextColors.GRAY, "(", TextColors.RED, "Error", TextColors.GRAY, ")")));

		try {
			NucleusAPI.getMessageTokenService().register(
					BidoofUnleashed.getInstance().getPluginContainer(),
					this
			);
			this.getTokenNames().forEach(x -> NucleusAPI.getMessageTokenService().registerPrimaryToken(x.toLowerCase(), BidoofUnleashed.getInstance().getPluginContainer(), x.toLowerCase()));
		} catch (PluginAlreadyRegisteredException e) {
			e.printStackTrace();
		}
	}

	@Nonnull
	@Override
	public Optional<Text> parse(String tokenInput, CommandSource source, Map<String, Object> variables) {
		String[] split = tokenInput.split("\\|", 2);
		String var = "";
		if (split.length == 2) {
			var = split[1];
		}

		return tokens.getOrDefault(split[0].toLowerCase(), (p, v, m) -> Optional.empty()).get(source, var, variables);
	}

	private Set<String> getTokenNames() {
		return Sets.newHashSet(tokens.keySet());
	}
}
