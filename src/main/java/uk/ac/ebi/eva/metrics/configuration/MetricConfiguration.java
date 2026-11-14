package uk.ac.ebi.eva.metrics.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.eva.metrics.count.CountServiceParameters;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class MetricConfiguration {
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);

    @Bean
    @ConfigurationProperties(prefix = "eva.count-stats")
    public CountServiceParameters countServiceParameters() {
        return new CountServiceParameters();
    }

    @Bean(name="COUNT_STATS_REST_TEMPLATE")
    public RestTemplate restTemplate(RestTemplateBuilder builder, CountServiceParameters countServiceParameters) {
        return builder
                .requestFactory(() -> new JdkClientHttpRequestFactory(http11Client()))
                .basicAuthentication(countServiceParameters.getUserName(), countServiceParameters.getPassword())
                .build();
    }

    static HttpClient http11Client() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(CONNECT_TIMEOUT)
                .build();
    }
}
