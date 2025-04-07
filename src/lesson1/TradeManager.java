package src.lesson1;

import lombok.Getter;

import java.util.ArrayList;

public class TradeManager {
    @Getter
    ArrayList<Trade> trades = new ArrayList<>();

    public void addTrade(Trade trade) {
        this.trades.add(trade);
    }

    public ArrayList<Trade> getTradesByType(TradeType type){
        ArrayList<Trade> tradesByType = new ArrayList<>();
        for (Trade trade : this.trades )
             {
                 if (trade.getType().equals(type))
                     tradesByType.add(trade);
        }
        return tradesByType;
    }

}