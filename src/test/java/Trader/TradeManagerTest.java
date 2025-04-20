package Trader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TradeManagerTest {
    private TradeManager tradeManager;
    private Trade buyTrade;
    private Trade sellTrade;

    @BeforeEach
    void setUp() {
        tradeManager = new TradeManager();
        buyTrade = new Trade(1693526400000L, new BigDecimal("50000.0"), new BigDecimal("0.1"), TradeType.BUY);
        sellTrade = new Trade(1693526500000L, new BigDecimal("51000.0"), new BigDecimal("0.2"), TradeType.SELL);
    }

    @Test
    void testAddTrade() {
        tradeManager.addTrade(buyTrade);
        assertEquals(1, tradeManager.getTrades().size());
        assertEquals(buyTrade, tradeManager.getTrades().get(0));
    }

    @Test
    void testGetTradesByType() {
        tradeManager.addTrade(buyTrade);
        tradeManager.addTrade(sellTrade);
        var buyTrades = tradeManager.getTradesByType(TradeType.BUY);
        assertEquals(1, buyTrades.size());
        assertEquals(buyTrade, buyTrades.get(0));
    }

    @Test
    void testCalculateTotalAmountByType(){
        tradeManager.addTrade(buyTrade);
        tradeManager.addTrade(sellTrade);
        BigDecimal buyAmount = tradeManager.calculateAmount(TradeType.BUY);
        BigDecimal sellAmount = tradeManager.calculateAmount(TradeType.SELL);
        assertEquals(new BigDecimal("0.1"), buyAmount);
        assertEquals(new BigDecimal("0.2"), sellAmount);
    }

    @Test
    void testCalculateTotalAMountByTypeEmpty() {
        BigDecimal amount = tradeManager.calculateAmount(TradeType.BUY);
        assertEquals(BigDecimal.ZERO, amount);
    }
}
