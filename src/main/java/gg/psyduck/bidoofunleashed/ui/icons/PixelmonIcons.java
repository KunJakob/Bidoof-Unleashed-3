package gg.psyduck.bidoofunleashed.ui.icons;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.gui.v2.Icon;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import com.pixelmonmod.pixelmon.util.helpers.SpriteHelper;
import gg.psyduck.bidoofunleashed.api.spec.BU3PokemonSpec;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public class PixelmonIcons {

	public static Icon getPicture(EnumPokemon spec, boolean shiny, int form) {
		return new Icon(createPicture(spec, shiny, form));
	}

	public static ItemStack createPicture(EnumPokemon pokemon, boolean shiny, int form) {
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

	public static ItemStack applySpecDetails(ItemStack picture, BU3PokemonSpec spec) {
		Text level = spec.level != null ? Text.of(TextColors.GRAY, " | ", TextColors.GREEN, "Lvl ", spec.level) : Text.EMPTY;
		picture.offer(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_AQUA, spec.name, level));
		List<Text> lore = Lists.newArrayList();
		lore.add(Text.of(TextColors.GRAY, "Ability: ", TextColors.YELLOW, spec.ability));
		if(spec.nature != null) {
			lore.add(Text.of(TextColors.GRAY, "Nature: ", TextColors.YELLOW, EnumNature.getNatureFromIndex(spec.nature)));
		}
		if(spec.gender != null) {
			lore.add(Text.of(TextColors.GRAY, "Gender: ", TextColors.YELLOW, Gender.getGender(spec.gender)));
		}
		if(spec.form != null) {
			String form = EnumPokemon.getFromNameAnyCase(spec.name).getFormEnum(spec.form).getFormSuffix().substring(1);
			lore.add(Text.of(TextColors.GRAY, "Form: ", TextColors.YELLOW, form.substring(0, 1).toUpperCase() + form.substring(1)));
		}

		int[] evs = new int[6];
		for(int i = 0; i < 6; i++) {
			evs[i] = -1;
		}
		spec.extraSpecs.stream().filter(s -> s.key.startsWith("ev")).forEach(s -> {
			switch (s.key) {
				case "evhp":
					evs[0] = (Integer) s.value;
					break;
				case "evattack":
					evs[1] = (Integer) s.value;
					break;
				case "evdefence":
					evs[2] = (Integer) s.value;
					break;
				case "evspecialattack":
					evs[3] = (Integer) s.value;
					break;
				case "evspecialdefence":
					evs[4] = (Integer) s.value;
					break;
				case "evspeed":
					evs[5] = (Integer) s.value;
					break;
			}
		});

		Text evText = Text.of(TextColors.GRAY, "EVs: ", TextColors.YELLOW);
		boolean added = false;
		for(int i = 0; i < 6; i++) {
			if(evs[i] == -1) continue;

			if(!added) {
				evText = Text.of(evText, TextColors.YELLOW, evs[i]);
				added = true;
			} else {
				evText = Text.of(evText, TextColors.GRAY, " / ", TextColors.YELLOW, evs[i]);
			}
		}
		lore.add(evText);

		int[] ivs = new int[6];
		for(int i = 0; i < 6; i++) {
			ivs[i] = -1;
		}
		spec.extraSpecs.stream().filter(s -> s.key.startsWith("iv")).forEach(s -> {
			switch (s.key) {
				case "ivhp":
					ivs[0] = (Integer) s.value;
					break;
				case "ivattack":
					ivs[1] = (Integer) s.value;
					break;
				case "ivdefence":
					ivs[2] = (Integer) s.value;
					break;
				case "ivspecialattack":
					ivs[3] = (Integer) s.value;
					break;
				case "ivspecialdefence":
					ivs[4] = (Integer) s.value;
					break;
				case "ivspeed":
					ivs[5] = (Integer) s.value;
					break;
			}
		});
		Text ivText = Text.of(TextColors.GRAY, "IVs: ", TextColors.YELLOW);
		added = false;
		for(int i = 0; i < 6; i++) {
			if(ivs[i] == -1) continue;

			if(!added) {
				ivText = Text.of(ivText, TextColors.YELLOW, ivs[i]);
				added = true;
			} else {
				ivText = Text.of(ivText, TextColors.GRAY, " / ", TextColors.YELLOW, ivs[i]);
			}
		}
		lore.add(ivText);
		if(spec.attacks != null) {
			lore.add(Text.of(TextColors.GRAY, "Moveset:"));
			for(String attack : spec.attacks) {
				lore.add(Text.of(TextColors.GRAY, "- ", TextColors.YELLOW, attack));
			}
		}

		picture.offer(Keys.ITEM_LORE, lore);
		return picture;
	}
}
