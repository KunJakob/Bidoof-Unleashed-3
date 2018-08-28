package gg.psyduck.bidoofunleashed.storage.dao.sql;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import com.nickimpact.impactor.api.storage.connections.sql.AbstractSQLConnectionFactory;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.storage.dao.AbstractBU3Dao;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class SqlDao extends AbstractBU3Dao {

	private static final String ADD_OR_UPDATE_PLAYERDATA = "INSERT INTO `{prefix}player_data` (uuid, data) VALUES ('%1$s', '%2$s') ON DUPLICATE KEY UPDATE data='%2$s'";
	private static final String ADD_OR_UPDATE_GYM = "INSERT INTO `{prefix}gyms` (name, data) VALUES ('%1$s', '%2$s') ON DUPLICATE KEY UPDATE data='%2$s'";
	private static final String GET_PLAYERDATA = "SELECT * FROM `{prefix}player_data` WHERE UUID='%s'";
	private static final String GET_GYMS = "SELECT * FROM `{prefix}gyms` WHERE NAME='%s'";
	private static final String DELETE_GYMS = "DELETE FROM `{prefix}gyms` WHERE NAME=%s";

	@Getter private final AbstractSQLConnectionFactory provider;
	@Getter private final Function<String, String> prefix;

	public SqlDao(SpongePlugin plugin, AbstractSQLConnectionFactory provider, String prefix) {
		super(plugin, provider.getName());
		this.provider = provider;
		this.prefix = s -> s.replace("{prefix}", prefix);
	}

	private boolean tableExists(String table) throws SQLException {
		try(Connection connection = provider.getConnection()) {
			try (ResultSet rs = connection.getMetaData().getTables(null, null, "%", null)) {
				while(rs.next()) {
					if(rs.getString(3).equalsIgnoreCase(table)) {
						return true;
					}
				}
				return false;
			}
		}
	}

	@Override
	public void init() throws Exception {
		provider.init();

		// Init tables
		if(!tableExists(prefix.apply("{prefix}player_data"))) {
			String schemaFileName = "gg/psyduck/bidoofunleashed/schema/" + provider.getName().toLowerCase() + ".sql";
			try (InputStream is = ((BidoofUnleashed)plugin).getResourceStream(schemaFileName)) {
				if(is == null) {
					throw new Exception("Couldn't locate schema file for " + provider.getName());
				}

				try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
					try (Connection connection = provider.getConnection()) {
						try (Statement s = connection.createStatement()) {
							StringBuilder sb = new StringBuilder();
							String line;
							while ((line = reader.readLine()) != null) {
								if (line.startsWith("--") || line.startsWith("#")) continue;

								sb.append(line);

								// check for end of declaration
								if (line.endsWith(";")) {
									sb.deleteCharAt(sb.length() - 1);

									String result = prefix.apply(sb.toString().trim());
									if (!result.isEmpty())
										s.addBatch(result);

									// reset
									sb = new StringBuilder();
								}
							}
							s.executeBatch();
						}
					}
				}
			}
		}
	}

	@Override
	public void shutdown() throws Exception {}

	@Override
	public void addOrUpdatePlayerData(PlayerData data) throws Exception {
		try {
			Connection connection = provider.getConnection();
			String stmt = String.format(prefix.apply(ADD_OR_UPDATE_PLAYERDATA), data.getUuid(), BidoofUnleashed.prettyGson.toJson(data));

			PreparedStatement ps = connection.prepareStatement(stmt);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Optional<PlayerData> getPlayerData(UUID uuid) throws Exception {
		PlayerData data = null;

		try {
			Connection connection = provider.getConnection();
			String stmt = String.format(prefix.apply(GET_PLAYERDATA), uuid);
			PreparedStatement query = connection.prepareStatement(stmt);
			ResultSet results = query.executeQuery();
			if(results.next()) {
				data = BidoofUnleashed.prettyGson.fromJson(results.getString("data"), PlayerData.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Optional.ofNullable(data);
	}

	@Override
	public void addOrUpdateGym(Gym gym) throws Exception {
		try {
			Connection connection = provider.getConnection();
			String stmt = String.format(prefix.apply(ADD_OR_UPDATE_GYM), gym.getName(), BidoofUnleashed.prettyGson.toJson(gym));

			PreparedStatement ps = connection.prepareStatement(stmt);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Gym> fetchGyms() throws Exception {
		List<Gym> gyms = Lists.newArrayList();

		try {
			Connection connection = provider.getConnection();
			String stmt = prefix.apply(GET_GYMS);
			PreparedStatement query = connection.prepareStatement(stmt);
			ResultSet results = query.executeQuery();
			while(results.next()) {
				gyms.add(BidoofUnleashed.prettyGson.fromJson(results.getString("data"), Gym.class).initialize());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return gyms;
	}

    @Override
    public void removeGym(Gym gym) throws Exception {
        try {
            Connection connection = provider.getConnection();
            String stmt = String.format(prefix.apply(DELETE_GYMS), gym.getName(), BidoofUnleashed.prettyGson.toJson(gym));

            PreparedStatement ps = connection.prepareStatement(stmt);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
