package io.github.panjung99.routify.core.model.entity;

import io.github.panjung99.routify.core.router.metrics.LocalVendorMetrics;
import lombok.Data;

@Data
public class ModelBinding {

    private VendorModel vendorModel;

    private int weight;

    private LocalVendorMetrics metrics;
}
