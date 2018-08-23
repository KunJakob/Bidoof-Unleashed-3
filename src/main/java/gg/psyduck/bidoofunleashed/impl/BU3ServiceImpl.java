package gg.psyduck.bidoofunleashed.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.BU3Service;
import gg.psyduck.bidoofunleashed.api.enums.EnumLeaderType;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

@Getter
public class BU3ServiceImpl implements BU3Service {

	private List<Gym> gyms = Lists.newArrayList();

	@Override
	public Optional<PlayerData> getPlayerData(UUID uuid) {
		PlayerData data = BidoofUnleashed.getInstance().getDataRegistry().getPlayerData(uuid);
		if(data == null) {
			try {
				return BidoofUnleashed.getInstance().getStorage().getPlayerData(uuid).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return Optional.empty();
			}
		} else {
			return Optional.of(data);
		}
	}

	@Override
	public List<Gym> getAllGyms() {
		return this.gyms;
	}

	@Override
	public Optional<Gym> getGym(String id) {
		return this.gyms.stream().filter(gym -> gym.getName().equalsIgnoreCase(id)).findAny();
	}

	@Override
	public Map<Gym, Map<UUID, EnumLeaderType>> getAllLeaders() {
		Map<Gym, Map<UUID, EnumLeaderType>> leaders = Maps.newHashMap();
		for(Gym gym : this.gyms) {
			leaders.put(gym, gym.getLeaders());
		}

		return leaders;
	}

	@Override
	public Optional<Map<UUID, EnumLeaderType>> getLeadersForGym(String name) {
		Optional<Gym> target = this.gyms.stream().filter(gym -> gym.getName().equalsIgnoreCase(name)).findAny();
		return target.map(Gym::getLeaders);
	}

	@Override
	public Map<UUID, EnumLeaderType> getLeadersForGym(Gym gym) {
		return gym.getLeaders();
	}

	private <T> CompletableFuture<T> makeFuture(Callable<T> supplier) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return supplier.call();
			} catch (Exception e) {
				Throwables.propagateIfPossible(e);
				throw new CompletionException(e);
			}
		}, BidoofUnleashed.getInstance().getAsyncExecutorService());
	}

	private CompletableFuture<Void> makeFuture(BU3ServiceImpl.ThrowingRunnable runnable) {
		return CompletableFuture.runAsync(() -> {
			try {
				runnable.run();
			} catch (Exception e) {
				Throwables.propagateIfPossible(e);
				throw new CompletionException(e);
			}
		}, BidoofUnleashed.getInstance().getAsyncExecutorService());
	}

	private interface ThrowingRunnable {
		void run() throws Exception;
	}
}
