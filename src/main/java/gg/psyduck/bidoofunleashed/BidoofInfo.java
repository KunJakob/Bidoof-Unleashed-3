package gg.psyduck.bidoofunleashed;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.logger.Logger;
import com.nickimpact.impactor.api.plugins.PluginInfo;

public class BidoofInfo implements PluginInfo {

    public static final String ID = "bu3";
    public static final String NAME = "Bidoof Unleashed 3";
    public static final String VERSION = "S7.1-0.13.4-BETA";
    public static final String DESCRIPTION = "Gyms, with a hint of Bidoof";

    @Override
    public String getID() {
      return ID;
    }

    @Override
    public String getName() {
      return NAME;
    }

    @Override
    public String getVersion() {
      return VERSION;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    public enum Dependencies {
        Pixelmon("pixelmon", "6.x.x+");

        private String dependency;
        private String version;

        private Dependencies(String dependency, String version) {
            this.dependency = dependency;
            this.version = version;
        }

        public String getDependency() {
        return dependency;
      }

        public String getVersion() {
        return version;
      }
    }

    static void startup() {
        List<String> banner = Lists.newArrayList(
                StringUtils.center("&eBidoof Unleashed", 50),
                StringUtils.center("&eRise of the Duck Edition", 50),
                StringUtils.center("&aVersion: " + VERSION, 50),
                StringUtils.center("&aAuthor: NickImpact, Smackzter & RysingDragon", 50), "",
                StringUtils.center("Now attempting to load internal components...", 50)
        );

        for (String s : banner)
            BidoofUnleashed.getInstance().getLogger().send(Logger.Prefixes.NONE, TextSerializers.FORMATTING_CODE.deserialize(s));

        BidoofUnleashed.getInstance().getLogger().send(Logger.Prefixes.NONE, Text.EMPTY);
    }

    static boolean dependencyCheck() {
        boolean valid = true;

        for (Dependencies dependency : Dependencies.values()) {
            if (!Sponge.getPluginManager().isLoaded(dependency.getDependency())) {
                BidoofUnleashed.getInstance().getLogger()
                        .error(Text.of(TextColors.DARK_RED, "==== Missing Dependency ===="));
                BidoofUnleashed.getInstance().getLogger().error(
                        Text.of(TextColors.DARK_RED, "  Dependency: ", TextColors.RED, dependency.name()));
                BidoofUnleashed.getInstance().getLogger().error(
                        Text.of(TextColors.DARK_RED, "  Version: ", TextColors.RED, dependency.getVersion()));

                valid = false;
            }
        }
        return valid;
    }
}
