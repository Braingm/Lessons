import Trader.PriceProcessor;
import Trader.TradeFileProcessor;
import Trader.TradeManager;


public class Main {
    public static void main(String[] args) {
        PriceProcessor priceProcessor = new PriceProcessor();
        priceProcessor.start();
//        TradeManager manager = new TradeManager();
//        TradeFileProcessor tradeFileProcessor = new TradeFileProcessor(manager);
//        String inputFile = "src/main/resources/trades.txt";
//        String outputFile = "src/main/resources/summary.txt";
//        tradeFileProcessor.processTradesFile(inputFile, outputFile);
    }
}