package uk.ac.ebi.eva.metrics;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.eva.metrics.configuration.MetricConfiguration;
import uk.ac.ebi.eva.metrics.count.CountServiceParameters;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MetricConfigurationTest {

    @Test
    public void countStatsRestTemplateIsPinnedToHttp11() {
        CountServiceParameters parameters = new CountServiceParameters();
        parameters.setUrl("https://localhost");
        parameters.setUserName("user");
        parameters.setPassword("password");

        RestTemplate restTemplate = new MetricConfiguration().restTemplate(new RestTemplateBuilder(), parameters);

        assertInstanceOf(JdkClientHttpRequestFactory.class, restTemplate.getRequestFactory());
        HttpClient httpClient = (HttpClient) ReflectionTestUtils.getField(restTemplate.getRequestFactory(), "httpClient");
        assertNotNull(httpClient);
        assertEquals(HttpClient.Version.HTTP_1_1, httpClient.version(),
                "HTTP/2 must stay disabled: the count service's proxy drops HTTP/2 connections");
    }
}