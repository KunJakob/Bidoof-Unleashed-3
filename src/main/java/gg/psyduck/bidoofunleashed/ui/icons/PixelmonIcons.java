package gg.psyduck.bidoofunleashed.ui.icons;

import com.nickimpact.impactor.gui.v2.Icon;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import com.pixelmonmod.pixelmon.util.helpers.SpriteHelper;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.item.inventory.ItemStack;

public class PixelmonIcons {

	public static Icon getPicture(EnumPokemon spec, boolean shiny, int form) {
		return new Icon(createPicture(spec, shiny, form));
	}

	private static ItemStack createPicture(EnumPokemon pokemon, boolean shiny, int form) {
		net.minecraft.item.ItemStack nativeItem = new net.minecraft.item.ItemStack(PixelmonItems.itemPixelmonSprite);
		NBTTagCompound nbt = new NBTTagCompound();
		String idValue = String.format("%03d", pokemon.getNationalPokedexInteger());

		if (shiny) {
			nbt.setString(NbtKeys.SPRITE_NAME, "pixelmon:sprites/shinypokemon/" + idValue + SpriteHelper.getSpriteExtra(pokemon.name, form));
		} else {
			nbt.setString(NbtKeys.SPRITE_NAME, "pixelmon:sprites/pokemon/" + idValue + SpriteHelper.getSpriteExtra(pokemon.name, form));
		}

		nativeItem.setTagCompound(nbt);
		return (ItemStack) (Object) nativeItem;
	}
}
