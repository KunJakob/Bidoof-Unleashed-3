package gg.psyduck.bidoofunleashed.config;

import com.google.common.collect.ImmutableMap;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import com.nickimpact.impactor.api.configuration.IConfigKeys;
import com.nickimpact.impactor.api.configuration.keys.AbstractKey;
import com.nickimpact.impactor.api.configuration.keys.EnduringKey;
import com.nickimpact.impactor.api.configuration.keys.IntegerKey;
import com.nickimpact.impactor.api.configuration.keys.StringKey;
import com.nickimpact.impactor.api.storage.StorageCredentials;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigKeys implements IConfigKeys {

	public static final ConfigKey<String> STORAGE_METHOD = StringKey.of("storage.storage-method", "h2");
	public static final ConfigKey<String> SQL_TABLE_PREFIX = StringKey.of("storage.data.sql-table-prefix", "bu3_");
	public static final ConfigKey<StorageCredentials> DATABASE_VALUES = EnduringKey.wrap(AbstractKey.of(c -> new StorageCredentials(
			c.getString("storage.data.connection-info.address", "localhost"),
			c.getString("storage.data.connection-info.database", "database"),
			c.getString("storage.data.connection-info.username", "username"),
			c.getString("storage.data.connection-info.password", "password")
	)));

	public static final ConfigKey<Integer> TELEPORT_WAIT = IntegerKey.of("gyms.teleport.wait", 10);
	public static final ConfigKey<Integer> DEFAULT_COOLDOWN = IntegerKey.of("gyms.defaults.cooldown", 60);

	private static Map<String, ConfigKey<?>> KEYS = null;

	@Override
	public Map<String, ConfigKey<?>> getAllKeys() {
		if(KEYS == null) {
			Map<String, ConfigKey<?>> keys = new LinkedHashMap<>();

			try {
				Field[] values = ConfigKeys.class.getFields();
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
