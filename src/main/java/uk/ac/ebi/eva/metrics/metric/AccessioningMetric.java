package uk.ac.ebi.eva.metrics.metric;

public enum AccessioningMetric implements Metric {
    SUBMITTED_VARIANTS("submitted_variants", "Number of variants submitted for accessioning", 0),
    DISCARDED_VARIANTS("discarded_variants", "Number of variants discarded while accessioning", 0);

    private static final String PROCESS = "accessioning_warehouse_ingestion";
    private String name;
    private String description;
    private long count;

    AccessioningMetric(String name, String description, long count) {
        this.name = name;
        this.description = description;
        this.count = count;
    }

    public String getProcessName() {
        return PROCESS;
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

    @Override
    public String toString() {
        return name;
    }
}
