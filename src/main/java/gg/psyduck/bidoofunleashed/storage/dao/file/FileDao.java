package gg.psyduck.bidoofunleashed.storage.dao.file;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.BU3BattleBase;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
import gg.psyduck.bidoofunleashed.config.ConfigKeys;
import gg.psyduck.bidoofunleashed.e4.E4Stage;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.storage.dao.AbstractBU3Dao;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.*;

public class FileDao extends AbstractBU3Dao {

	private static final File BASE_PATH_PLAYERS = new File("bidoof-unleashed-3/json/players/");
	private static final File BASE_PATH_GYMS = new File("bidoof-unleashed-3/json/battles/");

	private static final File BACKUPS = new File("bidoof-unleashed-3/backups/");

	public FileDao(SpongePlugin plugin) {
		super(plugin, "Flatfile");
	}

	@Override
	public void init() throws Exception {
		if(!BASE_PATH_PLAYERS.exists()) {
			BASE_PATH_PLAYERS.mkdirs();
		}

		if(!BASE_PATH_GYMS.exists()) {
			BASE_PATH_GYMS.mkdirs();
		}
	}

	@Override
	public void shutdown() throws Exception {}

	@Override
	public void addPlayerData(PlayerData data) throws Exception {
		this.addOrUpdatePlayerData(data);
	}

	@Override
	public void updatePlayerData(PlayerData data) throws Exception {
		this.addOrUpdatePlayerData(data);
	}

	private void addOrUpdatePlayerData(PlayerData data) throws Exception {
		File target = new File(BASE_PATH_PLAYERS, data.getUuid().toString().substring(0, 2) + "/" + data.getUuid().toString() + ".json");
		if(!target.exists()) {
			target.getParentFile().mkdirs();
			target.createNewFile();
		}

		FileWriter writer = new FileWriter(target);
		writer.write(BidoofUnleashed.prettyGson.toJson(data));
		writer.close();
	}

	@Override
	public Optional<PlayerData> getPlayerData(UUID uuid) throws Exception {
		File target = new File(BASE_PATH_PLAYERS, uuid.toString().substring(0, 2) + "/" + uuid.toString() + ".json");
		if(!target.exists()) {
			return Optional.empty();
		}

		return Optional.of(BidoofUnleashed.prettyGson.fromJson(new FileReader(target), PlayerData.class));
	}

	@Override
	public void addGym(Gym gym) throws Exception {
		this.addOrUpdateGym(gym);
	}

	@Override
	public void updateGym(Gym gym) throws Exception {
		this.addOrUpdateGym(gym);
	}

	private void addOrUpdateGym(Gym gym) throws Exception {
		File target = new File(pathBuilder(gym), "gyms/" + gym.getName() + "/" + gym.getName() + ".json");
		if(!target.exists()) {
			target.getParentFile().mkdirs();
			target.createNewFile();
		}

		FileWriter writer = new FileWriter(target);
		writer.write(BidoofUnleashed.prettyGson.toJson(gym));
		writer.close();
	}

	@Override
	public Multimap<Category, Gym> fetchGyms() throws Exception {
		Multimap<Category, Gym> gyms = ArrayListMultimap.create();
		for (File category : Objects.requireNonNull(BASE_PATH_GYMS.listFiles())) {
			File container = new File(category, "gyms");
			container.mkdirs();
			for(File gym : Objects.requireNonNull(container.listFiles())) {
				for(File spec : Objects.requireNonNull(gym.listFiles((d, s) -> s.toLowerCase().endsWith(".json")))) {
					gyms.put(new Category(category.getName()), BidoofUnleashed.prettyGson.fromJson(new FileReader(spec), Gym.class).initialize());
				}
			}
		}

		return gyms;
	}

    @Override
    public void removeGym(Gym gym) throws Exception {
	    File target = new File(pathBuilder(gym), "gyms/" + gym.getName() + "/");
        if(target.exists()) {
        	if(BidoofUnleashed.getInstance().getConfig().get(ConfigKeys.SAVE_POOLS_ON_DELETE)) {
        		if(!BACKUPS.exists()) {
        			BACKUPS.mkdirs();
		        }

		        File init = new File(target, "initial.pool");
		        File rematch = new File(target, "rematch.pool");

		        File initN = new File(BACKUPS, "gyms/" + gym.getName() + "/inital.pool");
		        initN.getParentFile().mkdirs();
		        init.renameTo(initN);

		        File rematchN = new File(BACKUPS, "gyms/" + gym.getName() + "/rematch.pool");
		        rematchN.getParentFile().mkdirs();
		        rematch.renameTo(rematchN);
	        }

            target.delete();
            return;
        }

        throw new Exception("File does not exist, considering already removed");
    }

	@Override
	public void addE4(EliteFour e4) throws Exception {
		this.addOrUpdateE4(e4);
	}

	@Override
	public void updateE4(EliteFour e4) throws Exception {
		this.addOrUpdateE4(e4);
	}

	private void addOrUpdateE4(EliteFour e4) throws Exception {
		File target = new File(pathBuilder(e4), "e4/" + e4.getName() + "/" + e4.getName() + ".json");
		if(!target.exists()) {
			target.getParentFile().mkdirs();
			target.createNewFile();
		}

		FileWriter writer = new FileWriter(target);
		writer.write(BidoofUnleashed.prettyGson.toJson(e4));
		writer.close();
	}

	@Override
	public Map<Category, EliteFour> fetchE4() throws Exception {
		Map<Category, EliteFour> e4 = Maps.newHashMap();
		for (File category : Objects.requireNonNull(BASE_PATH_GYMS.listFiles())) {
			File container = new File(category, "e4");
			container.mkdirs();
			for (File elite : Objects.requireNonNull(container.listFiles())) {
				for (File spec : Objects.requireNonNull(elite.listFiles((d, s) -> s.toLowerCase().endsWith(".json")))) {
					EliteFour instance = BidoofUnleashed.prettyGson.fromJson(new FileReader(spec), EliteFour.class).initialize();
					for (E4Stage stage : instance.getStages()) {
						stage.setE4(instance);
					}

					instance.getChampion().setE4(instance);
					e4.put(new Category(category.getName()), instance);
				}
			}
		}

		return e4;
	}

	@Override
	public void removeE4(EliteFour e4) throws Exception {
		File target = new File(pathBuilder(e4), "e4/" + e4.getName() + "/");
		if(target.exists()) {
			if(BidoofUnleashed.getInstance().getConfig().get(ConfigKeys.SAVE_POOLS_ON_DELETE)) {
				if(!BACKUPS.exists()) {
					BACKUPS.mkdirs();
				}

				File init = new File(target, "initial.pool");
				File rematch = new File(target, "rematch.pool");

				File initN = new File(BACKUPS, "e4/" + e4.getName() + "/inital.pool");
				initN.getParentFile().mkdirs();
				init.renameTo(initN);

				File rematchN = new File(BACKUPS, "e4/" + e4.getName() + "/rematch.pool");
				rematchN.getParentFile().mkdirs();
				rematch.renameTo(rematchN);
			}

			target.delete();
			return;
		}

		throw new Exception("File does not exist, considering already removed");
	}

	public static File pathBuilder(BU3BattleBase base) {
		return new File(BASE_PATH_GYMS, base.getCategory().getId() + "/");
	}
}
