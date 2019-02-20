package gg.psyduck.bidoofunleashed.api.pixelmon.specs;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.ISpecType;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.SpecValue;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.storage.NbtKeys;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MovesetSpec extends SpecValue<String[]> implements ISpecType {

	public MovesetSpec(String key, String[] value) {
		super(key, value);
	}

	@Override
	public List<String> getKeys() {
		return Lists.newArrayList("moveset", "moves", "atks", "attacks");
	}

	@Override
	public SpecValue<?> parse(@Nullable String s) {
		if(s == null) {
			return null;
		}

		String[] moves = s.split(",");
		String[] collected = new String[4];
		for(int i = 0; i < moves.length && i < 4; i++) {
			if(AttackBase.getAttackBase(moves[i]).isPresent()) {
				collected[i] = moves[i];
			}
		}

		if(moves.length == 0) {
			return null;
		}

		return new MovesetSpec(this.key, collected);
	}

	@Override
	public SpecValue<?> readFromNBT(NBTTagCompound nbt) {
		String[] moves = new String[4];

		AtomicInteger ai = new AtomicInteger();
		for(int i = 0; i < 4; i++) {
			int move = nbt.getInteger(NbtKeys.PIXELMON_MOVE_ID + i);
			if(move == 0) {
				continue;
			}

			AttackBase.getAttackBase(move).ifPresent(base -> moves[ai.getAndIncrement()] = base.getLocalizedName());
		}

		return new MovesetSpec(this.key, moves);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, SpecValue<?> value) {
		for(AtomicInteger i = new AtomicInteger(); i.get() < ((String[])value.value).length; i.getAndIncrement()) {
			AttackBase.getAttackBase(((String[])value.value)[i.get()]).ifPresent(attack -> nbt.setInteger(NbtKeys.PIXELMON_MOVE_ID + i.get(), attack.attackIndex));
		}
	}

	@Override
	public Class<? extends SpecValue<?>> getSpecClass() {
		return this.getClass();
	}

	@Override
	public String toParameterForm(SpecValue<?> value) {
		return this.key + ":" + Arrays.toString(this.value);
	}

	@Override
	public Class<String[]> getValueClass() {
		return String[].class;
	}

	@Override
	public void apply(EntityPixelmon pokemon) {
		Moveset moveset = new Moveset();
		for(String atk : this.value) {
			moveset.add(new Attack(AttackBase.getAttackBase(atk).get()));
		}

		for(int i = 0; i < moveset.attacks.length; i++) {
			pokemon.getPokemonData().getMoveset().set(i, moveset.get(i));
		}
	}

	@Override
	public void apply(NBTTagCompound nbt) {
		AtomicInteger ai = new AtomicInteger();
		for(String atk : this.value) {
			nbt.setInteger(NbtKeys.PIXELMON_MOVE_ID + ai.getAndIncrement(), AttackBase.getAttackBase(atk).get().attackIndex);
		}
	}

	@Override
	public void apply(Pokemon pokemon) {
		Moveset moveset = new Moveset();
		for(String atk : this.value) {
			moveset.add(new Attack(AttackBase.getAttackBase(atk).get()));
		}

		for(int i = 0; i < moveset.attacks.length; i++) {
			pokemon.getMoveset().set(i, moveset.get(i));
		}
	}

	@Override
	public boolean matches(EntityPixelmon pokemon) {
		return this.matches(pokemon.getPokemonData());
	}

	@Override
	public boolean matches(NBTTagCompound nbt) {
		AtomicInteger ai = new AtomicInteger();
		for(String atk : this.value) {
			int index = AttackBase.getAttackBase(atk).get().attackIndex;
			if(index != nbt.getInteger(NbtKeys.PIXELMON_MOVE_ID + ai.getAndIncrement())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean matches(Pokemon pokemon) {
		Moveset moveset = pokemon.getMoveset();
		AtomicInteger ai = new AtomicInteger(0);

		for(String atk : this.value) {
			if(!moveset.get(ai.getAndIncrement()).baseAttack.getLocalizedName().equalsIgnoreCase(atk)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public SpecValue<String[]> clone() {
		return new MovesetSpec(this.key, this.value);
	}
}
