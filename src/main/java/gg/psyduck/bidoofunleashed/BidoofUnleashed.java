package gg.psyduck.bidoofunleashed;

import java.io.*;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.flowpowered.math.vector.Vector3d;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import com.nickimpact.impactor.api.rewards.Reward;
import com.nickimpact.impactor.api.services.plan.PlanData;
import com.nickimpact.impactor.api.storage.StorageType;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.api.gyms.json.RequirementAdapter;
import gg.psyduck.bidoofunleashed.commands.BU3Command;
import gg.psyduck.bidoofunleashed.config.ConfigKeys;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.data.DataRegistry;
import gg.psyduck.bidoofunleashed.gyms.Badge;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.impl.EvolutionRequirement;
import gg.psyduck.bidoofunleashed.impl.GymRequirement;
import gg.psyduck.bidoofunleashed.impl.LevelRequirement;
import gg.psyduck.bidoofunleashed.internal.TextParsingUtils;
import gg.psyduck.bidoofunleashed.rewards.ItemReward;
import gg.psyduck.bidoofunleashed.rewards.money.MoneyReward;
import gg.psyduck.bidoofunleashed.rewards.PokemonReward;
import gg.psyduck.bidoofunleashed.rewards.json.RewardAdapter;
import gg.psyduck.bidoofunleashed.storage.StorageFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.AsynchronousExecutor;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.nickimpact.impactor.CoreInfo;
import com.nickimpact.impactor.api.configuration.AbstractConfig;
import com.nickimpact.impactor.api.configuration.AbstractConfigAdapter;
import com.nickimpact.impactor.api.configuration.ConfigBase;
import com.nickimpact.impactor.api.logger.Logger;
import com.nickimpact.impactor.api.plugins.PluginInfo;
import com.nickimpact.impactor.api.storage.Storage;
import com.nickimpact.impactor.logging.ConsoleLogger;
import com.nickimpact.impactor.logging.SpongeLogger;

import lombok.Getter;

@Getter
@Plugin(id = BidoofInfo.ID, name = BidoofInfo.NAME, version = BidoofInfo.VERSION,
    description = BidoofInfo.DESCRIPTION, dependencies = {@Dependency(id = CoreInfo.ID), @Dependency(id = "nucleus")})
public class BidoofUnleashed extends SpongePlugin {

    @Getter
    private static BidoofUnleashed instance;

    @Inject
    private PluginContainer pluginContainer;

    private Logger logger;
    @Inject
    private org.slf4j.Logger fallback;

    /** The pathing to the config directory for BidoofUnleashed */
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

	@Inject @AsynchronousExecutor
	private SpongeExecutorService asyncExecutorService;

    public static Gson prettyGson;

    private ConfigBase config;
    private ConfigBase msgConfig;

    private Storage storage;

    /**
     * Used to keep track of any potential start up issues which will prevent the plugin from
     * initializing properly
     */
    private Throwable error = null;

	private EconomyService economy;

	private TextParsingUtils textParsingUtils = new TextParsingUtils();

	private DataRegistry dataRegistry = new DataRegistry();

	@Listener
    public void onInit(GameInitializationEvent e) {
        instance = this;
        prettyGson = new GsonBuilder()
				.registerTypeAdapter(Requirement.class, new RequirementAdapter(this))
		        .registerTypeAdapter(Reward.class, new RewardAdapter(this))
				.setPrettyPrinting()
				.create();
        this.logger = new ConsoleLogger(this, new SpongeLogger(this, fallback));
	    BidoofInfo.startup();

	    try {
		    RequirementAdapter.requirementRegistry.register(EvolutionRequirement.class);
		    RequirementAdapter.requirementRegistry.register(GymRequirement.class);
		    RequirementAdapter.requirementRegistry.register(LevelRequirement.class);

		    RewardAdapter.rewardRegistry.register(PokemonReward.class);
		    RewardAdapter.rewardRegistry.register(ItemReward.class);
		    RewardAdapter.rewardRegistry.register(MoneyReward.class);
	    } catch (Exception ignored) {}

	    try {
		    this.logger.info(Text.of(TextColors.GRAY, "Now entering the init phase..."));
		    this.logger.info(Text.of(TextColors.GRAY, "Loading configuration..."));
		    this.config = new AbstractConfig(this, new AbstractConfigAdapter(this), new ConfigKeys(),"bidoof-unleashed.conf");
		    this.config.init();
		    this.msgConfig = new AbstractConfig(this, new AbstractConfigAdapter(this), new MsgConfigKeys(), "lang/en_us.conf");
		    this.msgConfig.init();

		    this.storage = StorageFactory.getInstance(this, StorageType.H2);
	    } catch (Exception exc) {
		    this.error = exc;
		    disable();
		    exc.printStackTrace();
	    }

	    //commands
        new BU3Command(this).register(this);
    }

