package gg.psyduck.bidoofunleashed.api.pixelmon.custom;

import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.commands.annotations.Permission;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import com.pixelmonmod.pixelmon.api.events.spawning.SpawnEvent;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.entities.pokemon.SpawnActionPokemon;
import com.pixelmonmod.pixelmon.config.PixelmonEntityList;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.BaseStats;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import com.pixelmonmod.pixelmon.enums.forms.EnumForms;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import lombok.AllArgsConstructor;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Customs {

	private static final Random rng = new Random();
	private static List<CustomSpec> specs = new ArrayList<>();

	private static final File base = new File(BidoofUnleashed.getInstance().getConfigDir().getParent().toFile(), "testing/specs/");

	/** Dummy method used to simply instantiate the class */
	public static void readAndInit() throws Exception {
		// Read in custom specs, and add to the set of base stats
		if(!base.exists()) {
			base.mkdirs();
		}

		for(File spec : base.listFiles()) {
			specs.add(BidoofUnleashed.prettyGson.fromJson(new FileReader(spec), CustomSpec.class));
		}

		for(CustomSpec spec : specs) {
			BaseStats stats = spec.getStats();
			stats.expand(EntityPixelmon.allBaseStats.get(spec.getSpecies()));
			EntityPixelmon.allBaseStats.get(spec.getSpecies()).forms.put(spec.getStats().form, stats);
			if(!EnumPokemon.formList.containsKey(spec.getSpecies())) {
				Form base = new Form((byte) 0, true, null);
				EnumPokemon.formList.put(spec.getSpecies(), base);
			}

			if(EntityPixelmon.getNumForms(spec.getSpecies()) < spec.getStats().form) {
				for(int i = 1; i < spec.getStats().form; i++) {
					CustomSpec x = new CustomSpec(spec.getSpecies());
					x.getStats().form = i;
					x.getStats().expand(EntityPixelmon.allBaseStats.get(spec.getSpecies()));
					EntityPixelmon.allBaseStats.get(spec.getSpecies()).forms.put(x.getStats().form, x.getStats());
					EnumPokemon.formList.put(spec.getSpecies(), new Form((byte) i, false, EnumForms.NoForm.getFormSuffix()));
				}
			}

			BidoofUnleashed.getInstance().getLogger().debug("Added form for " + spec.getSpecies().name + " for form " + spec.getStats().form);
			EnumPokemon.formList.put(spec.getSpecies(), new Form((byte) spec.getStats().form, false, spec.getTexture().toLowerCase()));
		}
	}

	@SubscribeEvent
	public void onSpawn(SpawnEvent event) {
		if(event.action instanceof SpawnActionPokemon) {
			EntityPixelmon pokemon = (EntityPixelmon) event.action.getOrCreateEntity();
			EnumPokemon species = pokemon.getSpecies();
			List<CustomSpec> validSpecs = specs.stream().filter(spec -> spec.getSpecies().equals(species)).collect(Collectors.toList());

			if(!validSpecs.isEmpty()) {
				CustomSpec selection = validSpecs.get(rng.nextInt(validSpecs.size()));
				if(selection.getRarity() != 0) {
					if(rng.nextInt(selection.getRarity()) == 0) {
						try {
							selection.create(pokemon);
							Sponge.getServer().getOnlinePlayers().forEach(player -> player.sendMessage(
									TextSerializers.FORMATTING_CODE.deserialize(selection.getSpawnMsg().replaceAll("\\{\\{pokemon}}", species.name))
							));
						} catch (Exception e) {
							BidoofUnleashed.getInstance().getLogger().error("Unable to spawn pokemon due to an error: " + e.getMessage());
						}
					}
				}
			}
		}
	}

	@Aliases({"test"})
	@Permission(admin = true)
	public static class TestSpawn extends SpongeCommand {

		private final Text SPECIES = Text.of("species");

		public TestSpawn(SpongePlugin plugin) {
			super(plugin);
		}

		@Override
		public CommandElement[] getArgs() {
			return new CommandElement[] {
					GenericArguments.enumValue(SPECIES, EnumPokemon.class)
			};
		}

		@Override
		public Text getDescription() {
			return Text.of();
		}

		@Override
		public Text getUsage() {
			return Text.of();
		}

		@Override
		public SpongeCommand[] getSubCommands() {
			return new SpongeCommand[0];
		}

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if(!(src instanceof Player)) {
				throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.SOURCE_NOT_PLAYER, null, null));
			}

			Player player = (Player) src;
			EnumPokemon species = args.<EnumPokemon>getOne(SPECIES).get();

			EntityPixelmon pokemon = (EntityPixelmon) PixelmonEntityList.createEntityByName(species.name, (World) player.getWorld());
			pokemon.setPosition(player.getPosition().getX(), player.getPosition().getY() + 1, player.getPosition().getZ());
			pokemon.setSpawnLocation(pokemon.getDefaultSpawnLocation());
			List<CustomSpec> validSpecs = specs.stream().filter(spec -> spec.getSpecies().equals(species)).collect(Collectors.toList());

			if(!validSpecs.isEmpty()) {
				CustomSpec selection = validSpecs.get(rng.nextInt(validSpecs.size()));
				try {
					selection.create(pokemon);
					Sponge.getServer().getOnlinePlayers().forEach(pl -> pl.sendMessage(
							TextSerializers.FORMATTING_CODE.deserialize(selection.getSpawnMsg().replaceAll("\\{\\{pokemon}}", species.name))
					));
				} catch (Exception e) {
					BidoofUnleashed.getInstance().getLogger().error("Unable to spawn pokemon due to an error: " + e.getMessage());
				}
			}

			player.getLocation().getExtent().spawnEntity((Entity) pokemon);

			return CommandResult.success();
		}
	}

	@Aliases({"forge"})
	@Permission(admin = true)
	public static class TestCreate extends SpongeCommand {

		private final Text SPECIES = Text.of("species");

		public TestCreate(SpongePlugin plugin) {
			super(plugin);
		}

		@Override
		public CommandElement[] getArgs() {
			return new CommandElement[] {
					GenericArguments.enumValue(SPECIES, EnumPokemon.class)
			};
		}

		@Override
		public Text getDescription() {
			return Text.of();
		}

		@Override
		public Text getUsage() {
			return Text.of();
		}

		@Override
		public SpongeCommand[] getSubCommands() {
			return new SpongeCommand[0];
		}

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			EnumPokemon species = args.<EnumPokemon>getOne(SPECIES).get();
			File target = new File(base, species.name() + ".json");
			CustomSpec spec = new CustomSpec(species);
			try {
				if(!target.exists()) {
					target.createNewFile();
				}

				FileWriter fw = new FileWriter(target);
				fw.write(BidoofUnleashed.prettyGson.toJson(spec));
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return CommandResult.success();
		}
	}

	@AllArgsConstructor
	private static class Form implements IEnumForm {

		private byte form;
		private boolean isDefault;
		private String suffix;

		@Override
		public String getFormSuffix() {
			return suffix != null && !suffix.equals("") ? "-" + suffix : "";
		}

		@Override
		public byte getForm() {
			return form;
		}

		@Override
		public boolean isDefaultForm() {
			return isDefault;
		}
	}
}
