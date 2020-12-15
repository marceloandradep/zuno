package com.zipwhip.zuno.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("zuno")
public class ZunoProperties {

    private HashId hashId;
    private Jms jms;
    private Zipwhip zipwhip;

    @Data
    public static class HashId {
        private String salt;
    }

    @Data
    public static class Jms {
        private String queue;
    }

    @Data
    public static class Zipwhip {
        private boolean isEnabled;
        private String username;
        private String password;
    }

}
