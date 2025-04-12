package src.lesson1;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Getter
public class TradeManager {
    ArrayList<Trade> trades = new ArrayList<>();

    public void addTrade(Trade trade) {
        this.trades.add(trade);
    }

    public ArrayList<Trade> getTradesByType(TradeType type) {
        return trades.stream()
                .filter(trade -> trade.type().equals(type))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public BigDecimal calculateTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (Trade trade : this.trades) {
            total = total.add(trade.amount());
        }
        return total;
    }

}