package gg.psyduck.bidoofunleashed.storage.wrapping;

import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.storage.BU3Storage;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor(staticName = "of")
public class PhasedStorage implements BU3Storage {

	private final BU3Storage delegate;
	private final Phaser phaser = new Phaser();

	@Override
	public void init() throws Exception {
		delegate.init();
	}

	@Override
	public void shutdown() throws Exception {
		try {
			phaser.awaitAdvanceInterruptibly(phaser.getPhase(), 10, TimeUnit.SECONDS);
		} catch (InterruptedException | TimeoutException e) {
			e.printStackTrace();
		}

		delegate.shutdown();
	}

	@Override
	public CompletableFuture<Void> addOrUpdatePlayerData(PlayerData data) {
		phaser.register();
		try {
			return delegate.addOrUpdatePlayerData(data);
		} finally {
			phaser.arriveAndDeregister();
		}
	}

	@Override
	public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid) {
		phaser.register();
		try {
			return delegate.getPlayerData(uuid);
		} finally {
			phaser.arriveAndDeregister();
		}
	}

	@Override
	public CompletableFuture<Void> addOrUpdateGym(Gym gym) {
		phaser.register();
		try {
			return delegate.addOrUpdateGym(gym);
		} finally {
			phaser.arriveAndDeregister();
		}
	}

	@Override
	public CompletableFuture<List<Gym>> fetchGyms() {
		phaser.register();
		try {
			return delegate.fetchGyms();
		} finally {
			phaser.arriveAndDeregister();
		}
	}

    @Override
    public CompletableFuture<Void> removeGym(Gym gym) {
	    phaser.register();
	    try {
	        return delegate.removeGym(gym);
        } finally {
	        phaser.arriveAndDeregister();
        }
    }
}
