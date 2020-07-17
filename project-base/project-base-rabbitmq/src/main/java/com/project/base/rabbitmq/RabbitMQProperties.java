package com.project.base.rabbitmq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;


@ConfigurationProperties("spring.rabbitmq")
public class RabbitMQProperties {
    private String host = "localhost";
    private int port = 5672;
    private String username;
    private String password;
    private String virtualHost;
    private boolean disableListenerConverter;
    private List<RabbitMQHost> hosts;
    private final Listener listener = new Listener();
    private final Cache cache = new Cache();

    public RabbitMQProperties() {

    }

    public static class RabbitMQHost {
        private String host;
        private int port;
        private String flag;
        private String username;
        private String password;
        private String virtualHost = "/";
        private boolean disableListenerConverter;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getVirtualHost() {
            return virtualHost;
        }

        public void setVirtualHost(String virtualHost) {
            this.virtualHost = virtualHost;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public boolean getDisableListenerConverter() {
            return disableListenerConverter;
        }

        public void setDisableListenerConverter(boolean disableListenerConverter) {
            this.disableListenerConverter = disableListenerConverter;
        }
    }

    public static class Listener {

        /**
         * Start the container automatically on startup.
         */
        private boolean autoStartup = true;

        /**
         * Acknowledge mode of container.
         */
        private AcknowledgeMode acknowledgeMode;

        /**
         * Minimum number of consumers.
         */
        private Integer concurrency;

        /**
         * Maximum number of consumers.
         */
        private Integer maxConcurrency;

        /**
         * Number of messages to be handled in a single request. It should be greater than
         * or equal to the transaction size (if used).
         */
        private Integer prefetch;

        /**
         * Number of messages to be processed in a transaction. For best results it should
         * be less than or equal to the prefetch count.
         */
        private Integer transactionSize;

        /**
         * Whether rejected deliveries are requeued by default; default true.
         */
        private Boolean defaultRequeueRejected;

        /**
         * How often idle container events should be published in milliseconds.
         */
        private Long idleEventInterval;

        /**
         * Optional properties for a retry interceptor.
         */
        @NestedConfigurationProperty
        private final RabbitProperties.ListenerRetry retry = new RabbitProperties.ListenerRetry();

        public boolean isAutoStartup() {
            return this.autoStartup;
        }

        public void setAutoStartup(boolean autoStartup) {
            this.autoStartup = autoStartup;
        }

        public AcknowledgeMode getAcknowledgeMode() {
            return this.acknowledgeMode;
        }

        public void setAcknowledgeMode(AcknowledgeMode acknowledgeMode) {
            this.acknowledgeMode = acknowledgeMode;
        }

        public Integer getConcurrency() {
            return this.concurrency;
        }

        public void setConcurrency(Integer concurrency) {
            this.concurrency = concurrency;
        }

        public Integer getMaxConcurrency() {
            return this.maxConcurrency;
        }

        public void setMaxConcurrency(Integer maxConcurrency) {
            this.maxConcurrency = maxConcurrency;
        }

        public Integer getPrefetch() {
            return this.prefetch;
        }

        public void setPrefetch(Integer prefetch) {
            this.prefetch = prefetch;
        }

        public Integer getTransactionSize() {
            return this.transactionSize;
        }

        public void setTransactionSize(Integer transactionSize) {
            this.transactionSize = transactionSize;
        }

        public Boolean getDefaultRequeueRejected() {
            return this.defaultRequeueRejected;
        }

        public void setDefaultRequeueRejected(Boolean defaultRequeueRejected) {
            this.defaultRequeueRejected = defaultRequeueRejected;
        }

        public Long getIdleEventInterval() {
            return this.idleEventInterval;
        }

        public void setIdleEventInterval(Long idleEventInterval) {
            this.idleEventInterval = idleEventInterval;
        }

        public RabbitProperties.ListenerRetry getRetry() {
            return this.retry;
        }

    }

    public static class Cache {
        private final Channel channel = new Channel();
        private final Connection connection = new Connection();

        public Channel getChannel() {
            return this.channel;
        }

        public Connection getConnection() {
            return connection;
        }
    }

    public static class Channel {
        private Integer size;
        private Long checkoutTimeout;

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        public Long getCheckoutTimeout() {
            return checkoutTimeout;
        }

        public void setCheckoutTimeout(Long checkoutTimeout) {
            this.checkoutTimeout = checkoutTimeout;
        }
    }

    public static class Connection {

        /**
         * Connection factory cache mode.
         */
        private CachingConnectionFactory.CacheMode mode = CachingConnectionFactory.CacheMode.CHANNEL;

        /**
         * Number of connections to cache. Only applies when mode is CONNECTION.
         */
        private Integer size;

        public CachingConnectionFactory.CacheMode getMode() {
            return this.mode;
        }

        public void setMode(CachingConnectionFactory.CacheMode mode) {
            this.mode = mode;
        }

        public Integer getSize() {
            return this.size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public List<RabbitMQHost> getHosts() {
        return hosts;
    }

    public void setHosts(List<RabbitMQHost> hosts) {
        this.hosts = hosts;
    }

    public boolean getDisableListenerConverter() {
        return disableListenerConverter;
    }

    public void setDisableListenerConverter(boolean disableListenerConverter) {
        this.disableListenerConverter = disableListenerConverter;
    }

    public Listener getListener() {
        return listener;
    }

    public Cache getCache() {
        return cache;
    }


}
