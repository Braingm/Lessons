package src.lesson1;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class Trade {
    final long timestamp;
    final BigDecimal price;
    final BigDecimal amount;
    final TradeType type;
}
