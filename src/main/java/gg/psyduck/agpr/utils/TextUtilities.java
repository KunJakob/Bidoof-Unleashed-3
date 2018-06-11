package gg.psyduck.agpr.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumPokemon;
import com.pixelmonmod.pixelmon.storage.NbtKeys;

import net.minecraft.nbt.NBTTagCompound;

public class TextUtilities {

  /**
   * Takes a string input and randomly reorders the characters. Recursively makes sure it cannot
   * return the same string.
   * 
   * @param string the input {@code string} to shuffle.
   * @return a {@code string} with randomly reordered characters.
   */
  public static String shuffle(String string) {
    if (StringUtils.isBlank(string)) {
      return string;
    }

    final List<Character> randomChars = new ArrayList<>();
    Collections.addAll(randomChars, ArrayUtils.toObject(string.toCharArray()));
    Collections.shuffle(randomChars);
    String shuffled = StringUtils.join(randomChars, "");
    if (string == shuffled) {
      shuffled = shuffle(string);
    }
    return shuffled;
  }

  /**
   * Convert a millisecond duration to a string format
   * 
   * @param seconds A duration to convert to a string form
   * @return A string of the form "X Days Y Hours Z Minutes A Seconds".
   */
  public static String getDurationBreakdown(long seconds) {
    if (seconds < 0) {
      throw new IllegalArgumentException("Duration must be greater than zero!");
    }

    long days = TimeUnit.SECONDS.toDays(seconds);
    seconds -= TimeUnit.DAYS.toSeconds(days);
    long hours = TimeUnit.SECONDS.toHours(seconds);
    seconds -= TimeUnit.HOURS.toSeconds(hours);
    long minutes = TimeUnit.SECONDS.toMinutes(seconds);
    seconds -= TimeUnit.MINUTES.toSeconds(minutes);

    StringBuilder sb = new StringBuilder(64);
    if (days > 0) {
      sb.append(" " + days);
      sb.append(" Days");
    }
    if (hours > 0) {
      sb.append(" " + hours);
      sb.append(" Hours");
    }
    if (minutes > 0) {
      sb.append(" " + minutes);
      sb.append(" Minutes");
    }
    if (seconds > 0) {
      sb.append(" " + seconds);
      sb.append(" Seconds");
    }

    return sb.toString();
  }

