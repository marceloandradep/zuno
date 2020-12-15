package com.zipwhip.zuno.config;

import org.hashids.Hashids;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public Hashids hashids(ZunoProperties zunoProperties) {
        return new Hashids(zunoProperties.getHashId().getSalt());
    }

}
