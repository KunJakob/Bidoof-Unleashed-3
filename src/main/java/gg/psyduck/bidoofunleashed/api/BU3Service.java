package gg.psyduck.bidoofunleashed.api;

import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.gyms.Gym;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface BU3Service {

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
	Map<Gym, Map<UUID, EnumLeaderType>> getAllLeaders();

	/**
	 * Fetches the set of leaders, by their UUID, for a gym with the specified name.
	 * If the gym doesn't exist, the call to this method will return an empty
	 * Optional. Otherwise, all leaders, listed by their UUID, shall be listed.
	 *
	 * @param name The name of the gym
	 * @return A mapping containing leader UUIDs, along with their roles. If the gym
	 * specified doesn't exist, however, an empty Optional.
	 */
	Optional<Map<UUID, EnumLeaderType>> getLeadersForGym(String name);

	/**
	 * Fetches the set of leaders, by their UUID, for the specified gym.
	 *
	 * @param gym The gym to fetch against
	 * @return A mapping containing leader UUIDs, along with their roles.
	 */
	Map<UUID, EnumLeaderType> getLeadersForGym(Gym gym);
}
