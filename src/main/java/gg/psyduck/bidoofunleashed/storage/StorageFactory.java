package gg.psyduck.bidoofunleashed.storage;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import com.nickimpact.impactor.api.storage.Storage;
import com.nickimpact.impactor.api.storage.StorageType;
import com.nickimpact.impactor.api.storage.connections.sql.file.H2ConnectionFactory;
import com.nickimpact.impactor.api.storage.connections.sql.hikari.MySQLConnectionFactory;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.config.ConfigKeys;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.storage.dao.AbstractBU3Dao;
import gg.psyduck.bidoofunleashed.storage.dao.file.FileDao;
import gg.psyduck.bidoofunleashed.storage.dao.sql.SqlDao;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class StorageFactory {

	public static StorageType getStorageType(SpongePlugin plugin, StorageType defaultType) {
		String type = plugin.getConfig().get(ConfigKeys.STORAGE_METHOD);
		StorageType st = StorageType.parse(type);

		if(st == null) {
			st = defaultType;
		}

		return st;
	}

	public static BU3Storage getInstance(SpongePlugin plugin, StorageType defaultMethod) throws Exception {
		String method = plugin.getConfig().get(ConfigKeys.STORAGE_METHOD);
		StorageType type = StorageType.parse(method);
		if (type == null) {
			type = defaultMethod;
		}

		final StorageType declared = type;

		Map<String, Function<CommandSource, Optional<Text>>> tokens = Maps.newHashMap();
		tokens.put("storage_type", src -> Optional.of(Text.of(declared.getName())));
		plugin.getLogger().info(MessageUtils.fetchAndParseMsg(null, MsgConfigKeys.STARTUP_STORAGE_PROVIDER, tokens, null));
		BU3Storage storage = makeInstance(declared, plugin);
		storage.init();
		return storage;
	}

	private static BU3Storage makeInstance(StorageType type, SpongePlugin plugin) {
		return AbstractBU3Storage.create(makeDao(type, plugin));
	}

	private static AbstractBU3Dao makeDao(StorageType type, SpongePlugin plugin) {
		switch(type) {
			case MYSQL:
				return new SqlDao(
						plugin,
						new MySQLConnectionFactory(plugin.getConfig().get(ConfigKeys.DATABASE_VALUES)),
						plugin.getConfig().get(ConfigKeys.SQL_TABLE_PREFIX)
				);
			case H2:
				return new SqlDao(
						plugin,
						new H2ConnectionFactory(new File(((BidoofUnleashed) plugin).getDataDirectory(), "sql/bu3-h2")),
						plugin.getConfig().get(ConfigKeys.SQL_TABLE_PREFIX)
				);
			case JSON:
				return new FileDao(plugin);
			default:
				//return new JsonDao(plugin);
				return new SqlDao(
						plugin,
						new H2ConnectionFactory(new File(((BidoofUnleashed) plugin).getDataDirectory(), "bu3-h2")),
						plugin.getConfig().get(ConfigKeys.SQL_TABLE_PREFIX)
				);
		}
	}
}
