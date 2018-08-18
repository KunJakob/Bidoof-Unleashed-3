package gg.psyduck.bidoofunleashed.internal;

import com.google.common.collect.Lists;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.exceptions.NucleusException;
import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

public class TextParsingUtils {

	private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
	static {
		suffixes.put(1_000L, "K");
		suffixes.put(1_000_000L, "M");
		suffixes.put(1_000_000_000L, "B");
		suffixes.put(1_000_000_000_000L, "T");
	}

	public NucleusTextTemplate getTemplate(String text) throws NucleusException {
		return NucleusAPI.getMessageTokenService().createFromString(text);
	}

	public List<NucleusTextTemplate> getTemplates(List<String> text) throws NucleusException {
		List<NucleusTextTemplate> templates = Lists.newArrayList();
		for(String str : text) {
			templates.add(getTemplate(str));
		}

		return templates;
	}

	public Text parse(String template, CommandSource source, @Nullable Map<String, Function<CommandSource, Optional<Text>>> tokens, @Nullable Map<String, Object> variables) {
		try {
			return this.parse(this.getTemplate(template), source, tokens, variables);
		} catch (NucleusException e) {
			return Text.EMPTY;
		}
	}

	public List<Text> parse(Collection<String> templates, CommandSource source, @Nullable Map<String, Function<CommandSource, Optional<Text>>> tokens, @Nullable Map<String, Object> variables) {
		try {
			return this.parse(this.getTemplates(Lists.newArrayList(templates)), source, tokens, variables);
		} catch (NucleusException e) {
			return Lists.newArrayList();
		}
	}

	public Text parse(NucleusTextTemplate template, CommandSource source, @Nullable Map<String, Function<CommandSource, Optional<Text>>> tokens, @Nullable Map<String, Object> variables) {
		return template.getForCommandSource(source, tokens, variables);
	}

	public List<Text> parse(List<NucleusTextTemplate> templates, CommandSource source, @Nullable Map<String, Function<CommandSource, Optional<Text>>> tokens, @Nullable Map<String, Object> variables) {
		List<Text> output = Lists.newArrayList();
		for(NucleusTextTemplate template : templates) {
			output.add(this.parse(template, source, tokens, variables));
		}

		return output;
	}

	public Text getNameFromUser(CommandSource source) {
		return Text.of(source.getName());
	}

	public Text getBalance(CommandSource source, boolean abbreviated) {
		if(source instanceof User) {
			EconomyService econ = BidoofUnleashed.getInstance().getEconomy();

			Optional<UniqueAccount> acc = econ.getOrCreateAccount(((User) source).getUniqueId());
			if(acc.isPresent()) {
				if(!abbreviated) {
					return econ.getDefaultCurrency().format(acc.get().getBalance(econ.getDefaultCurrency()));
				} else {
					BigDecimal balance = acc.get().getBalance(econ.getDefaultCurrency());
					return format(econ, balance.longValue());
				}
			}
		}

		return Text.of(BidoofUnleashed.getInstance().getEconomy().getDefaultCurrency().format(BigDecimal.ZERO));
	}

	private Text format(EconomyService econ, long value) {
		if(value < 0) {
			return Text.of("-", format(econ, -value));
		}
		if(value < 1000) {
			return econ.getDefaultCurrency().format(BigDecimal.valueOf(value));
		}

		Map.Entry<Long, String> entry = suffixes.floorEntry(value);
		Long divideBy = entry.getKey();
		String suffix = entry.getValue();

		long truncated = value / (divideBy / 10);
		boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
		return hasDecimal ? Text.of((truncated / 10d), suffix, " ", econ.getDefaultCurrency().getPluralDisplayName()) :
				Text.of((truncated / 10), suffix, " ", econ.getDefaultCurrency().getPluralDisplayName());
	}
}
