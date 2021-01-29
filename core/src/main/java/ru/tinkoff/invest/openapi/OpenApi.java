package ru.tinkoff.invest.openapi;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

abstract public class OpenApi implements Closeable {

    protected final OpenApiConfig config;
    protected final boolean isSandboxMode;
    protected final String authToken;

    public OpenApi(@NotNull final String token, final boolean isSandboxMode) {
        this.authToken = "Bearer " + token;
        this.isSandboxMode = isSandboxMode;

        try {
            this.config = extractConfig();
        } catch (IOException ex) {
            throw new RuntimeException("Не удалось считать внутренний конфигурационный файл");
        }
    }

    public boolean isSandboxMode() {
        return this.isSandboxMode;
    }

    @NotNull
    public String getAuthToken() {
        return this.authToken;
    }

    @NotNull
    abstract public SandboxContext getSandboxContext();

    @NotNull
    abstract public OrdersContext getOrdersContext();

    @NotNull
    abstract public PortfolioContext getPortfolioContext();

    @NotNull
    abstract public MarketContext getMarketContext();

    @NotNull
    abstract public OperationsContext getOperationsContext();

    @NotNull
    abstract public UserContext getUserContext();

    @NotNull
    abstract public StreamingContext getStreamingContext();

    /**
     * Извлечение параметров конфигурации.
     *
     * @return Параметры конфигурации.
     */
    @NotNull
    protected static OpenApiConfig extractConfig() throws IOException {
        final Properties prop = new Properties();

        final ClassLoader classLoader = OpenApi.class.getClassLoader();
        try (final InputStream input = classLoader.getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new FileNotFoundException();
            }

            //load properties file from class path, inside static method
            prop.load(input);
        }

        final String host = prop.getProperty("ru.tinkoff.invest.openapi.host");
        final String sandboxHost = prop.getProperty("ru.tinkoff.invest.openapi.host-sandbox");
        final String streamingHost = prop.getProperty("ru.tinkoff.invest.openapi.streaming");
        final int streamingParallelism = Integer.parseInt(prop.getProperty("ru.tinkoff.invest.openapi.streaming-parallelism"));

        return new OpenApiConfig(host, sandboxHost, streamingHost, streamingParallelism);
    }
}
