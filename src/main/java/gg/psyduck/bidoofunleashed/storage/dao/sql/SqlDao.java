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

	private static final String ADD_PLAYERDATA = "INSERT INTO `{prefix}player_data` (uuid, content) VALUES ('%s', '%s')";
	private static final String UPDATE_PLAYERDATA = "UPDATE `{prefix}player_data` SET content = '%s' WHERE uuid = '%s'";
	private static final String ADD_GYM = "INSERT INTO `{prefix}gyms` (uuid, content) VALUES ('%s', '%s')";
	private static final String UPDATE_GYM = "UPDATE `{prefix}gyms` SET content = '%s' WHERE uuid = '%s'";
	private static final String GET_PLAYERDATA = "SELECT * FROM `{prefix}player_data` WHERE uuid='%s'";
	private static final String GET_GYMS = "SELECT * FROM `{prefix}gyms`";
	private static final String DELETE_GYMS = "DELETE FROM `{prefix}gyms` WHERE uuid='%s'";

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
	public void addPlayerData(PlayerData data) throws Exception {
		Connection connection = provider.getConnection();
		String stmt = String.format(prefix.apply(ADD_PLAYERDATA), data.getUuid(), BidoofUnleashed.prettyGson.toJson(data));

		PreparedStatement ps = connection.prepareStatement(stmt);
		ps.executeUpdate();
	}

	@Override
	public void updatePlayerData(PlayerData data) throws Exception {
		Connection connection = provider.getConnection();
		String stmt = String.format(prefix.apply(UPDATE_PLAYERDATA), BidoofUnleashed.prettyGson.toJson(data), data.getUuid());

		PreparedStatement ps = connection.prepareStatement(stmt);
		ps.executeUpdate();
	}

	@Override
	public Optional<PlayerData> getPlayerData(UUID uuid) throws Exception {
		PlayerData data = null;

		Connection connection = provider.getConnection();
		String stmt = String.format(prefix.apply(GET_PLAYERDATA), uuid);
		PreparedStatement query = connection.prepareStatement(stmt);
		ResultSet results = query.executeQuery();
		if(results.next()) {
			data = BidoofUnleashed.prettyGson.fromJson(results.getString("content"), PlayerData.class);
		}


		return Optional.ofNullable(data);
	}

	@Override
	public void addGym(Gym gym) throws Exception {
		Connection connection = provider.getConnection();
		String stmt = String.format(prefix.apply(ADD_GYM), gym.getUuid(), BidoofUnleashed.prettyGson.toJson(gym));

		PreparedStatement ps = connection.prepareStatement(stmt);
		ps.executeUpdate();
	}

	@Override
	public void updateGym(Gym gym) throws Exception {
		Connection connection = provider.getConnection();
		String stmt = String.format(prefix.apply(UPDATE_GYM), BidoofUnleashed.prettyGson.toJson(gym), gym.getUuid());

		PreparedStatement ps = connection.prepareStatement(stmt);
		ps.executeUpdate();
	}

	@Override
	public List<Gym> fetchGyms() throws Exception {
		List<Gym> gyms = Lists.newArrayList();

		Connection connection = provider.getConnection();
		String stmt = prefix.apply(GET_GYMS);
		PreparedStatement query = connection.prepareStatement(stmt);
		ResultSet results = query.executeQuery();
		while(results.next()) {
			gyms.add(BidoofUnleashed.prettyGson.fromJson(results.getString("content"), Gym.class).initialize());
		}

		return gyms;
	}

    @Override
    public void removeGym(Gym gym) throws Exception {
        Connection connection = provider.getConnection();
        String stmt = String.format(prefix.apply(DELETE_GYMS), gym.getName());

        PreparedStatement ps = connection.prepareStatement(stmt);
        ps.executeUpdate();
    }
}
