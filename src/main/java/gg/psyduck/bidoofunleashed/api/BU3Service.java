package gg.psyduck.bidoofunleashed.api;

import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.api.gyms.Requirement;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface BU3Service {

	/**
	 * Attempts to locate and return a {@link PlayerData} instance specific to a player. The method
	 * will first check against all currently loaded data, falling back to offline data if the player
	 * is not online. If the data doesn't exist whatsoever, an empty Optional will be returned.
	 *
	 * @param uuid The uuid of the target
	 * @return An optionally wrapped instance of a {@link PlayerData}, or an empty optional
	 */
	Optional<PlayerData> getPlayerData(UUID uuid);

	/**
	 * Returns a list of {@link Gym}s registered into the current instance of AGP.
	 *
	 * @return A list of all gym data registered into the plugin
	 */
	List<Gym> getAllGyms();

	/**
	 * Attempts to locate a gym by its ID reference. In the event the gym request returns nothing,
	 * {@link Optional#empty()} will be returned. Otherwise, an Optional value with the gym data will
	 * be returned.
	 *
	 * @param id The gym ID for the requested gym
	 * @return An Optional with the {@link Gym} for the gym, or {@link Optional#empty()}
	 */
	Optional<Gym> getGym(String id);

	/**
	 * Retrieves a mapping of leaders to their respective gyms.
	 *
	 * @return A mapping of leaders, by UUID, to their respective gyms.
	 */
	Map<Gym, List<UUID>> getAllLeaders();

	/**
	 * Fetches the set of leaders, by their UUID, for a gym with the specified name.
	 * If the gym doesn't exist, the call to this method will return an empty
	 * Optional. Otherwise, all leaders, listed by their UUID, shall be listed.
	 *
	 * @param name The name of the gym
	 * @return A mapping containing leader UUIDs, along with their roles. If the gym
	 * specified doesn't exist, however, an empty Optional.
	 */
	Optional<List<UUID>> getLeadersForGym(String name);

	/**
	 * Fetches the set of leaders, by their UUID, for the specified gym.
	 *
	 * @param gym The gym to fetch against
	 * @return A mapping containing leader UUIDs, along with their roles.
	 */
	List<UUID> getLeadersForGym(Gym gym);

	/**
	 * Adds a gym to the Storage Provider, as well as the loaded cache of gyms.
	 *
	 * @param gym The gym to register into the system
	 */
	void addGym(Gym gym);

	/**
	 * Removes a gym from the active Storage Provider, if it exists. If it does not, we essentially fire
	 * a no-op, and just return <code>false</code> to signify no gym was actually removed.
	 *
	 * @param name The name of the gym to purge
	 * @return <code>true</code> on successful gym deletion, <code>false</code> otherwise
	 */
	default boolean purgeGym(String name) {
		return this.getGym(name).filter(this::purgeGym).isPresent();
	}

	/**
	 * Removes a gym from the active Storage Provider, if it exists. If it does not, we essentially fire
	 * a no-op, and just return <code>false</code> to signify no gym was actually removed.
	 *
	 * @param gym The gym to purge
	 * @return <code>true</code> on successful gym deletion, <code>false</code> otherwise
	 */
	boolean purgeGym(Gym gym);

	List<EliteFour> getAllEliteFour();

	void addE4(EliteFour eliteFour);

	/**
	 * Registers a requirement to the system. This method will ensure the game phase is at least before the
	 * {@link org.spongepowered.api.event.game.state.GameStartedServerEvent}, otherwise, the call will fail.
	 *
	 * To specify why this is the case, it's due to the fact that the plugin will need that requirement instance
	 * in order to deserialize gyms potentially implementing the requirement. Gym deserialization happens during the
	 * {@link org.spongepowered.api.event.game.state.GameStartedServerEvent}, so they must be present in order to
	 * successfully deserialize the gym.
	 *
	 * @param requirement The class of the requirement to register
	 */
	void registerRequirement(Class<? extends Requirement> requirement);

	/**
	 * Simply fetches and returns a list of all dummy object requirements available to the plugin
	 * for manipulation.
	 *
	 * @return Dummy requirements which don't have any of their information filled, if they support information at all
	 */
	List<Requirement> getLoadedRequirements();
}
