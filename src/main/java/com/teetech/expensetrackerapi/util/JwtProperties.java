package com.teetech.expensetrackerapi.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "application.security.jwt")
@Component
@Setter
@Getter
public class JwtProperties {
    private String secretKey;
    private long accessTokenExpiration; // milliseconds
    private long refreshTokenExpiration; // milliseconds
    private String issuer;
}
