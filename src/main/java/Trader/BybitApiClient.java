package Trader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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

    public String placeMarketOrder(String symbol, String side, BigDecimal qty) throws IOException {
        String endpoint = "/v5/order/create";
        long timestamp = System.currentTimeMillis();
        OrderRequest order = new OrderRequest("spot", symbol, side, "Market", qty.toString());

        String jsonBody = mapper.writeValueAsString(order);
        String signStr = timestamp + apiKey + RECV_WINDOW + jsonBody;
        String signature = hmacSha256(signStr, apiSecret);

        Request request = new Request.Builder()
                .url(baseUrl + endpoint)
                .post(RequestBody.create(jsonBody, MediaType.get("application/json")))
                .addHeader("X-BAPI-API-KEY", apiKey)
                .addHeader("X-BAPI-TIMESTAMP", String.valueOf(timestamp))
                .addHeader("X-BAPI-RECV-WINDOW", RECV_WINDOW)
                .addHeader("X-BAPI-SIGN", signature)
                .build();
    }

    private String hmacSha256(String data, String key) throws  RuntimeException{
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8),"HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash){
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            logger.error("Failed to compute sign", e);
            throw new RuntimeException("Signature error",e);
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
