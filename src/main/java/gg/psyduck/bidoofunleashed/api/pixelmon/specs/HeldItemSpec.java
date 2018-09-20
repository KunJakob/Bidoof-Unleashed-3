package gg.psyduck.bidoofunleashed.api.pixelmon.specs;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.ISpecType;
import com.pixelmonmod.pixelmon.api.pokemon.SpecValue;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class HeldItemSpec extends SpecValue<String> implements ISpecType {

	public HeldItemSpec(String key, String value) {
		super(key, value);
	}

	@Override
	public List<String> getKeys() {
		return Lists.newArrayList("item", "holding", "hi", "held");
	}

	@Override
	public SpecValue<?> parse(@Nullable String s) {
		if(s == null) {
			return null;
		} else {
			Optional<ItemType> check = Sponge.getRegistry().getType(ItemType.class, s);
			return check.map(x -> new HeldItemSpec(this.key, s)).orElse(null);
		}
	}

	@Override
	public SpecValue<?> readFromNBT(NBTTagCompound nbt) {
		return new HeldItemSpec(this.key, nbt.getCompoundTag(NbtKeys.HELD_ITEM_STACK).getString("id"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, SpecValue<?> value) {
		nbt.setTag(NbtKeys.HELD_ITEM_STACK, new NBTTagCompound());
		NBTTagCompound ref = nbt.getCompoundTag(NbtKeys.HELD_ITEM_STACK);
		ref.setInteger("count", 1);
		ref.setInteger("damage", 0);
		ref.setString("id", value.value.toString());
	}

	@Override
	public Class<? extends SpecValue<?>> getSpecClass() {
		return this.getClass();
	}

	@Override
	public String toParameterForm(SpecValue<?> specValue) {
		return this.key + ":" + specValue.value.toString();
	}

	@Override
	public Class<String> getValueClass() {
		return String.class;
	}

	@Override
	public void apply(EntityPixelmon pokemon) {
		pokemon.setHeldItem((net.minecraft.item.ItemStack) (Object) ItemStack.builder().itemType(Sponge.getRegistry().getType(ItemType.class, this.value).orElse(ItemTypes.AIR)).build());
	}

	@Override
	public void apply(NBTTagCompound nbt) {
		nbt.setTag(NbtKeys.HELD_ITEM_STACK, new NBTTagCompound());
		NBTTagCompound ref = nbt.getCompoundTag(NbtKeys.HELD_ITEM_STACK);
		ref.setInteger("count", 1);
		ref.setInteger("damage", 0);
		ref.setString("id", this.value);
	}

	@Override
	public boolean matches(EntityPixelmon pokemon) {
		NBTTagCompound nbt = new NBTTagCompound();
		return this.matches(pokemon.writeToNBT(nbt));
	}

	@Override
	public boolean matches(NBTTagCompound nbt) {
		return nbt.getCompoundTag(NbtKeys.HELD_ITEM_STACK).getString("id").equalsIgnoreCase(this.value);
	}

	@Override
	public SpecValue<String> clone() {
		return new HeldItemSpec(this.key, this.value);
	}
}
