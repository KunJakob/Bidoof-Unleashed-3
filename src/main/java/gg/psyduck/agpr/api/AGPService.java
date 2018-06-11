package gg.psyduck.agpr.api;

import gg.psyduck.agpr.data.GymData;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface AGPService {

	/**
	 * Returns a list of {@link GymData}s registered into the current instance of AGP.
	 *
	 * @return A list of all gym data registered into the plugin
	 */
	List<GymData> getAllGyms();

	/**
	 * Attempts to locate a gym by its ID reference. In the event the gym request returns nothing,
	 * {@link Optional#empty()} will be returned. Otherwise, an Optional value with the gym data will
	 * be returned.
	 *
	 * @param id The gym ID for the requested gym
	 * @return An Optional with the {@link GymData} for the gym, or {@link Optional#empty()}
	 */
	Optional<GymData> getGym(String id);

	/**
	 * Retrieves a mapping of leaders to their respective gyms.
	 *
	 * @return A mapping of leaders, by UUID, to their respective gyms.
	 */
	Map<GymData, UUID> getAllLeaders();

	List<UUID> getLeadersForGym(String id);

	List<UUID> getLeadersForGym(GymData gym);
}
