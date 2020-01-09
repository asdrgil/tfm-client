package com.tfm.rarawa.tfm.rabbitMQ;

public class SettingsBean {

        String host;
        String virtualHost;
        String username;
        String password;


        public SettingsBean(){}

        public SettingsBean(String host, String virtualHost, String queue, String username, String password){
            this.host = host;
            this.virtualHost = virtualHost;
            this.username = username;
            this.password = password;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }