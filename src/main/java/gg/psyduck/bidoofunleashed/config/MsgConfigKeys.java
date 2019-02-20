package gg.psyduck.bidoofunleashed.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.configuration.ConfigKey;
import com.nickimpact.impactor.api.configuration.IConfigKeys;
import com.nickimpact.impactor.api.configuration.keys.ListKey;
import com.nickimpact.impactor.api.configuration.keys.StringKey;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MsgConfigKeys implements IConfigKeys {

	public static final ConfigKey<String> STARTUP_STORAGE_PROVIDER = StringKey.of("startup.phase.init.storage.type", "Loading storage provider... [{{storage_type}}]");

	public static final ConfigKey<String> COMMANDS_CHALLENGE_QUEUED = StringKey.of("commands.challenge.queued", "{{bu3_prefix}} &7You've been queued to challenge the &e{{bu3_gym}}&7! Your position: &c{{bu3_queue_position}}");
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
    public static final ConfigKey<String> COMMANDS_ADD_ELITE_FOUR_SUCCESS = StringKey.of("commands.add-e4.success", "{{bu3_prefix}} &7Elite Four successfully created!");
    public static final ConfigKey<String> COMMANDS_ADD_GYM_EXISTS = StringKey.of("commands.add-gym.error.exists", "{{bu3_error}} &7A gym with that name already exists...");
	public static final ConfigKey<String> COMMANDS_ARENA_AREA_SETUP = StringKey.of("commands.arena-setup.applied", "{{bu3_prefix}} &7Arena settings updated!");
	public static final ConfigKey<String> COMMANDS_EDIT_GYM_SUCCESS = StringKey.of("commands.edit-gym.success", "{{bu3_prefix}} &7Gym successfully edited!");
	public static final ConfigKey<String> COMMANDS_ADD_LEADER = StringKey.of("commands.add-leader.success", "{{bu3_prefix}} &7New gym leader added!");
	public static final ConfigKey<String> COMMANDS_ADD_POKEMON = StringKey.of("commands.add-spec.success", "{{bu3_prefix}} &7Spec successfully added!");

	public static final ConfigKey<String> REQUIREMENT_GYM = StringKey.of("requirements.gym", "{{bu3_error}} &7Unfortunately, you can't challenge this gym until you've earned the &e{{bu3_badge}}&7...");
	public static final ConfigKey<String> REQUIREMENT_LEVELCAP = StringKey.of("requirements.level-cap", "{{bu3_error}} &7Unfortunately, a member of your party exceeds the level cap of &e{{bu3_level_cap}}&7...");
	public static final ConfigKey<String> REQUIREMENT_EVOLUTION = StringKey.of("requirements.evolution", "{{bu3_error}} &7Unfortunately, a member of your party represents an unnatural evolution...");
	public static final ConfigKey<String> REQUIREMENT_LEGENDS = StringKey.of("requirements.legends", "{{bu3_error}} &7Unfortunately, a member of your party cannot participate due to the gym's no legendary rule...");
	public static final ConfigKey<String> REQUIREMENT_MEGAS = StringKey.of("requirements.megas", "{{bu3_error}} &7Unfortunately, a member of your party cannot participate due to the gym's no mega pokemon rule...");

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
		    "",
		    "&aInfo: ",
		    "&7Level Cap: &e{{bu3_level_cap}}",
		    "&7Able to Battle? {{bu3_can_battle}}",
		    "",
		    "&cTimes Defeated: &e{{bu3_gym_defeated}}"
    ));

	public static final ConfigKey<String> UI_E4_ICON_TITLE = StringKey.of("ui.gym-list.icons.e4.title", "&e{{bu3_e4:s}}Elite Four");
	public static final ConfigKey<List<String>> UI_E4_ICON_LORE = ListKey.of("ui.gym-list.icons.e4.lore", Lists.newArrayList(
			"&7Status: {{bu3_e4_status}}",
			"",
			"&aInfo: ",
			"&7Level Cap: &e{{bu3_level_cap}}",
			"&7Able to Battle? {{bu3_can_battle}}",
			"",
			"&bStages:",
			"&7{{bu3_e4_1}}: {{bu3_e4_1_state}}",
			"&7{{bu3_e4_2}}: {{bu3_e4_2_state}}",
			"&7{{bu3_e4_3}}: {{bu3_e4_3_state}}",
			"&7{{bu3_e4_4}}: {{bu3_e4_4_state}}",
			"",
			"&7{{bu3_e4_champion}}: {{bu3_e4_champion_state}}",
			"",
			"&cTimes Defeated: &e{{bu3_e4_defeated}}"
	));

	public static final ConfigKey<String> UI_GYM_POOL_RANDOM_TEAM = StringKey.of("ui.gym-pool.icons.random-team", "&eSelect a Random Team");

	public static final ConfigKey<List<String>> MISC_CHALLENGE_COOLDOWN_GYM = ListKey.of("misc.challenge.challenger.cooldown.gym", Lists.newArrayList(
			"&7&m---------------------------------",
			"&c&lActive Cooldown",
			"",
			"&7Attempted Gym: &e{{bu3_gym}}",
			"&7Time Remaining: &e{{bu3_cooldown_time}}",
			"&7&m---------------------------------"
	));
	public static final ConfigKey<List<String>> MISC_CHALLENGE_COOLDOWN_E4 = ListKey.of("misc.challenge.challenger.cooldown.e4", Lists.newArrayList(
			"&7&m---------------------------------",
			"&c&lActive Cooldown",
			"",
			"&7Attempted Elite Four: &e{{bu3_e4}}",
			"&7Time Remaining: &e{{bu3_cooldown_time}}",
			"&7&m---------------------------------"
	));
	public static final ConfigKey<String> MISC_CHALLENGE_BEGINNING = StringKey.of("misc.challenge.challenger.battle-accepted.inform", "{{bu3_prefix}} &7The gym leader is now ready, starting battle in {{bu3_wait}} seconds!");
    public static final ConfigKey<String> MISC_CHALLENGE_BEGINNING_LEADER_RANDOM = StringKey.of("misc.challenge.leader.battle-accepted.random-team", "{{bu3_prefix}} &7Your team was randomly selected, beginning battle in {{bu3_wait}} seconds!");
    public static final ConfigKey<String> MISC_CHALLENGE_BEGINNING_LEADER_SELECTED = StringKey.of("misc.challenge.leader.battle-accepted.selected-team", "{{bu3_prefix}} &7Your team has been applied, starting battle in {{bu3_wait}} seconds!");
	public static final ConfigKey<String> NEW_CHALLENGER_QUEUED = StringKey.of("misc.challenge.leader.new-challenger", "{{bu3_prefix}} &7A new challenger awaits a battle for the &e{{bu3_gym}}&7!");
	public static final ConfigKey<String> MISC_GYM_MORE_REQUIRED = StringKey.of("misc.gym-editing.more-required", "{{bu3_prefix}} &7The gym is, however, not yet complete! Please finish it with &e/bu3 editgym {{bu3_gym}}");
	public static final ConfigKey<String> MISC_GYM_ARENA_REQUIRED = StringKey.of("misc.gym-editing.arena-required", "{{bu3_prefix}} &7The gym now just needs its arena setup! Use &e/bu3 setuparena {{bu3_gym}} <leader|challenger|spectators> to set the locations based on your position!");
	public static final ConfigKey<String> MISC_RELOAD = StringKey.of("misc.plugin-reload", "{{bu3_prefix}} &7Gym data has been updated, and as such, you will need to requeue your challenge!");
	public static final ConfigKey<String> MISC_ALREADY_QUEUED = StringKey.of("misc.challenge.challenger.already-queued", "{{bu3_error}} &7You are already queued to challenge another gym...");
	public static final ConfigKey<List<String>> MISC_LEADER_JOIN_SINGLE = ListKey.of("misc.leader-join.single-gym", Lists.newArrayList(
			"{{bu3_prefix}} &7A leader for the {{bu3_gym}} Gym, &e{{player}}&7, has logged in!"
	));
	public static final ConfigKey<List<String>> MISC_LEADER_JOIN_MULTIPLE = ListKey.of("misc.leader-join.multi-gym", Lists.newArrayList(
			"{{bu3_prefix}} &7A leader for multiple gyms, &e{{player}}&7, has logged in!"
	));
	public static final ConfigKey<List<String>> MISC_LEADER_JOIN_E4 = ListKey.of("misc.leader-join.e4-leader", Lists.newArrayList(
			"{{bu3_prefix}} &7An E4 member, &e{{player}}&7, has logged in!"
	));

	public static final ConfigKey<String> ERRORS_MISSING_PLAYER_STORAGE = StringKey.of("errors.missing-player-storage", "{{bu3_error}} &7A Pixelmon error was encountered, and prevented successful operation...");
	public static final ConfigKey<String> ERRORS_CANT_CHALLENGE = StringKey.of("errors.gym.cant-challenge", "{{bu3_error}} &7Unfortunately, your challenge could not be fulfilled...");
	public static final ConfigKey<String> ERRORS_NOT_OPEN = StringKey.of("errors.not-open", "{{bu3_error}} &7Unfortunately, your challenge could not be fulfilled as the target is closed...");
	public static final ConfigKey<String> ERRORS_E4_ALREADY_DEFEATED = StringKey.of("errors.e4.already-defeated", "{{bu3_error}} &7You've already defeated this stage for the E4...");
	public static final ConfigKey<String> ERRORS_E4_DIFFERING = StringKey.of("errors.e4.differing-e4", "{{bu3_error}} &7You can only challenge E4 members of your current challenge...");
	public static final ConfigKey<String> ERRORS_CANT_CHALLENGE_CHAMPION = StringKey.of("errors.e4.cant-challenge", "{{bu3_error}} &7You must defeat all E4 members before challenging this member of the elite four...");
	public static final ConfigKey<String> ERRORS_E4_NOT_ALL_BADGES = StringKey.of("errors.e4.not-all-badges", "{{bu3_error}} &7You must have all the badges for this E4's category before you can challenge it...");

	public static final ConfigKey<String> BATTLES_WIN_GYM = StringKey.of("battles.win.gym", "{{bu3_prefix}} &7Congrats! You've defeated the &e{{bu3_gym}} Gym&7!");
	public static final ConfigKey<String> BATTLES_WIN_E4 = StringKey.of("battles.win.e4", "{{bu3_prefix}} &7Congrats! You've defeated &eStage {{bu3_e4}} &7of the &a{{bu3_category}} category&7!");
	public static final ConfigKey<String> BATTLES_WIN_CHAMPION = StringKey.of("battles.win.champion", "{{bu3_prefix}} &7Congrats! You've defeated the &e{{bu3_category}} Champion&7!");
	public static final ConfigKey<String> BATTLES_LOSE_GYM = StringKey.of("battles.lose", "{{bu3_prefix}} &7Well shoot! You've lost to the &e{{bu3_gym}} Gym&7...");
	public static final ConfigKey<String> BATTLES_FORFEIT = StringKey.of("battles.forfeit", "{{bu3_prefix}} &7The challenge ended in a forfeit...");
	public static final ConfigKey<String> LEADER_LOSE_BATTLE = StringKey.of("battles.leaders.lose", "{{bu3_prefix}} &7You've lost the battle to challenger &e{{bu3_challenger}}&7...");
	public static final ConfigKey<String> LEADER_WIN_BATTLE = StringKey.of("battles.leaders.win", "{{bu3_prefix}} &7You've won the battle against challenger &e{{bu3_challenger}}&7...");

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
