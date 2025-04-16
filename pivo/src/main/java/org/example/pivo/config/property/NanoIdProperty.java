package org.example.pivo.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "beer-application.nanoid")
public class NanoIdProperty {
    private String alphabet;
    private Integer size;

}
