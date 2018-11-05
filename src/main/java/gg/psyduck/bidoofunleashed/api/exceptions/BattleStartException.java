package gg.psyduck.bidoofunleashed.api.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.text.Text;

@Getter
@RequiredArgsConstructor
public class BattleStartException extends Exception {

	private final Text reason;
}
