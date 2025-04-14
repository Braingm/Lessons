package Trader;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PriceProcessor {
    private final List<PriceEntry> prices = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    private final String GENERATOR_FORMAT = "Price generated: %.2f";
    private final String AVERAGE_PRICE_FORMAT = "Average price (last 10s): %.2f";

    record PriceEntry(BigDecimal price, long timestamp) {
    }

    public void start() {
        executor.scheduleAtFixedRate(() -> {
            Random random = new Random();
            double price = 50000 + (random.nextDouble() * 50000);
            prices.add(new PriceEntry(new BigDecimal(price), System.currentTimeMillis()));
            System.out.printf((GENERATOR_FORMAT) + "%n", price);
        }, 0, 3, TimeUnit.SECONDS);

        executor.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            prices.removeIf(entry -> now - entry.timestamp > 15000);
            double average = prices.stream()
                    .filter(entry -> now - entry.timestamp() <= 10000)
                    .mapToDouble(entry -> entry.price().doubleValue())
                    .average()
                    .orElse(0.0);
            System.out.printf((AVERAGE_PRICE_FORMAT) + "%n", average);
        }, 10, 10, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS))
                    executor.shutdownNow();
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
            System.out.println("PriceProcessor stopped");
        }));
    }
}
