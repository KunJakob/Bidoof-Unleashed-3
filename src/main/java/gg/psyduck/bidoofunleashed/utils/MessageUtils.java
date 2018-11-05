package gg.psyduck.bidoofunleashed.utils;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import com.nickimpact.impactor.api.logger.Logger;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class MessageUtils {

	public static List<Text> genErrorMessage(String label, String... info) {
		List<Text> error = Lists.newArrayList(
				Text.of("========== ", label, " ==========")
		);

		for(String str : info) {
			error.add(Text.of(str));
		}

		return error;
	}

	public static void genAndSendErrorMessage(String label, String... info) {
		BidoofUnleashed.getInstance().getLogger().send(Logger.Prefixes.DEBUG, genErrorMessage(label, info));
	}

	public static Text fetchMsg(String def) {
		return TextSerializers.FORMATTING_CODE.deserialize(def);
	}

	public static Text fetchMsg(ConfigKey<String> key) {
		return TextSerializers.FORMATTING_CODE.deserialize(BidoofUnleashed.getInstance().getMsgConfig().get(key));
	}

	public static Text fetchMsg(CommandSource source, String def) {
		return fetchAndParseMsg(source, def, null, null);
	}

	public static Text fetchMsg(CommandSource source, ConfigKey<String> key) {
		return fetchAndParseMsg(source, key, null, null);
	}

	public static Text fetchAndParseMsg(CommandSource source, String def, @Nullable Map<String, Function<CommandSource, Optional<Text>>> tokens, @Nullable Map<String, Object> variables) {
		return BidoofUnleashed.getInstance().getTextParsingUtils().parse(
				def,
				source,
				tokens,
				variables
		);
	}

	public static Text fetchAndParseMsg(CommandSource source, ConfigKey<String> key, @Nullable Map<String, Function<CommandSource, Optional<Text>>> tokens, @Nullable Map<String, Object> variables) {
		return BidoofUnleashed.getInstance().getTextParsingUtils().parse(
				BidoofUnleashed.getInstance().getMsgConfig().get(key),
				source,
				tokens,
				variables
		);
	}

	public static List<Text> fetchMsgs(ConfigKey<List<String>> key) {
		List<Text> output = Lists.newArrayList();
		for(String str : BidoofUnleashed.getInstance().getMsgConfig().get(key)) {
			output.add(TextSerializers.FORMATTING_CODE.deserialize(str));
		}
		return output;
	}

	public static List<Text> fetchMsgs(CommandSource source, ConfigKey<List<String>> key) {
		return fetchAndParseMsgs(source, key, null, null);
	}

	public static List<Text> fetchAndParseMsgs(CommandSource source, ConfigKey<List<String>> key, @Nullable Map<String, Function<CommandSource, Optional<Text>>> tokens, @Nullable Map<String, Object> variables) {
		return BidoofUnleashed.getInstance().getTextParsingUtils().parse(
				BidoofUnleashed.getInstance().getMsgConfig().get(key),
				source,
				tokens,
				variables
		);
	}

	public static String titlize(String input) {
		String[] sep = input.split("\\s+");
		StringBuilder result = new StringBuilder();
		for(String str : sep) {
			result.append(str.substring(0, 1).toUpperCase()).append(str.substring(1).toLowerCase()).append(" ");
		}
		result.delete(result.length() - 1, result.length());

		return result.toString();
	}
}
