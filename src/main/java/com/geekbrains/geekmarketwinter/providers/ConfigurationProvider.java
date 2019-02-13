package com.geekbrains.geekmarketwinter.providers;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author stegnin
 */

@Data
@Component
@ConfigurationProperties(prefix = "spring.config")
public class ConfigurationProvider {

    private String fileUploadDirectory;

    private Integer pageSize;

    private Integer expirationDays;

    private Integer maxExpirationDays;

}
