package gg.psyduck.bidoofunleashed.storage;

import com.google.common.base.Throwables;
import gg.psyduck.bidoofunleashed.BidoofUnleashed;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.storage.dao.AbstractBU3Dao;
import gg.psyduck.bidoofunleashed.storage.wrapping.PhasedStorage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
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
				Throwables.propagateIfPossible(e);
				throw new CompletionException(e);
			}
		}, BidoofUnleashed.getInstance().getAsyncExecutorService());
	}

	private CompletableFuture<Void> makeFuture(ThrowingRunnable runnable) {
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

	@Override
	public void init() throws Exception {
		dao.init();
		BidoofUnleashed.getInstance().getDataRegistry().getGyms().addAll(this.fetchGyms().get());
	}

	@Override
	public void shutdown() throws Exception {
		dao.shutdown();
	}

	@Override
	public CompletableFuture<Void> addOrUpdatePlayerData(PlayerData data) {
		return makeFuture(() -> dao.addOrUpdatePlayerData(data));
	}

	@Override
	public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid) {
		return makeFuture(() -> dao.getPlayerData(uuid));
	}

	@Override
	public CompletableFuture<Void> addOrUpdateGym(Gym gym) {
	    return makeFuture(() -> {
	       dao.addOrUpdateGym(gym);
	       BidoofUnleashed.getInstance().getDataRegistry().getGyms().add(gym);
        });
	}

	@Override
	public CompletableFuture<List<Gym>> fetchGyms() {
		return makeFuture(dao::fetchGyms);
	}

    @Override
    public CompletableFuture<Void> removeGym(Gym gym) {
        return makeFuture(() -> {
            dao.removeGym(gym);
            BidoofUnleashed.getInstance().getDataRegistry().getGyms().remove(gym);
        });
    }
}
