package uk.ac.ebi.eva.metrics.metric;

public interface Metric {

    String getProcessName();

    String getName();

    String getDescription();

    long getCount();

    void addCount(long count);
}
