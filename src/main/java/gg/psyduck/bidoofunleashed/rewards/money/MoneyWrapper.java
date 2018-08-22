package gg.psyduck.bidoofunleashed.rewards.money;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MoneyWrapper {

	private String currency;
	private BigDecimal amount;
}
