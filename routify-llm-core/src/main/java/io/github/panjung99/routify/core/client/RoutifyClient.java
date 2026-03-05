package io.github.panjung99.routify.core.client;

import io.github.panjung99.routify.core.adapter.ChatAdapter;
import io.github.panjung99.routify.core.apikey.ApiKeyRoutingStrategy;
import io.github.panjung99.routify.core.apikey.strategy.RoundRobinApiKeyRoutingStrategy;
import io.github.panjung99.routify.core.model.dto.RoutifyRequest;
import io.github.panjung99.routify.core.model.dto.RoutifyResponse;
import io.github.panjung99.routify.core.model.entity.*;
import io.github.panjung99.routify.core.model.enums.ModelCategory;
import io.github.panjung99.routify.core.model.enums.VendorType;
import io.github.panjung99.routify.core.repository.ConfigRepository;
import io.github.panjung99.routify.core.router.VendorRoutingStrategy;
import io.github.panjung99.routify.core.router.metrics.DefaultVendorMetricsRecorder;
import io.github.panjung99.routify.core.router.metrics.LocalVendorMetrics;
import io.github.panjung99.routify.core.router.metrics.MetricsStorageManager;
import io.github.panjung99.routify.core.router.strategy.PerformanceFirstVendorRoutingStrategy;
import lombok.Builder;

import java.util.List;
import java.util.Optional;

@Builder
public class RoutifyClient {

    private ConfigRepository configRepository;

    @Builder.Default
    private VendorRoutingStrategy routingStrategy = new PerformanceFirstVendorRoutingStrategy();

    @Builder.Default
    private MetricsStorageManager metricsStorageManager = new MetricsStorageManager();

    @Builder.Default
    private DefaultVendorMetricsRecorder metricsRecorder = new DefaultVendorMetricsRecorder(0.3);

    @Builder.Default
    private ApiKeyRoutingStrategy apiKeyRoutingStrategy = new RoundRobinApiKeyRoutingStrategy();

    /**
     * 阻塞式非流式调用，返回完整响应。
     *
     * @param request 请求参数
     * @return 模型响应
     */
    public RoutifyResponse chat(RoutifyRequest request) {
        RoutingContext context = new RoutingContext();
        context.setMetricsManager(metricsStorageManager);

        String modelName = request.getModel();

        if (configRepository == null) {
            throw new RuntimeException("");
        }
        Optional<LogicModel> logicModel = configRepository.getModel(modelName);
        if (!logicModel.isPresent()) {
            // 404
            return RoutifyResponse.error("MODEL_NOT_FOUND", "The model does not exist.");
        }
        context.setModel(logicModel.get());

        List<VendorModel> vendorModels = configRepository.getVendorModels(logicModel.get().getName());
        if (vendorModels == null || vendorModels.isEmpty()) {
            return RoutifyResponse.error("TODO", "");
        }
        context.setVendorModels(vendorModels);
        // TODO 校验数据合法性

        if (!ModelCategory.chat.equals(logicModel.get().getCategory())) {
            return RoutifyResponse.error("UNSUPPORTED_MODEL", "The model is not a chat model.");
        }

        VendorModel vendorModel = routingStrategy.route(context);
        if (vendorModel == null) {
            // TODO fallback
            return null;
        }
        Vendor vendor = configRepository.getVendor(vendorModel.getVendorId())
                .orElseThrow(() -> new RuntimeException(""));
        List<ApiKey> apiKeys = configRepository.getApiKeysByVendor(vendor.getVendorId());

        ApiKey apiKey = apiKeyRoutingStrategy.route(apiKeys, vendorModel.getVendorModelId());


        VendorType vendorType = vendor.getVendorType();
        ChatAdapter chatAdapter = ChatAdapter.getAdapter(vendorType);

        long start = System.currentTimeMillis();
        boolean success = false;
        LocalVendorMetrics metrics = metricsStorageManager.getOrCreate(vendorModel.getVendorModelId());
        metricsRecorder.incrementConcurrency(metrics);
        try {
            RoutifyResponse response = chatAdapter.chat(request, vendorModel, vendor, apiKey, context);
            success = true; //TODO 此处是否成功需要写逻辑判断 或者失败则由子类抛异常
            return response;
        } catch (Exception e) {
            return RoutifyResponse.error("VENDOR_ERROR", e.getMessage());
        } finally {
            long latency = System.currentTimeMillis() - start;
            metricsRecorder.record(metrics, latency, success);
            metricsRecorder.decrementConcurrency(metrics);
        }

    }

    /**
     * 阻塞式流式调用，通过回调处理器接收数据块。
     *
     * @param request 请求参数
     * @param handler 回调处理器，用于接收流式数据块、错误和完成事件
     */
    public void chatStream(RoutifyRequest request, StreamResponseHandler handler) {

    }
}
