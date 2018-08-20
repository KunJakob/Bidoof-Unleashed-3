package gg.psyduck.bidoofunleashed.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.comm.packetHandlers.itemDrops.ItemDropMode;
import com.pixelmonmod.pixelmon.comm.packetHandlers.itemDrops.ItemDropPacket;
import com.pixelmonmod.pixelmon.comm.packetHandlers.npc.SetNPCData;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemQuery;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemQueryList;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DroppedItem;
import com.pixelmonmod.pixelmon.enums.EnumGui;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;

public class PixelmonUtilities {

	public static List<String> safePokes = new ArrayList<>();
	private static Predicate<EntityPixelmon> invalidPokes = poke -> !(poke.getIsShiny()
			|| poke.hasOwner() || poke.isInRanchBlock || poke.isBeingRidden() || poke.isBossPokemon()
			|| poke.battleController != null || EnumPokemon.legendaries.contains(poke.getName())
			|| safePokes.contains(poke.getPokemonName()));

	public static void openDropInventory(ArrayList<DroppedItem> items, String message, Player p) {
		EntityPlayerMP mp = (EntityPlayerMP) p;

		DropItemQuery query = new DropItemQuery(mp.getPositionVector(), p.getUniqueId(), items);
		DropItemQueryList.queryList.add(query);
		ItemDropPacket packet =
				new ItemDropPacket(ItemDropMode.Other, new TextComponentTranslation(message), items);
		Pixelmon.network.sendTo(packet, mp);
	}

	public static void sendChatBoxes(Player p, String name, String... strings) {
		ArrayList<String> stringlist = new ArrayList<>();
		for (String s : strings) {
			stringlist.add(s);
		}
		sendChatBoxes(p, name, stringlist);
	}

	public static void sendChatBoxes(Player p, String name, List<String> strings) {
		EntityPlayerMP mp = (EntityPlayerMP) p;
		Pixelmon.network.sendTo(new SetNPCData(name, (ArrayList<String>) strings), mp);
		mp.openGui(Pixelmon.instance, EnumGui.NPCChat.getIndex(), mp.getEntityWorld(), 0, 0, 0);
	}

	public static int killPokes(World world) {
		final AtomicInteger killCount = new AtomicInteger();

		Stream<EntityPixelmon> entStream =
				world.getEntities().stream().filter(ent -> ent instanceof EntityPixelmon)
						.map(ent -> (EntityPixelmon) ent).filter(invalidPokes);

		entStream.forEach(poke -> {
			poke.setDead();
			killCount.getAndIncrement();
		});

		return killCount.intValue();
	}

}
