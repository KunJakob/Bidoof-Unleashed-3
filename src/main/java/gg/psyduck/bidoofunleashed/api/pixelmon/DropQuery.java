package gg.psyduck.bidoofunleashed.api.pixelmon;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.comm.packetHandlers.itemDrops.ItemDropMode;
import com.pixelmonmod.pixelmon.comm.packetHandlers.itemDrops.ItemDropPacket;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemQuery;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemQueryList;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DroppedItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;

public class DropQuery {

	private ArrayList<DroppedItem> items = Lists.newArrayList();
	private int id = 1;

	public DropQuery add(DroppedItem item) {
		items.add(item);
		return this;
	}

	public DropQuery add(ItemStack item) {
		this.add(new DroppedItem(item, id++));
		return this;
	}

	public void send(Player player, String message) {
		EntityPlayerMP mp = (EntityPlayerMP) player;

		DropItemQuery query = new DropItemQuery(mp.getPositionVector(), player.getUniqueId(), items);
		DropItemQueryList.queryList.add(query);
		ItemDropPacket packet = new ItemDropPacket(ItemDropMode.Other, new TextComponentTranslation(message), items);
		Pixelmon.network.sendTo(packet, mp);
	}
}
