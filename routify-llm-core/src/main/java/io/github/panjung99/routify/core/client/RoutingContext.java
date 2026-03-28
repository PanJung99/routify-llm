package io.github.panjung99.routify.core.client;

import io.github.panjung99.routify.core.model.entity.LogicModel;
import io.github.panjung99.routify.core.model.entity.VendorModel;
import io.github.panjung99.routify.core.repository.ConfigRepository;
import io.github.panjung99.routify.core.router.metrics.MetricsStorageManager;
import lombok.Data;

import java.util.List;


@Data
public class RoutingContext {

    private MetricsStorageManager metricsManager;

    private ConfigRepository configRepository;

    private LogicModel model;

    private List<VendorModel> vendorModels;

}
