package Trader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TradeFileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TradeFileProcessor.class);
    private final String SUMMARY_FORMAT = "Total trades: %d%nTotal BUY amount: %s%nTotal SELL amount: %s";
    private final String EMPTY_SUMMARY = "Total trades: 0%nTotal BUY amount: 0%nTotal SELL amount: 0";
    private final TradeManager tradeManager;

    public TradeFileProcessor(TradeManager tradeManager) {
        this.tradeManager = tradeManager;
    }

    public void processTradesFile(String inputFile, String outputFile) {
        readTradeFile(inputFile);
        writeSummaryFile(outputFile);
    }

    private Trade parseTrade(String line) {
        if (line == null || line.trim().isEmpty()) {
            logger.error("Line is empty or null");
            throw new IllegalArgumentException("Line is empty or null");
        }
        String[] result = line.split(";");
        if (result.length != 4) {
            logger.error("Wrong line format");
            throw new IllegalArgumentException("Wrong line format");
        }
        try {
            long timestamp = Long.parseLong(result[0].trim());
            BigDecimal price = new BigDecimal(result[1].trim());
            BigDecimal amount = new BigDecimal(result[2].trim());
            TradeType type = TradeType.valueOf(result[3].trim());
            logger.debug("Parsed trade: timestamp={}, price={}, amount={}, type={}", timestamp, price, amount, type);
            return new Trade(timestamp, price, amount, type);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Parse line error: " + line, e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Wrong operation type in line: " + line, e);
        }
    }

    private void readTradeFile(String inputFile) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Trade trade = parseTrade(line);
                tradeManager.addTrade(trade);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private void writeSummaryFile(String outputFile) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile))) {
            int totalTrades = tradeManager.getTrades().size();
            BigDecimal buyAmount = tradeManager.calculateAmount(TradeType.BUY);
            BigDecimal sellAmount = tradeManager.calculateAmount(TradeType.SELL);

            if (totalTrades == 0) {
                writer.write(String.format(EMPTY_SUMMARY));
            } else {
                writer.write(String.format(SUMMARY_FORMAT, totalTrades, buyAmount, sellAmount));
            }

        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }
}
