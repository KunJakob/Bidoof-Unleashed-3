package gg.psyduck.bidoofunleashed.config;

import com.google.common.collect.ImmutableMap;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import com.nickimpact.impactor.api.configuration.IConfigKeys;
import com.nickimpact.impactor.api.configuration.keys.StringKey;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class MsgConfigKeys implements IConfigKeys {

	public static final ConfigKey<String> STARTUP_STORAGE_PROVIDER = StringKey.of("startup.phase.init.storage.type", "Loading storage provider... [{{storage_type}}]");

	public static final ConfigKey<String> COMMANDS_CHALLENGE_QUEUED = StringKey.of("commands.challenge.queued", "{{bu3_prefix}} &7You've been queued to challenge the &e{{bu3_gym}} Gym&7!");
    public static final ConfigKey<String> COMMANDS_GIVE_BADGE_LEADER = StringKey.of("commands.challenge.queued", "{{bu3_prefix}} &7You've given the {{bu3_badge}} Badge to {{player}}");
    public static final ConfigKey<String> COMMANDS_GIVE_BADGE_CHALLENGER = StringKey.of("commands.challenge.queued", "{{bu3_prefix}} &7You've been given the {{bu3_badge}} Badge!");
    public static final ConfigKey<String> COMMANDS_REMOVE_BADGE_LEADER = StringKey.of("commands.challenge.queued",
            "{{bu3_prefix}} &7You've taken the {{bu3_badge}} Badge from {{player}}");
    public static final ConfigKey<String> COMMANDS_REMOVE_BADGE_CHALLENGER = StringKey.of("commands.challenge.queued",
            "{{bu3_prefix}} &7The {{bu3_badge}} Badge has been taken from you");


	public static final ConfigKey<String> REQUIREMENT_GYM = StringKey.of("requirements.gym", "{{bu3_error}} &7Unfortunately, you can't challenge this gym until you've earned the &e{{bu3_badge}}&7...");
	public static final ConfigKey<String> REQUIREMENT_LEVELCAP = StringKey.of("requirements.level-cap", "{{bu3_error}} &7Unfortunately, a member of your party exceeds the level cap of &e{{bu3_level_cap}}&7...");
	public static final ConfigKey<String> REQUIREMENT_EVOLUTION = StringKey.of("requirements.evolution", "{{bu3_error}} &7Unfortunately, a member of your party represents an unnatural evolution...");

	public static final ConfigKey<String> SOURCE_NOT_PLAYER = StringKey.of("commands.error.src-not-player", "{{bu3_error}} &7You must be a player to use this command!");
    public static final ConfigKey<String> PLAYER_NOT_LEADER = StringKey.of("commands.error.player-not-leader", "{{bu3_error}} &7You must be a gym leader to use this command!");
    public static final ConfigKey<String> BADGE_NOT_FOUND = StringKey.of("commands.error.badge-not-present",
            "{{bu3_error}} {{player}} does not have that badge!");

    private static Map<String, ConfigKey<?>> KEYS = null;

	@Override
	public Map<String, ConfigKey<?>> getAllKeys() {
		if(KEYS == null) {
			Map<String, ConfigKey<?>> keys = new LinkedHashMap<>();

			try {
				Field[] values = MsgConfigKeys.class.getFields();
				for(Field f : values) {
					if(!Modifier.isStatic(f.getModifiers()))
						continue;

					Object val = f.get(null);
					if(val instanceof ConfigKey<?>)
						keys.put(f.getName(), (ConfigKey<?>) val);
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			KEYS = ImmutableMap.copyOf(keys);
		}

		return KEYS;
	}
}
