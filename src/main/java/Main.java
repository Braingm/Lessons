import Trader.BybitApiClient;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
//        TradeManager manager = new TradeManager();
//        try {
//            TradeFileProcessor tradeFileProcessor = new TradeFileProcessor(manager);
//            String inputFile = "src/main/resources/trades.txt";
//            String outputFile = "src/main/resources/summary.txt";
//            tradeFileProcessor.processTradesFile(inputFile, outputFile);
//            new PriceProcessor().start();
//        } finally {
//            manager.close();

        BybitApiClient client = new BybitApiClient();
        client.getMarketPrice("BTCUSDT");
    }
}