	@Listener
	public void registerServices(ChangeServiceProviderEvent e){
		if(e.getService().equals(EconomyService.class)) {
			this.economy = (EconomyService) e.getNewProviderRegistration().getProvider();
		}
	}

    private void disable() {
        // Disable everything, just in case. Thanks to pie-flavor:
        // https://forums.spongepowered.org/t/disable-plugin-disable-itself/15831/8
        Sponge.getEventManager().unregisterPluginListeners(this);
        Sponge.getCommandManager().getOwnedBy(this).forEach(Sponge.getCommandManager()::removeMapping);
        Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);

        // Re-register this to warn people about the error.
        Sponge.getEventManager().registerListener(this, GameStartedServerEvent.class, e -> errorOnStartup());
    }

    private void errorOnStartup() {
      Sponge.getServer().getConsole().sendMessages(getErrorMessage());
    }

    private List<Text> getErrorMessage() {
        List<Text> error = Lists.newArrayList();
        error.add(Text.of(TextColors.RED, "----------------------------"));
        error.add(Text.of(TextColors.RED, "-    BU3 FAILED TO LOAD    -"));
        error.add(Text.of(TextColors.RED, "----------------------------"));
        error.add(Text.EMPTY);
        error.add(Text.of(TextColors.RED,
            "Bidoof Unleashed 3 encountered an error which prevented startup to succeed. All commands, listeners, and tasks have been halted..."));
        error.add(Text.of(TextColors.RED, "----------------------------"));
        if (this.error != null) {
            if (this.error instanceof IOException) {
                error.add(Text.of(TextColors.RED,
                    "It appears that there is an error in your configuration file! The error is: "));
                error.add(Text.of(TextColors.RED, this.error.getMessage()));
                error.add(Text.of(TextColors.RED, "Please correct this and restart your server."));
                error.add(Text.of(TextColors.YELLOW, "----------------------------"));
            } else if (this.error instanceof SQLException) {
                error.add(Text.of(TextColors.RED,
                    "It appears that there is an error with the Bidoof Unleashed 3 storage provider! The error is: "));
                error.add(Text.of(TextColors.RED, this.error.getMessage()));
                error.add(Text.of(TextColors.RED, "Please correct this and restart your server."));
                error.add(Text.of(TextColors.YELLOW, "----------------------------"));
            }

            error.add(Text.of(TextColors.YELLOW, "(The error that was thrown is shown below)"));

            try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
                this.error.printStackTrace(pw);
                pw.flush();
                String[] stackTrace = sw.toString().split("(\r)?\n");
                for (String s : stackTrace) {
                  error.add(Text.of(TextColors.YELLOW, s));
                }
            } catch (IOException e) {
                this.error.printStackTrace();
            }
        }

        return error;
    }

    @Override
    public Logger getLogger() {
      return this.logger;
    }

	@Override
	public Optional<PlanData> getPlanData() {
		return Optional.empty();
	}

	@Override
	public List<ConfigBase> getConfigs() {
		return Lists.newArrayList(this.config, this.msgConfig);
	}

	@Override
	public List<SpongeCommand> getCommands() {
		return Lists.newArrayList();
	}

	@Override
	public List<Object> getListeners() {
		return Lists.newArrayList();
	}

	@Override
	public void onDisconnect() {}

	@Override
	public void onReload() {}

	@Override
    public PluginInfo getPluginInfo() {
      return new BidoofInfo();
    }

	public InputStream getResourceStream(String path) {
		return getClass().getClassLoader().getResourceAsStream(path);
	}

	public File getDataDirectory() {
		File root = configDir.toFile().getParentFile().getParentFile();
		File dir = new File(root, "bidoof-unleashed-3");
		if(!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
}
