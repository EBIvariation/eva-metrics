package uk.ac.ebi.eva.metrics.metric;

import java.util.List;

public interface MetricCompute<T extends Metric> {
    String getProcessName();

    List<T> getMetrics();

    String getIdentifier();

    long getCount(T metric);

    void addCount(T metric, long count);

    void clearCount(T metric);

    void clearCount();

    void saveMetricsCountsInDB();
}
