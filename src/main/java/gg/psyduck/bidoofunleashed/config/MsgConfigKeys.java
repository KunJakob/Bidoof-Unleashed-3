package gg.psyduck.bidoofunleashed.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import com.nickimpact.impactor.api.configuration.IConfigKeys;
import com.nickimpact.impactor.api.configuration.keys.ListKey;
import com.nickimpact.impactor.api.configuration.keys.StringKey;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MsgConfigKeys implements IConfigKeys {

	public static final ConfigKey<String> STARTUP_STORAGE_PROVIDER = StringKey.of("startup.phase.init.storage.type", "Loading storage provider... [{{storage_type}}]");

	public static final ConfigKey<String> COMMANDS_CHALLENGE_QUEUED = StringKey.of("commands.challenge.queued", "{{bu3_prefix}} &7You've been queued to challenge the &e{{bu3_gym}} Gym&7! Your position: &c{{bu3_queue_position}}");
    public static final ConfigKey<String> COMMANDS_GIVE_BADGE_LEADER = StringKey.of("commands.give-badge.leader", "{{bu3_prefix}} &7You've given the {{bu3_badge}} Badge to {{player}}");
    public static final ConfigKey<String> COMMANDS_GIVE_BADGE_CHALLENGER = StringKey.of("commands.give-badge.challenger", "{{bu3_prefix}} &7You've been given the {{bu3_badge}} Badge!");
    public static final ConfigKey<String> COMMANDS_REMOVE_BADGE_LEADER = StringKey.of("commands.remove-badge.leader", "{{bu3_prefix}} &7You've taken the {{bu3_badge}} Badge from {{player}}");
    public static final ConfigKey<String> COMMANDS_REMOVE_BADGE_CHALLENGER = StringKey.of("commands.remove-badge.challenger", "{{bu3_prefix}} &7The {{bu3_badge}} Badge has been taken from you");
    public static final ConfigKey<String> COMMANDS_DELETE_GYM_SUCCESS = StringKey.of("commands.delete-gym.success", "{{bu3_prefix}} &7You have successfully deleted the {{bu3_gym}} Gym");
    public static final ConfigKey<String> COMMANDS_OPEN_GYM_SUCCESS = StringKey.of("commands.open-gym.success", "{{bu3_prefix}} &7You have successfully opened the {{bu3_gym}} Gym");
    public static final ConfigKey<String> COMMANDS_CLOSE_GYM_SUCCESS = StringKey.of("commands.close-gym.success", "{{bu3_prefix}} &7You have successfully closed the {{bu3_gym}} Gym");
	public static final ConfigKey<String> COMMANDS_ACCEPT_LEADER_SELECTING_TEAM = StringKey.of("commands.accept-challenge.challenger.leader-selecting", "{{bu3_prefix}} &7Your challenge has been accepted! Please wait for the leader to select their team...");
	public static final ConfigKey<String> COMMANDS_ACCEPT_EMPTY_QUEUE = StringKey.of("commands.accept-challenge.leader.empty-queue", "{{bu3_error}} &7No challenges currently await you...");
	public static final ConfigKey<String> COMMANDS_ACCEPT_EMPTY_TEAM_POOL = StringKey.of("commands.accept-challenge.leader.empty-team-pool", "{{bu3_error}} &7The team pool for the challenger is empty, unable to accept challenge...");
	public static final ConfigKey<String> COMMANDS_ACCEPT_NOT_ON_DUTY = StringKey.of("commands.accept-challenge.leader.not-active", "{{bu3_error}} &7You aren't currently on duty...");
    public static final ConfigKey<String> COMMANDS_ADD_REQUIREMENT_SUCCESS = StringKey.of("commands.add-requirement.success", "{{bu3_prefix}} &7You have successfully added the requirement {{bu3_requirement}} to the gym {{bu3_gym} Gym");
    public static final ConfigKey<String> COMMANDS_ADD_GYM_SUCCESS = StringKey.of("commands.add-gym.success", "{{bu3_prefix}} &7Gym successfully created!");
	public static final ConfigKey<String> COMMANDS_ARENA_AREA_SETUP = StringKey.of("commands.arena-setup.applied", "{{bu3_prefix}} &7Arena settings updated!");

	public static final ConfigKey<String> REQUIREMENT_GYM = StringKey.of("requirements.gym", "{{bu3_error}} &7Unfortunately, you can't challenge this gym until you've earned the &e{{bu3_badge}}&7...");
	public static final ConfigKey<String> REQUIREMENT_LEVELCAP = StringKey.of("requirements.level-cap", "{{bu3_error}} &7Unfortunately, a member of your party exceeds the level cap of &e{{bu3_level_cap}}&7...");
	public static final ConfigKey<String> REQUIREMENT_EVOLUTION = StringKey.of("requirements.evolution", "{{bu3_error}} &7Unfortunately, a member of your party represents an unnatural evolution...");
	public static final ConfigKey<String> REQUIREMENT_LEGENDS = StringKey.of("requirements.legends", "{{bu3_error}} &7Unfortunately, a member of your party cannot participate due to the gym's no legendary rule...");

	public static final ConfigKey<String> SOURCE_NOT_PLAYER = StringKey.of("commands.error.src-not-player", "{{bu3_error}} &7You must be a player to use this command!");
    public static final ConfigKey<String> PLAYER_NOT_LEADER = StringKey.of("commands.error.player-not-leader", "{{bu3_error}} &7You must be a gym leader to use this command!");
    public static final ConfigKey<String> PLAYER_NOT_LEADER_OF_GYM = StringKey.of("commands.error.player-not-leader", "{{bu3_error}} &7You must be a gym leader for this gym to accept its challenges!");
    public static final ConfigKey<String> BADGE_NOT_FOUND = StringKey.of("commands.error.badge-not-present", "{{bu3_error}} {{player}} does not have that badge!");
    public static final ConfigKey<String> INVALID_REQUIREMENT = StringKey.of("commands.error.invalid-requirement", "{bu3_error}} &7You have specified an invalid requirement");

    public static final ConfigKey<String> GYM_TITLE = StringKey.of("ui.team-pool.title", "&3Team Pool &7- &c{{bu3_gym}}");
    public static final ConfigKey<String> SPECIES_TITLE = StringKey.of("ui.team-pool.species.title", "&3Team Pool &7- &c{{bu3_pokemon}}");
	public static final ConfigKey<String> EMPTY_GYM_POOL_TITLE = StringKey.of("ui.team-pool.icons.empty.title", "&cEmpty Gym Pool...");
    public static final ConfigKey<List<String>> EMPTY_GYM_POOL_LORE = ListKey.of("ui.team-pool.icons.empty.lore", Lists.newArrayList(
    		"&7Apparently, this gym has no",
		    "&7registered pokemon to its pool",
		    "&7of rental pokemon. You should",
		    "&7add some!"
    ));

    public static final ConfigKey<String> UI_GYM_ICON_TITLE = StringKey.of("ui.gym-list.icons.gym.title", "&e{{bu3_gym}}");
    public static final ConfigKey<List<String>> UI_GYM_ICON_LORE = ListKey.of("ui.gym-list.icons.gym.lore", Lists.newArrayList(
    		"&7Status: {{bu3_gym_status}}",
		    "&7Stats Shown: &e{{bu3_gym_stage}}",
		    "&7Level Cap: &e{{bu3_level_cap}}",
		    "&7Able to Battle? {{bu3_can_battle}}",
		    "",
		    "&7Leaders:"
    ));
	public static final ConfigKey<String> UI_GYM_POOL_RANDOM_TEAM = StringKey.of("ui.gym-pool.icons.random-team", "&eSelect a Random Team");


	public static final ConfigKey<String> MISC_CHALLENGE_COOLDOWN = StringKey.of("misc.challenge.challenger.cooldown", "{{bu3_error}} &7Sorry, but your cooldown for the &a{{bu3_gym}} &7has yet to expire! Time remaining: &e{{bu3_cooldown_time}}");
	public static final ConfigKey<String> MISC_CHALLENGE_BEGINNING = StringKey.of("misc.challenge.challenger.battle-accepted.inform", "{{bu3_prefix}} &7The gym leader is now ready, starting battle in {{bu3_wait}} seconds!");
    public static final ConfigKey<String> MISC_CHALLENGE_BEGINNING_LEADER_RANDOM = StringKey.of("misc.challenge.leader.battle-accepted.random-team", "{{bu3_prefix}} &7Your team was randomly selected, beginning battle in {{bu3_wait}} seconds!");
    public static final ConfigKey<String> MISC_CHALLENGE_BEGINNING_LEADER_SELECTED = StringKey.of("misc.challenge.leader.battle-accepted.selected-team", "{{bu3_prefix}} &7Your team has been applied, starting battle in {{bu3_wait}} seconds!");
	public static final ConfigKey<String> NEW_CHALLENGER_QUEUED = StringKey.of("misc.challenge.leader.new-challenger", "{{bu3_prefix}} &7A new challenger awaits a battle for the &e{{bu3_gym}} Gym&7!");

	public static final ConfigKey<String> ERRORS_MISSING_PLAYER_STORAGE = StringKey.of("errors.missing-player-storage", "{{bu3_error}} &7A Pixelmon error was encountered, and prevented successful operation...");

	public static final ConfigKey<String> BATTLES_WIN = StringKey.of("battles.win", "{{bu3_prefix}} &7Congrats! You've defeated the &e{{bu3_gym}} Gym&7!");
	public static final ConfigKey<String> BATTLES_LOSE = StringKey.of("battles.lose", "{{bu3_prefix}} &7Well shoot! You've lost to the &e{{bu3_gym}} Gym&7!");

	private static Map<String, ConfigKey<?>> KEYS = null;

	@Override
	public Map<String, ConfigKey<?>> getAllKeys() {
		if(KEYS == null) {
			Map<String, ConfigKey<?>> keys = new LinkedHashMap<>();

			try {
				Field[] values = MsgConfigKeys.class.getFields();
				for(Field f : values) {
					if(!Modifier.isStatic(f.getModifiers()))
						continue;

					Object val = f.get(null);
					if(val instanceof ConfigKey<?>)
						keys.put(f.getName(), (ConfigKey<?>) val);
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			KEYS = ImmutableMap.copyOf(keys);
		}

		return KEYS;
	}
}