  /**
   * Gets the english Pokedex Entry description of a pokemon from the language files. Removes the
   * pokemon's name to be used with the trivia game.
   * 
   * @param poke The EnumPokemon you wish to retrieve the data for.
   * @return A string of the pokemon's Pokedex Entry description.
   */
  public static String getDescription(EnumPokemon poke) {
    try {
      InputStream is = Pixelmon.class.getResourceAsStream("/assets/pixelmon/lang/en_US.lang");
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String line = "";
      String start = "pixelmon." + poke.name.toLowerCase() + ".description=";
      while ((line = br.readLine()) != null) {

        if (line.startsWith(start)) {
          line = line.replace(start, "");
          return line.replace(poke.name, "___");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }


  public static Text getName(Player p) {
    if (p.getDisplayNameData().displayName().exists()) {
      return p.getDisplayNameData().displayName().get();
    } else {
      return Text.of(p.getName());
    }
  }


  public static Text infoText(NBTTagCompound nbt) {
    Builder textBuilder = Text.builder();


    textBuilder.append(Text.of(TextColors.AQUA, TextStyles.UNDERLINE, "Info"), Text.NEW_LINE);

    textBuilder
        .append(Text.of(TextColors.GREEN, "Level: ", nbt.getInteger(NbtKeys.LEVEL), Text.NEW_LINE));

    textBuilder.append(Text.of(TextColors.LIGHT_PURPLE, "Shiny: ",
        String.valueOf(nbt.getBoolean(NbtKeys.IS_SHINY)), Text.NEW_LINE));

    textBuilder.append(Text.of(TextColors.RED, "Nature: ",
        EnumNature.getNatureFromIndex(nbt.getInteger(NbtKeys.NATURE)).name(), Text.NEW_LINE));

    textBuilder.append(
        Text.of(TextColors.GOLD, "Ability: ", nbt.getString(NbtKeys.ABILITY), Text.NEW_LINE));

    textBuilder.append(Text.of(TextColors.LIGHT_PURPLE, "Growth: ",
        EnumGrowth.getGrowthFromIndex(nbt.getInteger(NbtKeys.GROWTH)).name()));


    Text text = Text.builder()
        .append(Text.of(TextColors.GRAY, "[", TextColors.YELLOW, "Info", TextColors.GRAY, "]"))
        .onHover(TextActions.showText(textBuilder.build())).build();
    return text;
  }

  public static Text ivsText(NBTTagCompound nbt) {
    Builder textBuilder = Text.builder();
    textBuilder.append(Text.of(TextColors.AQUA, TextStyles.UNDERLINE, "IVs"), Text.NEW_LINE);
    textBuilder
        .append(Text.of(TextColors.GREEN, "HP: ", nbt.getInteger(NbtKeys.IV_HP), Text.NEW_LINE));
    textBuilder
        .append(Text.of(TextColors.RED, "ATK: ", nbt.getInteger(NbtKeys.IV_ATTACK), Text.NEW_LINE));
    textBuilder.append(
        Text.of(TextColors.GOLD, "DEF: ", nbt.getInteger(NbtKeys.IV_DEFENCE), Text.NEW_LINE));
    textBuilder.append(Text.of(TextColors.LIGHT_PURPLE, "SATK: ", nbt.getInteger(NbtKeys.IV_SP_ATT),
        Text.NEW_LINE));
    textBuilder.append(
        Text.of(TextColors.YELLOW, "SDEF: ", nbt.getInteger(NbtKeys.IV_SP_DEF), Text.NEW_LINE));
    textBuilder.append(Text.of(TextColors.DARK_AQUA, "SPD: ", nbt.getInteger(NbtKeys.IV_SPEED)));
    Text text = Text.builder()
        .append(Text.of(TextColors.GRAY, "[", TextColors.LIGHT_PURPLE, "IVs", TextColors.GRAY, "]"))
        .onHover(TextActions.showText(textBuilder.build())).build();
    return text;
  }

  public static Text evsText(NBTTagCompound nbt) {
    Builder textBuilder = Text.builder();
    textBuilder.append(Text.of(TextColors.AQUA, TextStyles.UNDERLINE, "EVs"), Text.NEW_LINE);
    textBuilder
        .append(Text.of(TextColors.GREEN, "HP: ", nbt.getInteger(NbtKeys.EV_HP), Text.NEW_LINE));
    textBuilder
        .append(Text.of(TextColors.RED, "ATK: ", nbt.getInteger(NbtKeys.EV_ATTACK), Text.NEW_LINE));
    textBuilder.append(
        Text.of(TextColors.GOLD, "DEF: ", nbt.getInteger(NbtKeys.EV_DEFENCE), Text.NEW_LINE));
    textBuilder.append(Text.of(TextColors.LIGHT_PURPLE, "SATK: ",
        nbt.getInteger(NbtKeys.EV_SPECIAL_ATTACK), Text.NEW_LINE));
    textBuilder.append(Text.of(TextColors.YELLOW, "SDEF: ",
        nbt.getInteger(NbtKeys.EV_SPECIAL_DEFENCE), Text.NEW_LINE));
    textBuilder.append(Text.of(TextColors.DARK_AQUA, "SPD: ", nbt.getInteger(NbtKeys.EV_SPEED)));

    Text text = Text.builder()
        .append(Text.of(TextColors.GRAY, "[", TextColors.RED, "EVs", TextColors.GRAY, "]"))
        .onHover(TextActions.showText(textBuilder.build())).build();
    return text;
  }

  public static Text movesText(NBTTagCompound nbt) {
    Builder textBuilder = Text.builder();
    textBuilder.append(Text.of(TextColors.BLUE, TextStyles.UNDERLINE, "Moves"), Text.NEW_LINE);


    textBuilder.append(Text.of(TextColors.GREEN, "Move 1: ", nbt.hasKey("PixelmonMoveID0")
        ? new Attack(AttackBase.getAttackBase(nbt.getInteger("PixelmonMoveID0")).get()).baseAttack
            .getLocalizedName()
        : "None"), Text.NEW_LINE);

    textBuilder.append(Text.of(TextColors.RED, "Move 2: ", nbt.hasKey("PixelmonMoveID1")
        ? new Attack(AttackBase.getAttackBase(nbt.getInteger("PixelmonMoveID1")).get()).baseAttack
            .getLocalizedName()
        : "None"), Text.NEW_LINE);

    textBuilder.append(Text.of(TextColors.GOLD, "Move 3: ", nbt.hasKey("PixelmonMoveID2")
        ? new Attack(AttackBase.getAttackBase(nbt.getInteger("PixelmonMoveID2")).get()).baseAttack
            .getLocalizedName()
        : "None"), Text.NEW_LINE);

    textBuilder.append(Text.of(TextColors.LIGHT_PURPLE, "Move 4: ", nbt.hasKey("PixelmonMoveID3")
        ? new Attack(AttackBase.getAttackBase(nbt.getInteger("PixelmonMoveID3")).get()).baseAttack
            .getLocalizedName()
        : "None"));

    Text text = Text.builder()
        .append(Text.of(TextColors.GRAY, "[", TextColors.DARK_BLUE, "Moves", TextColors.GRAY, "]"))
        .onHover(TextActions.showText(textBuilder.build())).build();
    return text;
  }

}
