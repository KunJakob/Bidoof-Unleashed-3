package gg.psyduck.bidoofunleashed.commands.arguments;

import gg.psyduck.bidoofunleashed.gyms.Gym;
import org.spongepowered.api.command.CommandSource;

import java.util.regex.Pattern;

public class ExistingGymSpec {

	public static Gym parseValue(CommandSource src, Gym gym, String args) throws Exception {
		String[] split = args.split(" ");
		Pattern pattern = Pattern.compile("(?<identifier>[a-zA-Z]+):(?<value>[a-zA-Z]+(:[a-zA-Z]+)*)");
		Gym.Builder builder = gym.toBuilder();

		for(String mapping : split) {
			java.util.regex.Matcher matcher = pattern.matcher(mapping);
			if(matcher.matches()) {
				String identifier = matcher.group("identifier");
				String value = matcher.group("value");

				for(EnumGymDetails detail : EnumGymDetails.values()) {
					if(detail.identifiers.stream().anyMatch(alias -> alias.equalsIgnoreCase(identifier))) {
						detail.application.accept(src, builder, value);
						break;
					}
				}
			}
		}

		return builder.build();
	}
}
