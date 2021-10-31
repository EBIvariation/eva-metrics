package uk.ac.ebi.eva.metrics.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.eva.metrics.count.CountServiceParameters;

@Configuration
public class MetricConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "eva.count-stats")
    public CountServiceParameters countServiceParameters() {
        return new CountServiceParameters();
    }
}
