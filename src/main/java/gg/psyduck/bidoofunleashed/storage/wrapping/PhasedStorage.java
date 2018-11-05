package gg.psyduck.bidoofunleashed.storage.wrapping;

import com.google.common.collect.Multimap;
import gg.psyduck.bidoofunleashed.api.battlables.Category;
import gg.psyduck.bidoofunleashed.e4.EliteFour;
import gg.psyduck.bidoofunleashed.gyms.Gym;
import gg.psyduck.bidoofunleashed.players.PlayerData;
import gg.psyduck.bidoofunleashed.storage.BU3Storage;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
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
	public CompletableFuture<Void> addPlayerData(PlayerData data) {
		phaser.register();
		try {
			return delegate.addPlayerData(data);
		} finally {
			phaser.arriveAndDeregister();
		}
	}

	@Override
	public CompletableFuture<Void> updatePlayerData(PlayerData data) {
		phaser.register();
		try {
			return delegate.updatePlayerData(data);
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
	public CompletableFuture<Void> addGym(Gym gym) {
		phaser.register();
		try {
			return delegate.addGym(gym);
		} finally {
			phaser.arriveAndDeregister();
		}
	}

	@Override
	public CompletableFuture<Void> updateGym(Gym gym) {
		phaser.register();
		try {
			return delegate.updateGym(gym);
		} finally {
			phaser.arriveAndDeregister();
		}
	}

	@Override
	public CompletableFuture<Multimap<Category, Gym>> fetchGyms() {
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

	@Override
	public CompletableFuture<Void> addE4(EliteFour e4) {
		phaser.register();
		try {
			return delegate.addE4(e4);
		} finally {
			phaser.arriveAndDeregister();
		}
	}

	@Override
	public CompletableFuture<Void> updateE4(EliteFour e4) {
		phaser.register();
		try {
			return delegate.updateE4(e4);
		} finally {
			phaser.arriveAndDeregister();
		}
	}

	@Override
	public CompletableFuture<Map<Category, EliteFour>> fetchE4() {
		phaser.register();
		try {
			return delegate.fetchE4();
		} finally {
			phaser.arriveAndDeregister();
		}
	}

	@Override
	public CompletableFuture<Void> removeE4(EliteFour e4) {
		phaser.register();
		try {
			return delegate.removeE4(e4);
		} finally {
			phaser.arriveAndDeregister();
		}
	}
}
