package gg.psyduck.bidoofunleashed.storage;

import com.google.common.base.Throwables;
import com.google.common.collect.Multimap;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
import gg.psyduck.bidoofunleashed.api.extensions.ThrowingRunnable;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.storage.dao.AbstractBU3Dao;
import gg.psyduck.bidoofunleashed.storage.wrapping.PhasedStorage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AbstractBU3Storage implements BU3Storage {

	private final AbstractBU3Dao dao;

	static BU3Storage create(AbstractBU3Dao dao) {
		return PhasedStorage.of(new AbstractBU3Storage(dao));
	}

	private <T> CompletableFuture<T> makeFuture(Callable<T> supplier) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return supplier.call();
			} catch (Exception e) {
				e.printStackTrace();
				Throwables.throwIfUnchecked(e);
				throw new CompletionException(e);
			}
		}, BidoofUnleashed.getInstance().getAsyncExecutorService());
	}

	private CompletableFuture<Void> makeFuture(ThrowingRunnable runnable) {
		return CompletableFuture.runAsync(() -> {
			try {
				runnable.run();
			} catch (Exception e) {
				e.printStackTrace();
				Throwables.throwIfUnchecked(e);
				throw new CompletionException(e);
			}
		}, BidoofUnleashed.getInstance().getAsyncExecutorService());
	}

	@Override
	public void init() throws Exception {
		dao.init();
	}

	@Override
	public void shutdown() throws Exception {
		dao.shutdown();
	}

	@Override
	public CompletableFuture<Void> addPlayerData(PlayerData data) {
		return makeFuture(() -> dao.addPlayerData(data));
	}

	@Override
	public CompletableFuture<Void> updatePlayerData(PlayerData data) {
		return makeFuture(() -> dao.updatePlayerData(data));
	}

	@Override
	public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid) {
		return makeFuture(() -> dao.getPlayerData(uuid));
	}

	@Override
	public CompletableFuture<Void> addGym(Gym gym) {
		return makeFuture(() -> dao.addGym(gym));
	}

	@Override
	public CompletableFuture<Void> updateGym(Gym gym) {
		return makeFuture(() -> dao.updateGym(gym));
	}

	@Override
	public CompletableFuture<Multimap<Category, Gym>> fetchGyms() {
		return makeFuture(dao::fetchGyms);
	}

    @Override
    public CompletableFuture<Void> removeGym(Gym gym) {
        return makeFuture(() -> dao.removeGym(gym));
    }

	@Override
	public CompletableFuture<Void> addE4(EliteFour e4) {
		return makeFuture(() -> dao.addE4(e4));
	}

	@Override
	public CompletableFuture<Void> updateE4(EliteFour e4) {
		return makeFuture(() -> dao.updateE4(e4));
	}

	@Override
	public CompletableFuture<Map<Category, EliteFour>> fetchE4() {
		return makeFuture(dao::fetchE4);
	}

	@Override
	public CompletableFuture<Void> removeE4(EliteFour e4) {
		return makeFuture(() -> dao.removeE4(e4));
	}
}
