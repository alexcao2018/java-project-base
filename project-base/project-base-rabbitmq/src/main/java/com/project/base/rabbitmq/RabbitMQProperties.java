package com.project.base.rabbitmq;

import org.springframework.boot.context.properties.ConfigurationProperties;

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
}
