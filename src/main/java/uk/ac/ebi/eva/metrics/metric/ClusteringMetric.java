package uk.ac.ebi.eva.metrics.metric;

public enum ClusteringMetric implements Metric {
    SUBMITTED_VARIANTS("submitted_variants", "Number of variants(SS) picked up for clustering", 0),
    CREATED_VARIANTS("created_variants", "Number of Variants Created (RS created)", 0),
    UPDATED_VARIANTS("updated_variants", "Number of Variants Updated (RS updated)", 0),
    MERGED_VARIANTS("merged_variants", "Number of Variants Merged (RS merged)", 0),
    SUBMITTED_VARIANTS_CLUSTERED("submitted_variants_clustered", "Number of Variants(SS) Clustered", 0),
    SUBMITTED_VARIANTS_UNCLUSTERED("submitted_variants_unclustered", "Number of Variants(SS) kept Unclustered", 0),
    SUBMITTED_VARIANTS_RS_MERGED("submitted_variants_rs_merged", "Number of Submitted Variants updated because of RS merged", 0),
    SUBMITTED_VARIANTS_UPDATED_OPERATIONS("submitted_variants_updated", "Number of Submitted Variants update operations performed", 0);

    private String name;
    private String description;
    private long count;

    ClusteringMetric(String name, String description, long count) {
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