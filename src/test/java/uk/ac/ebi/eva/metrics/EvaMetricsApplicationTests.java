package uk.ac.ebi.eva.metrics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.eva.metrics.count.CountServiceParameters;
import uk.ac.ebi.eva.metrics.metric.Metric;
import uk.ac.ebi.eva.metrics.metric.BaseMetricCompute;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:metric-test.properties")
public class EvaMetricsApplicationTests {
    @Autowired
    private CountServiceParameters countServiceParameters;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;
    private final String URL_PATH_SAVE_COUNT = "/v1/bulk/count";

    @Before
    public void setup() throws Exception {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect(ExpectedCount.manyTimes(), requestTo(new URI(countServiceParameters.getUrl() + URL_PATH_SAVE_COUNT)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK));
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void testMetricCount() {
        BaseMetricCompute<TestMetric> baseMetricCompute = getMetricCompute();
        baseMetricCompute.saveMetricsCountsInDB();
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
