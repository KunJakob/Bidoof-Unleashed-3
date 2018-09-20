package gg.psyduck.bidoofunleashed.api.pixelmon.specs;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.ISpecType;
import com.pixelmonmod.pixelmon.api.pokemon.SpecValue;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.List;

public class NicknameSpec extends SpecValue<String> implements ISpecType {

	public NicknameSpec(String key, String value) {
		super(key, value);
	}

	@Override
	public List<String> getKeys() {
		return Lists.newArrayList("nickname");
	}

	@Override
	public SpecValue<?> parse(@Nullable String s) {
		if(s == null) {
			return null;
		} else {
			return new NicknameSpec(this.key, s);
		}
	}

	@Override
	public SpecValue<?> readFromNBT(NBTTagCompound nbt) {
		return this.parse(nbt.getString(NbtKeys.NICKNAME));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, SpecValue<?> value) {
		nbt.setString(NbtKeys.NICKNAME, (String) value.value);
	}

	@Override
	public Class<? extends SpecValue<?>> getSpecClass() {
		return this.getClass();
	}

	@Override
	public String toParameterForm(SpecValue<?> value) {
		return value.key + ":" + value.value.toString();
	}

	@Override
	public Class<String> getValueClass() {
		return String.class;
	}

	@Override
	public void apply(EntityPixelmon pokemon) {
		pokemon.setNickname(this.value);
	}

	@Override
	public void apply(NBTTagCompound nbt) {
		nbt.setString(NbtKeys.NICKNAME, this.value);
	}

	@Override
	public boolean matches(EntityPixelmon pokemon) {
		return pokemon.getNickname().equalsIgnoreCase(this.value);
	}

	@Override
	public boolean matches(NBTTagCompound nbt) {
		return nbt.getString(NbtKeys.NICKNAME).equalsIgnoreCase(this.value);
	}

	@Override
	public SpecValue<String> clone() {
		return new NicknameSpec(this.key, this.value);
	}
}
