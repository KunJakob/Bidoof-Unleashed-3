package gg.psyduck.bidoofunleashed.api.rewards;

import com.nickimpact.impactor.api.rewards.Reward;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class BU3Reward<T> implements Reward<T> {

	protected T reward;
}
