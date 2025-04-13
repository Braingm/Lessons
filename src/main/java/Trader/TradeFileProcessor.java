package Trader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TradeFileProcessor {
    private final TradeManager tradeManager;

    public TradeFileProcessor(TradeManager tradeManager) {
        this.tradeManager = tradeManager;
    }

    public void processTradeFiles(String inputFile, String outputFile) {
        //Чтение и парсинг trades.txt
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Trade trade = parseTrade(line);
                tradeManager.addTrade(trade);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден: " + inputFile);
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }

        // Запись статистики в summary.txt

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile))) {
            var trades = tradeManager.getTrades();
            for (Trade trade : trades) {
                writer.append(String.valueOf(trade.timestamp()));
                writer.append(";");
                writer.append(trade.price().toString());
                writer.append(";");
                writer.append(trade.amount().toString());
                writer.append(";");
                writer.append(trade.type().toString());
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            System.err.println("Ошибка записи файла: " + e.getMessage());
        }
    }

    private Trade parseTrade(String line) throws IllegalArgumentException {
        String[] result = line.split(";");
        if (result.length != 4)
            throw new IllegalArgumentException("Неправильный формат строки");
        return new Trade(Long.parseLong(result[0]), new BigDecimal(result[1]), new BigDecimal(result[2]), TradeType.valueOf(result[3].toUpperCase()));
    }
}
