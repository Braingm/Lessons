package Trader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class BybitApiClient {
    private static final Logger logger = LoggerFactory.getLogger(BybitApiClient.class);
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final String apiKey;
    private final String apiSecret;
    private final String baseUrl;
    private static final String RECV_WINDOW = "5000";

    public BybitApiClient() {
        // Getting config
        Properties props = new Properties();
        try (var stream = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            props.load(stream);
            this.apiKey = props.getProperty("bybit.api.key");
            this.apiSecret = props.getProperty("bybit.api.secret");
            this.baseUrl = props.getProperty("bybit.api.baseUrl");
        } catch (IOException e) {
            logger.error("Failed to load config.properties", e);
            throw new RuntimeException("Configuration error", e);
        }

        //HTTP client initialization
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        this.mapper = new ObjectMapper();
    }

    public BigDecimal getMarketPrice(String symbol) throws IOException {
        String endpoint = "/v5/market/tickers?category=spot&symbol=" + symbol;
        Request request = new Request.Builder()
                .url(baseUrl + endpoint)
                .get().build();

        logger.info("Getting market price for {}", symbol);
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("Failed to get price: HTTP {} - {}", response.code(), response.message());
                throw new IOException("Unexpected code " + response.code());
            }

            String json = response.body().string();
            BybitPriceResponse priceResponse = mapper.readValue(json, BybitPriceResponse.class);
            String priceStr = priceResponse.getResult().getList()[0].getLastPrice();
            logger.info("Current price for {}: {}", symbol, priceStr);
            return new BigDecimal(priceStr);
        }
    }

    public void shutdown() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
        logger.info("BybitApiClient shutdown");
    }

    //Classes for JSON parsing
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class BybitPriceResponse {
        private Result result;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Result {
            private String category;
            private Ticker[] list;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Ticker {
            private String symbol;
            private String lastPrice;
        }
    }
    @Data
    static class BybitOrderResponse {
        private Result result;

        @Data
        static class Result {
            private String orderId;
        }
    }

    @Data
    @AllArgsConstructor
    static class OrderRequest {
        private String category;
        private String symbol;
        private String side;
        private String orderType;
        private String qty;

    }
}
