package src;

import src.lesson1.Trade;
import src.lesson1.TradeManager;
import src.lesson1.TradeType;

import java.math.BigDecimal;


public class Main {
    public static void main(String[] args) {
        Trade trade1 = new Trade(1693526500000L, new BigDecimal("50000"), new BigDecimal("0.1"), TradeType.BUY);
        Trade trade2 = new Trade(1693526600000L, new BigDecimal("51000"), new BigDecimal("0.2"), TradeType.SELL);
        TradeManager manager = new TradeManager();
        manager.addTrade(trade1);
        manager.addTrade(trade2);

        System.out.println(manager.calculateTotalAmount());
    }
}