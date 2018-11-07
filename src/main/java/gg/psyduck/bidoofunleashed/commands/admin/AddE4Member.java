package gg.psyduck.bidoofunleashed.commands.admin;

import com.flowpowered.math.vector.Vector3d;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.commands.annotations.Aliases;
import com.nickimpact.impactor.api.commands.annotations.Permission;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.npcs.registry.NPCRegistryTrainers;
import com.pixelmonmod.pixelmon.enums.EnumEncounterMode;
import com.pixelmonmod.pixelmon.enums.EnumTrainerAI;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.commands.arguments.E4Arg;
import gg.psyduck.bidoofunleashed.e4.E4Stage;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import net.minecraft.world.World;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

@Aliases({"ae4m", "adde4member"})
@Permission(admin = true)
public class AddE4Member extends SpongeCommand {

	private static final Text E4 = Text.of("e4");
	private static final Text STAGE = Text.of("stage");
	private static final Text LEADER = Text.of("leader");

	public AddE4Member(SpongePlugin plugin) {
		super(plugin);
	}

	@Override
	public CommandElement[] getArgs() {
		return new CommandElement[] {
				new E4Arg(E4),
				GenericArguments.string(Text.of(STAGE)),
				GenericArguments.string(Text.of(LEADER))
		};
	}

	@Override
	public Text getDescription() {
		return Text.of("Adds a member to the elite four instance for handling battles");
	}

	@Override
	public Text getUsage() {
		return Text.of("/bu3 ae4m (e4) (stage #/\"champion\") (player/\"npc\"");
	}

	@Override
	public SpongeCommand[] getSubCommands() {
		return new SpongeCommand[0];
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		EliteFour e4 = args.<EliteFour>getOne(E4).get();
		String stage = args.<String>getOne(STAGE).get();
		String leader = args.<String>getOne(LEADER).get();

		if((src instanceof ConsoleSource || src instanceof CommandBlockSource) && leader.equalsIgnoreCase("npc")) {
			throw new CommandException(Text.of("Only players can add in NPC leaders due to location requirements..."));
		}

		if(stage.equalsIgnoreCase("champion")) {
			if(leader.equalsIgnoreCase("npc")) {
				NPCTrainer npc = new NPCTrainer((World) ((Player) src).getWorld());
				npc.init(NPCRegistryTrainers.Steve);
				npc.setPosition(
						((Player) src).getLocation().getPosition().getFloorX() + 0.5,
						((Player) src).getLocation().getPosition().getFloorY(),
						((Player) src).getLocation().getPosition().getFloorZ() + 0.5
				);
				npc.setAIMode(EnumTrainerAI.StandStill);
				npc.ignoreDespawnCounter = true;
				npc.initAI();
				npc.setStartRotationYaw(180);
				npc.setEncounterMode(EnumEncounterMode.Unlimited);
				Pixelmon.proxy.spawnEntitySafely(npc, (World) ((Player) src).getWorld());
				e4.getChampion().addNPCLeader(npc);
			} else {
				User p = findPlayerIfExists(leader);
				e4.getChampion().addLeader(p.getUniqueId());
			}
		} else {
			int s = Integer.parseInt(stage);
			E4Stage st = e4.getStages().get(s - 1);
			if(leader.equalsIgnoreCase("npc")) {
				NPCTrainer npc = new NPCTrainer((World) ((Player) src).getWorld());
				npc.init(NPCRegistryTrainers.Steve);
				npc.setPosition(
						((Player) src).getLocation().getPosition().getFloorX() + 0.5,
						((Player) src).getLocation().getPosition().getFloorY(),
						((Player) src).getLocation().getPosition().getFloorZ() + 0.5
				);
				npc.setAIMode(EnumTrainerAI.StandStill);
				npc.ignoreDespawnCounter = true;
				npc.initAI();
				npc.setStartRotationYaw(180);
				npc.setEncounterMode(EnumEncounterMode.Unlimited);
				Pixelmon.proxy.spawnEntitySafely(npc, (World) ((Player) src).getWorld());
				st.addNPCLeader(npc);
			} else {
				User p = findPlayerIfExists(leader);
				st.addLeader(p.getUniqueId());
			}
		}

		BidoofUnleashed.getInstance().getStorage().updateE4(e4);
		return CommandResult.success();
	}

	private User findPlayerIfExists(String name) throws CommandException {
		UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
		return uss.get(name).orElseThrow(() -> new CommandException(Text.of("Target player doesn't exist...")));
	}
}
