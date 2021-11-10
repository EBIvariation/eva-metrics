package uk.ac.ebi.eva.metrics.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.eva.metrics.count.CountServiceParameters;

@Configuration
public class MetricConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "eva.count-stats")
    public CountServiceParameters countServiceParameters() {
        return new CountServiceParameters();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, CountServiceParameters countServiceParameters) {
        return builder.basicAuthentication(countServiceParameters.getUserName(), countServiceParameters.getPassword()).build();
    }
}
