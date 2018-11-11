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
import gg.psyduck.bidoofunleashed.commands.arguments.GymArg;
import gg.psyduck.bidoofunleashed.config.MsgConfigKeys;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.utils.MessageUtils;
import net.minecraft.world.World;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.util.Optional;

@Aliases({"addgymleader", "agl", "addleader", "al"})
@Permission(admin = true)
public class AddGymLeaderCommand extends SpongeCommand {

    public AddGymLeaderCommand(SpongePlugin plugin) {
        super(plugin);
    }

    @Override
    public CommandElement[] getArgs() {
        return new CommandElement[] {
        		new GymArg(Text.of("gym-name")),
                GenericArguments.string(Text.of("leader")),
		        GenericArguments.optional(GenericArguments.string(Text.of("world"))),
		        GenericArguments.optional(GenericArguments.vector3d(Text.of("location")))
        };
    }

    @Override
    public Text getDescription() {
        return Text.of("Add a leader to a gym");
    }

    @Override
    public Text getUsage() {
        return Text.of("/bu3 addgymleader <gym-name> <player>");
    }

    @Override
    public SpongeCommand[] getSubCommands() {
        return new SpongeCommand[0];
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Gym gym = args.<Gym>getOne("gym-name").get();
        String leader = args.<String>getOne("leader").get();

        if(leader.equalsIgnoreCase("npc")) {
	        Optional<org.spongepowered.api.world.World> world = args.<String>getOne(Text.of("world")).map(w -> Sponge.getServer().getWorld(w).orElse(null));
	        Optional<Vector3d> pos = args.getOne(Text.of("location"));

	        if((!world.isPresent() || !pos.isPresent()) && !(src instanceof Player)) {
	        	throw new CommandException(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.SOURCE_NOT_PLAYER, null, null));
	        }

	        NPCTrainer npc = new NPCTrainer((World) world.orElseGet(((Player) src)::getWorld));
	        npc.init(NPCRegistryTrainers.Steve);
	        npc.setPosition(
	        		pos.map(Vector3d::getFloorX).orElseGet(() -> ((Player) src).getLocation().getPosition().getFloorX()) + 0.5,
			        pos.map(Vector3d::getFloorY).orElseGet(() -> ((Player) src).getLocation().getPosition().getFloorY()),
			        pos.map(Vector3d::getFloorZ).orElseGet(() -> ((Player) src).getLocation().getPosition().getFloorZ()) + 0.5
	        );
	        npc.setAIMode(EnumTrainerAI.StandStill);
	        npc.ignoreDespawnCounter = true;
	        npc.initAI();
	        npc.setStartRotationYaw(180);
	        npc.setEncounterMode(EnumEncounterMode.Unlimited);
	        Pixelmon.proxy.spawnEntitySafely(npc, (World) world.orElseGet(((Player) src)::getWorld));
	        gym.addNPCLeader(npc);
        } else {
        	User user = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(leader).orElseThrow(() -> new CommandException(Text.of("Unable to find a player by that name...")));
	        gym.addLeader(user.getUniqueId());
        }

        src.sendMessages(MessageUtils.fetchAndParseMsg(src, MsgConfigKeys.COMMANDS_ADD_LEADER, null, null));
        BidoofUnleashed.getInstance().getStorage().updateGym(gym);
        return CommandResult.success();
    }
}
