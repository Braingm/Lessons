package Trader;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Getter
public class TradeManager {
    private final ArrayList<Trade> trades = new ArrayList<>();

    public void addTrade(Trade trade) {
        this.trades.add(trade);
    }

    public ArrayList<Trade> getTradesByType(TradeType type) {
        return trades.stream()
                .filter(trade -> trade.type().equals(type))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public BigDecimal calculateAmount() {
        return trades.stream()
                .map(Trade::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateAmount(TradeType type) {
        return trades.stream()
                .filter(trade -> trade.type().equals(type))
                .map(Trade::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}