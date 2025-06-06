package Trader;

import java.math.BigDecimal;

public record Trade(long timestamp, BigDecimal price, BigDecimal amount, TradeType type) {
}
