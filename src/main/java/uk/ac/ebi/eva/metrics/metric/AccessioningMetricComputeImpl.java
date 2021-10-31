package uk.ac.ebi.eva.metrics.metric;

import uk.ac.ebi.eva.metrics.count.CountServiceParameters;
import uk.ac.ebi.eva.metrics.util.MetricUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AccessioningMetricComputeImpl extends MetricComputeImpl {
    private static final String PROCESS = "accessioning_warehouse_ingestion";
    private final String assembly;
    private final String study;

    public AccessioningMetricComputeImpl(CountServiceParameters countServiceParameters,
                                         String assembly, String study) {
        super(countServiceParameters);
        this.assembly = assembly;
        this.study = study;
    }

    public String getProcessName() {
        return PROCESS;
    }

    public List<Metric> getMetrics() {
        return Arrays.stream(AccessioningMetric.values()).collect(Collectors.toList());
    }

    public String getIdentifier() {
        return MetricUtil.createAccessionIdentifier(this.assembly, this.study);
    }

    public long getCount(Metric metric) {
        return metric.getCount();
    }

    public void addCount(Metric metric, long count) {
        metric.addCount(count);
    }

}
