package uk.ac.ebi.eva.metrics.metric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.eva.metrics.count.Count;
import uk.ac.ebi.eva.metrics.count.CountServiceParameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class BaseMetricCompute<T extends Metric> implements MetricCompute<T> {
    private static final Logger logger = LoggerFactory.getLogger(uk.ac.ebi.eva.metrics.metric.MetricCompute.class);
    private static final String URL_PATH_SAVE_COUNT = "/v1/bulk/count";

    private static final int MAX_ATTEMPTS = 5;
    private static final long INITIAL_BACKOFF_MS = 1_000;
    private static final long MAX_BACKOFF_MS = 30_000;

    private CountServiceParameters countServiceParameters;
    private RestTemplate restTemplate;

    public BaseMetricCompute(CountServiceParameters countServiceParameters, RestTemplate restTemplate) {
        this.countServiceParameters = countServiceParameters;
        this.restTemplate = restTemplate;
    }

    public void clearCount(T metric) {
        metric.clearCount();
    }

    public void clearCount() {
        for (T metric : getMetrics()) {
            metric.clearCount();
        }
    }

    public void saveMetricsCountsInDB() {
        String processName = getProcessName();
        String identifier = getIdentifier();

        List<Count> counts = new ArrayList<>();
        for (T metric : getMetrics()) {
            Count count = new Count(processName, identifier, metric.getName(), metric.getCount());
            counts.add(count);
            logger.info(count.toString());
        }

        HttpHeaders headers;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String url = countServiceParameters.getUrl() + URL_PATH_SAVE_COUNT;
        HttpEntity<Object> requestEntity = new HttpEntity<>(counts, headers);
        ResponseEntity<List<Count>> response = postCountsWithRetry(url, requestEntity);

        if (response.getStatusCode() == HttpStatus.OK) {
            logger.info("Metric Count successfully saved In DB");
        } else {
            throw new RestClientException("Could not save count In DB. HttpStatus code is " + response.getStatusCode());
        }
    }

    private ResponseEntity<List<Count>> postCountsWithRetry(String url, HttpEntity<Object> requestEntity) {
        long backoffMs = INITIAL_BACKOFF_MS;
        for (int attempt = 1; ; attempt++) {
            try {
                return restTemplate.exchange(url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {});
            } catch (ResourceAccessException ex) {
                if (attempt >= MAX_ATTEMPTS) {
                    logger.error("Saving counts failed after {} attempts", attempt, ex);
                    throw ex;
                }
                long sleepMs = backoffMs + ThreadLocalRandom.current().nextLong(backoffMs / 2 + 1);
                logger.warn("Saving counts failed (attempt {}/{}): {}. Retrying in {} ms",
                        attempt, MAX_ATTEMPTS, ex.getMessage(), sleepMs);
                try {
                    sleepBeforeRetry(sleepMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw ex;
                }
                backoffMs = Math.min(backoffMs * 2, MAX_BACKOFF_MS);
            }
        }
    }

    protected void sleepBeforeRetry(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}
