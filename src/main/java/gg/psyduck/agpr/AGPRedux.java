package gg.psyduck.agpr;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import gg.psyduck.agpr.config.ConfigKeys;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
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
import com.nickimpact.impactor.api.plugins.ConfigurableSpongePlugin;
import com.nickimpact.impactor.api.plugins.PluginInfo;
import com.nickimpact.impactor.api.storage.Storage;
import com.nickimpact.impactor.logging.ConsoleLogger;
import com.nickimpact.impactor.logging.SpongeLogger;

import lombok.Getter;

@Getter
@Plugin(id = AGPReduxInfo.ID, name = AGPReduxInfo.NAME, version = AGPReduxInfo.VERSION,
    description = AGPReduxInfo.DESCRIPTION, dependencies = @Dependency(id = CoreInfo.ID))
public class AGPRedux extends ConfigurableSpongePlugin {

    @Getter
    private static AGPRedux instance;

    @Inject
    private PluginContainer pluginContainer;

    private Logger logger;
    @Inject
    private org.slf4j.Logger fallback;

    /** The pathing to the config directory for AGPRedux */
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    public static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

    private ConfigBase config;
    private ConfigBase msgConfig;

    private Storage storage;

    /**
     * Used to keep track of any potential start up issues which will prevent the plugin from
     * initializing properly
     */
    private Throwable error = null;

    @Listener
    public void onInit(GameInitializationEvent e) {
        instance = this;
        this.logger = new ConsoleLogger(this, new SpongeLogger(this, fallback));
        this.connect();
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
        error.add(Text.of(TextColors.RED, "- AGP REDUX FAILED TO LOAD -"));
        error.add(Text.of(TextColors.RED, "----------------------------"));
        error.add(Text.EMPTY);
        error.add(Text.of(TextColors.RED,
            "AGP Redux encountered an error which prevented startup to succeed. All commands, listeners, and tasks have been halted..."));
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
                    "It appears that there is an error with the AGP Redux storage provider! The error is: "));
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
    public ConfigBase getConfig() {
        // TODO Auto-generated method stub
        return null;
    }



    @Override
    public Path getConfigDir() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void doConnect() {
        AGPReduxInfo.startup();

        try {
            this.logger.info(Text.of(TextColors.GRAY, "Now entering the init phase..."));
            this.logger.info(Text.of(TextColors.GRAY, "Loading configuration..."));
            this.config = new AbstractConfig(this, new AbstractConfigAdapter(this), new ConfigKeys(),"agpredux.conf");
            this.config.init();

            connect();
        } catch (Exception exc) {
            this.error = exc;
            disable();
            exc.printStackTrace();
        }
    }

    @Override
    public void doDisconnect() {
        // TODO Auto-generated method stub

    }

    @Override
    public Logger getLogger() {
      return this.logger;
    }

    @Override
    public PluginInfo getPluginInfo() {
      return new AGPReduxInfo();
    }

}
