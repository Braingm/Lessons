package src.lesson1;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Trade {
    final long timestamp;
    final double price;
    final double amount;
    final TradeType type;
}
