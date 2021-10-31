package uk.ac.ebi.eva.metrics.metric;

import java.util.List;

public interface MetricCompute {
    String getProcessName();

    List<Metric> getMetrics();

    String getIdentifier();

    long getCount(Metric metric);

    void addCount(Metric metric, long count);

    void clearCount(Metric metric);

    void clearCount();

    void saveMetricsCountsInDB();
}
