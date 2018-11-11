package gg.psyduck.bidoofunleashed.api.battlables;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.comm.PixelmonData;
import com.pixelmonmod.pixelmon.config.PixelmonEntityList;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import com.pixelmonmod.pixelmon.enums.forms.EnumCastform;
import com.pixelmonmod.pixelmon.enums.forms.EnumForms;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import gg.psyduck.bidoofunleashed.api.pixelmon.specs.BU3PokemonSpec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShowdownImporter {

	public static List<BU3PokemonSpec> importFromFile(File file) {
		List<BU3PokemonSpec> pool = Lists.newArrayList();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			StringBuilder block = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					if (line.startsWith("//") || line.startsWith("#")) continue;
					if (line.trim().length() == 0) {
						if(block.length() != 0) {
							pool.add(build(block.toString()));
							block = new StringBuilder();
						}
						continue;
					}

					block.append(line).append("\n");
				} catch (Exception e) {
					block = new StringBuilder();
					e.printStackTrace();
				}
			}

			if (block.length() != 0) {
				pool.add(build(block.toString()));
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pool;
	}

	private static BU3PokemonSpec build(String block) throws Exception {
		BU3PokemonSpec spec = new BU3PokemonSpec();
		String[] lines = block.split("\n");

		String details = lines[0].trim();
		Pattern pattern = Pattern.compile("((?<nickname>[a-zA-Z]+)(((-)(?<form1>[a-zA-Z-]+))?))(( \\((?<name>[a-zA-Z]{2,})(-)?(?<form2>[a-zA-Z-]+))\\))?( \\((?<gender>[MF])\\))?( @ (?<item>[a-zA-Z]+( [a-zA-Z]+)?))?");
		Matcher matcher = pattern.matcher(details);
		if(matcher.find()) {
			String nickname = matcher.group("nickname");
			String name = matcher.group("name");
			if (name == null) {
				spec.name = nickname;
			} else {
				spec.name = name;
				spec.nickname = nickname;
			}

			if(spec.name == null) {
				return null;
			}

			String gender = matcher.group("gender");
			if (gender != null) {
				spec.gender = Gender.getGender(gender).getForm();
			}

			String item = matcher.group("item");
			if (item != null) {
				item = item.toLowerCase().replaceAll(" ", "_");
				spec.item = "pixelmon:" + item;
			}

			String form = matcher.group("form1");
			if(form == null) form = matcher.group("form2");
			if(form != null) {
				if(spec.name.equalsIgnoreCase("castform")) {
					IEnumForm f = EnumCastform.getFromName(form);
					if(f != null) {
						spec.form = f.getForm();
					}
				} else {
					EnumPokemon species = EnumPokemon.getFromNameAnyCase(spec.name);
					List<IEnumForm> forms = EnumPokemon.formList.get(species);
					for(IEnumForm f : forms) {
						if(f != EnumForms.NoForm) {
							if (f.getFormSuffix().substring(1).equalsIgnoreCase(form)) {
								spec.form = f.getForm();
							}
						}
					}
				}
			}
		} else {
			spec.form = EnumForms.NoForm.getForm();
		}

		Optional<AbilityBase> optAb = AbilityBase.getAbility(lines[1].substring(lines[1].indexOf(":") + 2));
		spec.ability = optAb.map(AbilityBase::getName).orElse(null);

		lines_loop:
		for(String line : Arrays.copyOfRange(lines, 2, lines.length)) {
			for(Fields field : Fields.values()) {
				if(field.starting) {
					if(line.startsWith(field.identifier)) {
						spec = field.function.apply(spec, field.pattern.matcher(line));
						continue lines_loop;
					}
				} else {
					if(line.contains(field.identifier)) {
						spec = field.function.apply(spec, field.pattern.matcher(line));
						continue lines_loop;
					}
				}
			}
		}

		return spec;
	}

	private enum Fields {
		Shiny("Shiny: ", true, Pattern.compile("Shiny: Yes"), (spec, matcher) -> {
			if (matcher.find()) {
				spec.shiny = true;
			}
			return spec;
		}),
		Level("Level: ", true, Pattern.compile("Level: (?<level>[0-9]{1,3})"), (spec, matcher) -> {
			if (matcher.find()) {
				spec.level = Integer.parseInt(matcher.group("level"));
			} else {
				spec.level = 100;
			}
			return spec;
		}),
		EVs("EVs: ", true,
				Pattern.compile("EVs: ((?<hp>[0-9]{1,3}) HP)?( / )?((?<attack>[0-9]{1,3}) Atk)?( / )?((?<defense>[0-9]{1,3}) Def)?( / )?((?<spatk>[0-9]{1,3}) SpA)?( / )?((?<spdef>[0-9]{1,3}) SpD)?( / )?((?<speed>[0-9]{1,3}) Spe)?"),
				(spec, matcher) -> {
					if (matcher.find()) {
						String index;
						if (spec.extraSpecs == null) {
							spec.extraSpecs = Lists.newArrayList();
						}
						spec.extraSpecs.add(PokemonSpec.getSpecForKey("evhp").parse((index = matcher.group("hp")) != null ? index : "0"));
						spec.extraSpecs.add(PokemonSpec.getSpecForKey("evatk").parse((index = matcher.group("attack")) != null ? index : "0"));
						spec.extraSpecs.add(PokemonSpec.getSpecForKey("evdef").parse((index = matcher.group("defense")) != null ? index : "0"));
						spec.extraSpecs.add(PokemonSpec.getSpecForKey("evspatk").parse((index = matcher.group("spatk")) != null ? index : "0"));
						spec.extraSpecs.add(PokemonSpec.getSpecForKey("evspdef").parse((index = matcher.group("spdef")) != null ? index : "0"));
						spec.extraSpecs.add(PokemonSpec.getSpecForKey("evspeed").parse((index = matcher.group("speed")) != null ? index : "0"));
					}
					return spec;
				}),
		IVs("IVs: ", true,
				Pattern.compile("IVs: ((?<hp>[0-9]{1,2}) HP)?( / )?((?<attack>[0-9]{1,2}) Atk)?( / )?((?<defense>[0-9]{1,2}) Def)?( / )?((?<spatk>[0-9]{1,2}) SpA)?( / )?((?<spdef>[0-9]{1,2}) SpD)?( / )?((?<speed>[0-9]{1,2}) Spe)?"),
				(spec, matcher) -> {
					if (matcher.find()) {
						String index;
						if (spec.extraSpecs == null) {
							spec.extraSpecs = Lists.newArrayList();
						}
						spec.extraSpecs.add(PokemonSpec.getSpecForKey("ivhp").parse((index = matcher.group("hp")) != null ? index : "31"));
						spec.extraSpecs.add(PokemonSpec.getSpecForKey("ivatk").parse((index = matcher.group("attack")) != null ? index : "31"));
						spec.extraSpecs.add(PokemonSpec.getSpecForKey("ivdef").parse((index = matcher.group("defense")) != null ? index : "31"));
						spec.extraSpecs.add(PokemonSpec.getSpecForKey("ivspatk").parse((index = matcher.group("spatk")) != null ? index : "31"));
						spec.extraSpecs.add(PokemonSpec.getSpecForKey("ivspdef").parse((index = matcher.group("spdef")) != null ? index : "31"));
						spec.extraSpecs.add(PokemonSpec.getSpecForKey("ivspeed").parse((index = matcher.group("speed")) != null ? index : "31"));
					}
					return spec;
				}),
		Nature("Nature", false, Pattern.compile("(?<nature>[a-zA-Z]+) Nature"), (spec, matcher) -> {
			if (matcher.find()) {
				String nature = matcher.group("nature");
				if (nature != null && EnumNature.hasNature(nature)) {
					spec.nature = (byte) EnumNature.valueOf(nature).ordinal();
				}
			}
			return spec;
		}),
		Attacks("- ", true, Pattern.compile("- (?<attack>[a-zA-Z]+( [a-zA-Z]+)?)"), (spec, matcher) -> {
			if (matcher.find()) {
				String attack = matcher.group("attack");
				if (attack != null && AttackBase.getAttackBase(attack).isPresent()) {
					if (spec.attacks == null) {
						spec.attacks = Lists.newArrayList();
					}
					spec.attacks.add(attack);
				}
			}

			return spec;
		});

		private String identifier;
		private boolean starting;
		private Pattern pattern;
		private BiFunction<BU3PokemonSpec, Matcher, BU3PokemonSpec> function;

		private Fields(String identifier, boolean starting, Pattern pattern, BiFunction<BU3PokemonSpec, Matcher, BU3PokemonSpec> function) {
			this.identifier = identifier;
			this.starting = starting;
			this.pattern = pattern;
			this.function = function;
		}
	}

	/**
	 * A custom converter for PixelmonData to EntityPixelmon.
	 *
	 * @param data  The PixelmonData of the Pokemon you would like to convert.
	 * @param world The world the player is in.
	 * @return An EntityPixelmon value of PixelmonData
	 */
	public static EntityPixelmon pixelmonDataToEntityPixelmon(PixelmonData data, net.minecraft.world.World world) {
		EntityPixelmon pixelmon = (EntityPixelmon) PixelmonEntityList.createEntityByName(data.name, world);

		if (!data.nickname.isEmpty()) {
			pixelmon.setNickname(data.nickname);
		}

		if (data.lvl > 0 && data.lvl <= 100) {
			pixelmon.getLvl().setLevel(data.lvl);
		}

		pixelmon.setHealth(data.health);
		pixelmon.friendship.setFriendship(data.friendship);

		if (data.gender != null) {
			pixelmon.setGender(data.gender);
		}

		pixelmon.setIsShiny(data.isShiny);

		if (data.heldItem != null) {
			pixelmon.setHeldItem(data.heldItem);
		}

		pixelmon.getLvl().setExp(data.xp);

		if (data.nature != null) {
			pixelmon.setNature(data.nature);
		}

		if (data.growth != null) {
			pixelmon.setGrowth(data.growth);
		}

		if (data.pokeball != null) {
			pixelmon.caughtBall = data.pokeball;
		}

		if (data.moveset != null) {
			Attack attacks[] = {
					(data.moveset[0] != null ? data.moveset[0].getAttack() : null),
					(data.moveset[1] != null ? data.moveset[1].getAttack() : null),
					(data.moveset[2] != null ? data.moveset[2].getAttack() : null),
					(data.moveset[3] != null ? data.moveset[3].getAttack() : null)
			};
			pixelmon.setMoveset(new Moveset(attacks));
		}
		if (!data.ability.isEmpty()) {
			pixelmon.setAbility(data.ability);
		}

		if (data.evs != null) {
			pixelmon.stats.evs.speed = data.evs[5];
			pixelmon.stats.evs.specialAttack = data.evs[3];
			pixelmon.stats.evs.specialDefence = data.evs[4];
			pixelmon.stats.evs.defence = data.evs[2];
			pixelmon.stats.evs.attack = data.evs[1];
			pixelmon.stats.evs.hp = data.evs[0];
		}

		if (data.ivs != null) {
			pixelmon.stats.ivs.Speed = data.ivs[5];
			pixelmon.stats.ivs.SpAtt = data.ivs[3];
			pixelmon.stats.ivs.SpDef = data.ivs[4];
			pixelmon.stats.ivs.Defence = data.ivs[2];
			pixelmon.stats.ivs.Attack = data.ivs[1];
			pixelmon.stats.ivs.HP = data.ivs[0];
		}

		return pixelmon;
	}
}
