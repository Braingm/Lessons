package Trader;

import lombok.Getter;

import java.math.BigDecimal;
import java.sql.*;
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
        try {
            String sql = "INSERT INTO TRADES (timestamp, price, amount, type) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, trade.timestamp());
                stmt.setBigDecimal(2, trade.price());
                stmt.setBigDecimal(3, trade.amount());
                stmt.setString(4, trade.type().name());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add trade to database", e);
        }
    }

    public ArrayList<Trade> getTradesByType(TradeType type) {
        ArrayList<Trade> result = new ArrayList<>();
        try {
            String sql = "SELECT timestamp, price, amount, type FROM TRADES WHERE type = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, type.name());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(new Trade(
                                rs.getLong("timestamp"),
                                rs.getBigDecimal("price"),
                                rs.getBigDecimal("amount"),
                                TradeType.valueOf(rs.getString("type"))
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get trades", e);
        }
        return result;
    }

    public BigDecimal calculateAmount() {
        try {
            String sql = "SELECT SUM(amount) AS total FROM TRADES";
            try (PreparedStatement stmt = connection.prepareStatement(sql)){
                try (ResultSet rs = stmt.executeQuery()){
                    if (rs.next()){
                        BigDecimal total = rs.getBigDecimal("total");
                        return total != null ? total : BigDecimal.ZERO;
                    }
                }

            }
        } catch (SQLException e){
            throw new RuntimeException("Failed to calculate total amount", e);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateAmount(TradeType type) {
        try {
            String sql = "SELECT SUM(amount) AS total FROM TRADES WHERE type = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)){
                stmt.setString(1, type.name());
                try (ResultSet rs = stmt.executeQuery()){
                    if (rs.next()){
                        BigDecimal total = rs.getBigDecimal("total");
                        return total != null ? total : BigDecimal.ZERO;
                    }
                }

            }
        } catch (SQLException e){
            throw new RuntimeException("Failed to calculate total amount", e);
        }
        return BigDecimal.ZERO;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
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
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
        }
    }

}