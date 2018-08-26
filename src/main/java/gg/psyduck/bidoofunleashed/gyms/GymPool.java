package gg.psyduck.bidoofunleashed.gyms;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.comm.PixelmonData;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import com.pixelmonmod.pixelmon.enums.forms.EnumCastform;
import com.pixelmonmod.pixelmon.enums.forms.IEnumForm;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.spec.BU3PokemonSpec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.swing.text.html.parser.Entity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class GymPool {

	private final File path;
    private transient List<BU3PokemonSpec> team = Lists.newArrayList();

    public GymPool(File path) {
    	this.path = path;
    	if(!path.exists()) {
    		path.getParentFile().mkdirs();
		    try {
			    path.createNewFile();
		    } catch (IOException ignored) {}
	    }
    }

    public GymPool importFromShowdownStyle() {
    	team = Lists.newArrayList();
    	try {
		    BufferedReader reader = new BufferedReader(new FileReader(path));

		    StringBuilder block = new StringBuilder();
		    String line;
		    BU3PokemonSpec spec;
		    while((line = reader.readLine()) != null) {
		    	if(line.startsWith("//") || line.startsWith("#")) continue;
		    	if(line.trim().length() == 0) {
				    spec = build(block.toString());
				    if(spec != null) {
					    team.add(spec);
				    }
		    		block = new StringBuilder();
				    continue;
			    }

			    block.append(line).append("\n");
		    }

		    spec = build(block.toString());
		    if(spec != null) {
			    team.add(spec);
		    }
		    reader.close();
	    } catch (Exception e) {
    		e.printStackTrace();
	    }

	    return this;
    }

    private BU3PokemonSpec build(String block) {
    	BU3PokemonSpec spec = new BU3PokemonSpec();
    	String[] lines = block.split("\n");

    	String details = lines[0].trim();
	    Pattern pattern = Pattern.compile("((?<nickname>[a-zA-Z]+)(((-)(?<form1>[a-zA-Z]+))?))(( \\((?<name>[a-zA-Z]{2,})(-)?(?<form2>[a-zA-Z]+))\\))?( \\((?<gender>[MF])\\))?( @ (?<item>[a-zA-Z]+( [a-zA-Z]+)?))?");
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
			    spec.item = item;
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
		    			if(f.getFormSuffix().substring(1).equalsIgnoreCase(form)) {
		    				spec.form = f.getForm();
					    }
				    }
			    }
		    }
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
    		if(matcher.find()) {
			    spec.shiny = true;
		    }
    		return spec;
	    }),
    	EVs("EVs: ", true,
	    Pattern.compile("EVs: ((?<hp>[0-9]{1,3}) HP)?( / )?((?<attack>[0-9]{1,3}) Atk)?( / )?((?<defense>[0-9]{1,3}) Def)?( / )?((?<spatk>[0-9]{1,3}) SpA)?( / )?((?<spdef>[0-9]{1,3}) SpD)?( / )?((?<speed>[0-9]{1,3}) Spe)?"),
        (spec, matcher) -> {
    		if(matcher.find()) {
			    String index;
			    if(spec.extraSpecs == null) {
				    spec.extraSpecs = Lists.newArrayList();
			    }
			    spec.extraSpecs.add(BU3PokemonSpec.getSpecForKey("evhp").parse((index = matcher.group("hp")) != null ? index : "0"));
			    spec.extraSpecs.add(BU3PokemonSpec.getSpecForKey("evatk").parse((index = matcher.group("attack")) != null ? index : "0"));
			    spec.extraSpecs.add(BU3PokemonSpec.getSpecForKey("evdef").parse((index = matcher.group("defense")) != null ? index : "0"));
			    spec.extraSpecs.add(BU3PokemonSpec.getSpecForKey("evspatk").parse((index = matcher.group("spatk")) != null ? index : "0"));
			    spec.extraSpecs.add(BU3PokemonSpec.getSpecForKey("evspdef").parse((index = matcher.group("spdef")) != null ? index : "0"));
			    spec.extraSpecs.add(BU3PokemonSpec.getSpecForKey("evspeed").parse((index = matcher.group("speed")) != null ? index : "0"));
		    }
            return spec;
        }),
    	IVs("IVs: ", true,
	    Pattern.compile("IVs: ((?<hp>[0-9]{1,2}) HP)?( / )?((?<attack>[0-9]{1,2}) Atk)?( / )?((?<defense>[0-9]{1,2}) Def)?( / )?((?<spatk>[0-9]{1,2}) SpA)?( / )?((?<spdef>[0-9]{1,2}) SpD)?( / )?((?<speed>[0-9]{1,2}) Spe)?"),
	    (spec, matcher) -> {
    		if(matcher.find()) {
			    String index;
			    if(spec.extraSpecs == null) {
				    spec.extraSpecs = Lists.newArrayList();
			    }
			    spec.extraSpecs.add(BU3PokemonSpec.getSpecForKey("ivhp").parse((index = matcher.group("hp")) != null ? index : "31"));
			    spec.extraSpecs.add(BU3PokemonSpec.getSpecForKey("ivatk").parse((index = matcher.group("attack")) != null ? index : "31"));
			    spec.extraSpecs.add(BU3PokemonSpec.getSpecForKey("ivdef").parse((index = matcher.group("defense")) != null ? index : "31"));
			    spec.extraSpecs.add(BU3PokemonSpec.getSpecForKey("ivspatk").parse((index = matcher.group("spatk")) != null ? index : "31"));
			    spec.extraSpecs.add(BU3PokemonSpec.getSpecForKey("ivspdef").parse((index = matcher.group("spdef")) != null ? index : "31"));
			    spec.extraSpecs.add(BU3PokemonSpec.getSpecForKey("ivspeed").parse((index = matcher.group("speed")) != null ? index : "31"));
		    }
		    return spec;
	    }),
    	Nature("Nature", false, Pattern.compile("(?<nature>[a-zA-Z]+) Nature"), (spec, matcher) -> {
    		if(matcher.find()) {
			    String nature = matcher.group("nature");
			    if (nature != null && EnumNature.hasNature(nature)) {
				    spec.nature = (byte) EnumNature.valueOf(nature).ordinal();
			    }
		    }
		    return spec;
	    }),
	    Attacks("- ", true, Pattern.compile("- (?<attack>[a-zA-Z]+( [a-zA-Z]+)?)"), (spec, matcher) -> {
	    	if(matcher.find()) {
			    String attack = matcher.group("attack");
			    if (attack != null && AttackBase.getAttackBase(attack).isPresent()) {
			    	if(spec.attacks == null) {
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
}
