package uk.ac.ebi.eva.metrics.metric;

import uk.ac.ebi.eva.metrics.count.CountServiceParameters;
import uk.ac.ebi.eva.metrics.util.MetricUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClusteringMetricComputeImpl extends MetricComputeImpl {
    private static final String PROCESS = "clustering";
    private final String assembly;

    public ClusteringMetricComputeImpl(CountServiceParameters countServiceParameters, String assembly) {
        super(countServiceParameters);
        this.assembly = assembly;
    }

    public String getProcessName() {
        return PROCESS;
    }

    public List<Metric> getMetrics() {
        return Arrays.stream(ClusteringMetric.values()).collect(Collectors.toList());
    }

    public String getIdentifier() {
        return MetricUtil.createClusteringIdentifier(this.assembly);
    }

    public long getCount(Metric metric){
        return metric.getCount();
    }

    public void addCount(Metric metric, long count) {
        metric.addCount(count);
    }

}
