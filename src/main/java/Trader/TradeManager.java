package Trader;

import lombok.Getter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Getter
public class TradeManager {
    private final ArrayList<Trade> trades = new ArrayList<>();
    private final Connection connection;

    public TradeManager() {
        try {
            connection = DriverManager.getConnection("jdbc:h2:mem:trades;DB_CLOSE_DELAY=-1", "test", "");
            createTable();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

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

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e){
            throw new RuntimeException("Failed to close database connection", e);
        }
    }

    private void createTable() throws SQLException {
        String sql = """
                 CREATE TABLE IF NOT EXISTS TRADES (
                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                 timestamp BIGINT NOT NULL,
                 price DECIMAL(20, 8) NOT NULL,
                 amount DECIMAL(20, 8) NOT NULL,
                 type VARCHAR(4) NOT NULL
                )
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.execute();
        }
    }

}