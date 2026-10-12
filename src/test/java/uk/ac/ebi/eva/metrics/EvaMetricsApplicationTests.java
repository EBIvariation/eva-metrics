package uk.ac.ebi.eva.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.eva.metrics.count.CountServiceParameters;
import uk.ac.ebi.eva.metrics.metric.BaseMetricCompute;
import uk.ac.ebi.eva.metrics.metric.Metric;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource("classpath:metric-test.properties")
public class EvaMetricsApplicationTests {
    @Autowired
    private CountServiceParameters countServiceParameters;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;
    private final String URL_PATH_SAVE_COUNT = "/v1/bulk/count";

    private final List<Long> sleeps = new ArrayList<>();

    @BeforeEach
    public void setup() throws Exception {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect(ExpectedCount.manyTimes(), requestTo(new URI(countServiceParameters.getUrl() + URL_PATH_SAVE_COUNT)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK));
    }

    @Test
    public void contextLoads() {
    }

    private URI countUrl() throws Exception {
        return new URI(countServiceParameters.getUrl() + URL_PATH_SAVE_COUNT);
    }

    @Test
    public void testMetricCount() {
        BaseMetricCompute<TestMetric> baseMetricCompute = getMetricCompute();
        baseMetricCompute.saveMetricsCountsInDB();
    }

    @Test
    public void testRetriesOnGoAwayThenSucceeds() throws Exception {
        mockServer.reset();
        mockServer.expect(ExpectedCount.times(2), requestTo(countUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withException(new IOException("/10.59.5.109:59926: GOAWAY received")));
        mockServer.expect(ExpectedCount.once(), requestTo(countUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK));

        getMetricCompute().saveMetricsCountsInDB();

        mockServer.verify();                                                        // exactly 3 calls were made
        assertEquals(2, sleeps.size());
        assertTrue(sleeps.get(0) >= 1_000 && sleeps.get(0) <= 1_500);   // 1s + up to 50% jitter
        assertTrue(sleeps.get(1) >= 2_000 && sleeps.get(1) <= 3_000);   // backoff doubled
    }

    @Test
    public void testGivesUpAfterMaxAttempts() throws Exception {
        mockServer.reset();
        mockServer.expect(ExpectedCount.times(5), requestTo(countUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withException(new IOException("GOAWAY received")));

        assertThrows(ResourceAccessException.class, () -> getMetricCompute().saveMetricsCountsInDB());

        mockServer.verify();                 // exactly 5 attempts
        assertEquals(4, sleeps.size());      // no sleep after the last attempt
    }

    @Test
    public void testDoesNotRetryOnServerError() throws Exception {
        mockServer.reset();
        mockServer.expect(ExpectedCount.once(), requestTo(countUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        assertThrows(HttpServerErrorException.class, () -> getMetricCompute().saveMetricsCountsInDB());

        mockServer.verify();                 // single call, no retry
        assertTrue(sleeps.isEmpty());
    }

    public BaseMetricCompute<TestMetric> getMetricCompute() {
        return new BaseMetricCompute<TestMetric>(countServiceParameters, restTemplate) {
            @Override
            public String getProcessName() {
                return "test-process";
            }

            @Override
            public List<TestMetric> getMetrics() {
                return Arrays.stream(TestMetric.values()).collect(Collectors.toList());
            }

            @Override
            public String getIdentifier() {
                return "test-identifier";
            }

            @Override
            public long getCount(TestMetric metric) {
                return metric.getCount();
            }

            @Override
            public void addCount(TestMetric metric, long count) {
                metric.addCount(count);
            }

            @Override
            protected void sleepBeforeRetry(long millis) {
                sleeps.add(millis);
            }
        };
    }

    enum TestMetric implements Metric {
        TEST_METRIC_ONE("test_metric_one", "Test Metric One", 0),
        TEST_METRIC_TWO("test_metric_two", "Test Metric Two", 0);

        private String name;
        private String description;
        private long count;

        TestMetric(String name, String description, long count) {
            this.name = name;
            this.description = description;
            this.count = count;
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public long getCount() {
            return this.count;
        }

        public void addCount(long count) {
            this.count += count;
        }

        public void clearCount() {
            this.count = 0;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
