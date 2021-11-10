package uk.ac.ebi.eva.metrics.metric;

public interface Metric {
    String getName();

    String getDescription();

    long getCount();

    void addCount(long count);

    void clearCount();
}
